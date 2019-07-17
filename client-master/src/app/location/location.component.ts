import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';

import { FormControl, ReactiveFormsModule, NgForm } from '@angular/forms';
import { LocationService } from '../app.location.service'
import { LoginService } from '../app.login.service';

import { LeafletModule, LeafletDirective } from '@asymmetrik/ngx-leaflet';
import { LeafletDrawModule } from '@asymmetrik/ngx-leaflet-draw';
import { latLng, LatLng, tileLayer, marker } from 'leaflet';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import * as L from 'leaflet';
import * as moment from 'moment';

import { MatDialog } from '@angular/material';

@Component({
	selector: 'app-location',
	templateUrl: './location.component.html',
	styleUrls: ['./location.component.css']
})
export class LocationComponent implements OnInit {

	points = [];
	timestamps = [];
	retrievedUsers;
	map;
	locations;
	options;
	drawOptions;
	layers;
	markerIcon;
	bounds;
	geoJSON;
	loggedUser;
	users;
	usersSelected;
	archiveLayer;
	userColorMap;
	numRetrievedPositions = 0;
	colorMap = ["#b2182b","#d6604d","#f4a582","#fddbc7","#f7f7f7","#d1e5f0","#92c5de","#4393c3","#2166ac"];

	startTimeH = 0;
	startTimeM = 0;
	endTimeH = 23;
	endTimeM = 59;

	buyButton = false;
	requestBuy = null;

	yScaleMax;
	yScaleMin;
	xScaleMax;
	xScaleMin;
	yAxis;
	xAxis;
	xAxisTickFormatting;
	yAxisTickFormatting;
	tooltipDisabled;
	customColors;
	colorScheme;

	dateStart = new FormControl(new Date());
	dateEnd = new FormControl(new Date());
	toppings = new FormControl();

	errorMsg;
	cgErrorMsg = (evt) => {this.errorMsg = evt;};


	constructor(private locationService : LocationService, private LoginService : LoginService, public dialog: MatDialog, private zone:NgZone) {

		this.options = {
			layers: [
				tileLayer('http://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {
					maxZoom: 20,
					subdomains: ['mt0', 'mt1', 'mt2', 'mt3'],
					detectRetina: true
				})
			],
			zoom: 12,
			center: latLng([ 45.0558182,7.6579988 ])
		};

		this.drawOptions = {
			position: 'topright',
			draw: {
				marker: {
					 icon: this.markerIcon
				},
				polyline: false,
				circle: {
					shapeOptions: {
						color: '#aaaaaa'
					}
				}
			}
		};

		this.markerIcon = L.icon({
			iconSize: [ 25, 41 ],
			iconAnchor: [ 13, 41 ],
			iconUrl: './assets/marker-icon.png',
			shadowUrl: './assets/marker-shadow.png'
		});

	}

	ngOnInit() {

		// retrieve list of user and set the properties timeline

		this.loggedUser = this.LoginService.getUserName();
		this.locationService.getUsers().subscribe(
			(data) => {
				this.users = data;
				this.users = this.users.filter(user => { return user != this.loggedUser; });
			},
			(error) => { this.errorMsg = 'cannot retrieve users';}
		)

		this.layers = [];
		this.userColorMap = new Map();

		this.yScaleMax = 1;
		this.yScaleMin = 1;
		this.xScaleMax = undefined;
		this.xScaleMin = undefined;
		this.yAxis = true;
		this.xAxis = true;
		this.tooltipDisabled = true;
		this.colorScheme = {domain: this.colorMap};
		this.customColors =[];

		this.xAxisTickFormatting = (timestamp) => { return moment(new Date(timestamp*1000)).format('DD/MM/YYYY H:m'); };
		this.yAxisTickFormatting = (timestamp) => { return undefined; };

		this.timestamps = [];

	}

	addPositionsToMap(){

		// add points to map

		this.map.removeLayer(this.archiveLayer);
		this.archiveLayer = L.layerGroup();

		for (let point of this.points){

			let usr = point.user;
			let color;

			let lat = point.coordinates[1];
			let lng = point.coordinates[0];
			let latlng = L.latLng(lat,lng);
			let m = L.circle(latlng, 100, { color : point.color });
			m.addTo(this.archiveLayer);
		}

		this.archiveLayer.addTo(this.map);
	}

	getArchives() : void {

		// get the list of archives specifying bounds, users and time

		// return the bounds of the map, do not consider drawn polygon
		let req = this.getReqForSearch(false);

		this.locationService.getArchives(req).subscribe(
			(data) => {

				let positions = data['positions'];
				let timestamps = data['timestamps'];

				this.points = [];

				for (const p of positions){

					let color;
					let user = p.username;
					let coordinates = p.point.coordinates;

					if ( this.userColorMap.has(user)){
						color = this.userColorMap.get(user);
					}
					else{
						color = this.colorMap[this.userColorMap.size % this.colorMap.length];
						this.userColorMap.set(user, color);
					}

					this.points.push({'user' : user,'color' : color,'coordinates' : coordinates});

				}

				let tmp = new Map();
				let mint=Infinity, maxt=0;

				for (let t of timestamps){
					let user = t.username;
					let color;

					if (this.userColorMap.has(user))
						color = this.userColorMap.get(user);
					else
						color = this.colorMap[this.userColorMap.size % this.colorMap.length];
						this.userColorMap.set(user, color);

					if (!tmp.has(user))
						tmp.set(user,{series : [], name : user})
					tmp.get(user).series.push({ name : "pos", x : t.timestamp, y : 1, r : 200});
					if (t.timestamp < mint) mint = t.timestamp;
					if (t.timestamp > maxt) maxt = t.timestamp;
				}

				this.xScaleMax = maxt;
				this.xScaleMin = mint;

				this.timestamps = Array.from(tmp.values());
				console.log(this.timestamps);


				this.zone.run( () => {
					this.retrievedUsers = new Set(positions.map(a => a.username));
					this.numRetrievedPositions = positions.length + timestamps.length;
				});


				this.addPositionsToMap();
				console.log(this.userColorMap);

			},
			(error) => { this.errorMsg = error.error.message; }
		);

	}

	getPolygon(coords) {

		// return a polygon from a set of coordinates

		let pol = {
    		"type":"Polygon",
    		"coordinates":
    			[ coords ]
    	};

		return pol;
	}

	onMapReady(map: L.Map) {

		// function called when the map is ready.
		// used to register callbacks

		this.map = map;

		this.archiveLayer = L.layerGroup();

		this.map.on('draw:created', (e) => {
			let l = e.layer;
			if (l instanceof L.Polygon){
				this.geoJSON = l.toGeoJSON();
				this.zone.run( () => { this.buyButton = true; } );
				console.log("Polygon created");
			}
			else
				this.errorMsg = "Not supported, use a polygon";
		});

		this.map.on('draw:edited', (e) => {
			let ls = e.layers;
			ls.eachLayer(function (l) {
	        	if (l instanceof L.Polygon){
					this.geoJSON = l.toGeoJSON();
					console.log("Polygon edited");
				} else
					this.errorMsg = "Not supported, use a polygon";
	        });

		});

		this.map.on('draw:deletestop', (e) => {
			this.geoJSON = undefined;
			console.log("Polygon deleted");
			this.buyButton = false;
		});

		this.map.on('moveend', () => {
	    	this.getArchives();
		});

	}

	timestamp(time) {
		return moment.unix(time);
	}

	discard() : void {

		// reset map if archives are discarded

		this.points = [];
		this.buyButton = false;
		this.requestBuy = null;

		let self = this;

		this.map.eachLayer( function(l){
			if (l instanceof L.Polygon)
				self.map.removeLayer(l);
		});
	}

	buy(ids) : void {

		// buy archives

		this.locationService.buyPositions({ids})
			.subscribe(
	            (data) => {
					alert("Positions Bought");
					this.points = [];
					this.buyButton = false;
					this.requestBuy = null;
					this.cleanMap();
				}, (error) => {
					this.errorMsg = error.error.message;
				}
			);
	}

	cleanMap() {

		// clean map by removing layers (positions)

		let self = this;
		this.map.eachLayer( function(l){
			if (l instanceof L.Polygon)
				self.map.removeLayer(l);
		});
	}

	openDialog() {

		// open the dialog that shows the archives that are requested for buying

		let req = this.getReqForSearch(true);

		let archives$ = this.locationService.showArchivesToBuy(req);

	    const dialogRef = this.dialog.open(DialogContentExampleDialog, {
	      height: '350px',
		  width: '600px',
		  data: { archives$ : archives$}
	    });

	    dialogRef.afterClosed().subscribe(
			result => {
		    	if (result){

					let archives = dialogRef.componentInstance.archives$;

					// filter archives
					archives = archives.filter( (a) => { return a.bought == 0;} );
					// retrieve list of ids
					let ids = archives.map(a => a.id);

					if (ids.length > 0)
						this.buy(ids);
				}
		    },
			(error) => {this.errorMsg = error.error.message;});
  	}

	getReqForSearch(fromSelection : boolean) {

		// build the request object used to search and buy archives

		let coords = [];
		if (fromSelection)
			coords = this.geoJSON.geometry.coordinates[0];
		else{
			let mapBounds = this.map.getBounds().toBBoxString();
			coords = [
				[this.map.getBounds().getNorthWest()['lng'], this.map.getBounds().getNorthWest()['lat']],
				[this.map.getBounds().getSouthWest()['lng'], this.map.getBounds().getSouthWest()['lat']],
				[this.map.getBounds().getSouthEast()['lng'], this.map.getBounds().getSouthEast()['lat']],
				[this.map.getBounds().getNorthEast()['lng'], this.map.getBounds().getNorthEast()['lat']],
				[this.map.getBounds().getNorthWest()['lng'], this.map.getBounds().getNorthWest()['lat']]
			];
		}

		let start = this.dateStart.value;
		let end = this.dateEnd.value;
		start.setHours(this.startTimeH);
		start.setMinutes(this.startTimeM);
		start.setSeconds(0);
		end.setHours(this.endTimeH);
		end.setMinutes(this.endTimeH);
		end.setSeconds(59);

		// getTime returns timestamp in ms, convert it to seconds
		start = Math.ceil(start.getTime()/1000);
		end = Math.ceil(end.getTime()/1000);

		// handle no user selection
		let usersSelected;
		if(this.toppings.value == null)
			usersSelected = [];
		else
			usersSelected = this.toppings.value;

		let req = {
			start : start,
			end : end,
			polygon : this.getPolygon(coords),
			usernames : usersSelected
		};

		return req;
	}


}

// Confirmation dialog for showing the shopping list

import {Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';

@Component({
	selector: 'dialog-content-example-dialog',
	templateUrl: 'dialog-content-example-dialog.html',
	styleUrls: ['dialog-content-example-dialog.css']
})
export class DialogContentExampleDialog {

	archives$;
	displayedColumns: string[] = ['user', 'start', 'end', 'bought'];

	constructor(@Inject(MAT_DIALOG_DATA) public data: any) {

		data.archives$.subscribe(
			(data) => {
				this.archives$ = data;
				this.archives$.map( a => { a.start *=1000; a.end *= 1000;  } );
			 }
		);
	}

}
