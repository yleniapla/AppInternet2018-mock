import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { LoginService } from '../app.login.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

	menuMap = new Map();
	show = true;
	menu;
    loggedUser;

	constructor(private router: Router, private LoginService : LoginService,) {
		this.menuMap.set("/user", ['buy', 'logout']);
		this.menuMap.set("/buy", ['user', 'logout']);
		this.menuMap.set("/registration", ['login']);
	}

	ngOnInit() {

		// Subscribe on router events to change menu items
		this.router.events.subscribe( (event) => {
			if (event instanceof NavigationEnd ) {
				// Menu not visibile on login
				if (event.urlAfterRedirects == '/login')
				  	this.show = false;
				else {
				  	this.show = true;
				  	this.menu = this.menuMap.get(event.urlAfterRedirects);
                    this.loggedUser = this.LoginService.getUserName();
				}
			}
		});

	}

}
