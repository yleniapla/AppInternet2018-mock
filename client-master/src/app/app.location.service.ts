import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, shareReplay } from 'rxjs/operators';

@Injectable({
	providedIn: 'root',
})

// Service for interactions with server on Archives and Positions

export class LocationService {

	constructor(private http: HttpClient) { }

	buyPositions(ids) {

		// buy archives from a list of ids

		const headers = new HttpHeaders()
            .set('Content-Type', 'application/json');

        let body = JSON.stringify(ids);
        console.dir(body);

        let req = this.http.post('http://localhost:8080/users/archives/buy', body, {headers: headers}).pipe(
            shareReplay()
        );

        return req;
	}

	getArchives(json) {

		// get list of archives that match the request

		const headers = new HttpHeaders()
            .set('Content-Type', 'application/json');

		let body = JSON.stringify(json);
		console.dir(body);

		let req = this.http.post('http://localhost:8080/users/archives/search', body, {headers: headers});

        return req;

	}

	getUsers(){

		// get list of users

		let req =  this.http.get('http://localhost:8080/users');

		return req;

	}

	getOwnArchives() {

		// get list of owned archives

		let req = this.http.get('http://localhost:8080/users/archives');

		return req;
	}

	getBoughtArchives() {

		// get list of bought archives

		let req = this.http.get('http://localhost:8080/users/bought');

		return req;
	}

	getArchive(id:string) {

		// get single archive

		let req = this.http.get('http://localhost:8080/users/archives/'+id);
		return req;
	}

	removeArchive(id:string) {

		// remove single archive

		let req = this.http.delete('http://localhost:8080/users/archives/'+id);
		return req;
	}

	showArchivesToBuy(json){

		// request list of archives that the user is going to buy

		const headers = new HttpHeaders()
            .set('Content-Type', 'application/json');

		let body = JSON.stringify(json);
		console.dir(body);

		let req = this.http.post('http://localhost:8080/users/archives/searchbuy', body, {headers: headers}).pipe(
            shareReplay()
        );

        return req;
	}

}
