import { Component, OnInit } from '@angular/core';
import { LoginService } from '../app.login.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { tap, shareReplay } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  isLogged;
  errorMsg;
  cgErrorMsg = (evt) => {this.errorMsg = evt;}; 

  constructor(private loginService : LoginService, private http: HttpClient, private router: Router) { }

  ngOnInit() {

    if (this.loginService.isLoggedIn()) {
      this.router.navigate(['user']);
    }

  	this.isLogged = this.loginService.isLoggedIn();
  }

  doLogin(usr,psw) {

    if(usr== '' || psw == '')
    {
        this.errorMsg = 'Insert all the requested fields';
    }	
    else
    {
      this.loginService.login(usr, psw).subscribe(
      data => {
            this.loginService.redirect();
      },
        error => {
            this.errorMsg = error.error.error_description;
        });
    }
    

  }

  doLogout() {
  	this.loginService.logout();
  	this.isLogged = this.loginService.isLoggedIn();
  }

  // TODO serve?
  doGet() {
  	let req = this.http.get('http://localhost:8080/users')
  		.subscribe(
            res => {console.dir(res)},
            error => {this.errorMsg = error.error.message;}
        );
  }

}
