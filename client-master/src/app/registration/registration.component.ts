import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../app.registration.service';
import { LoginService } from '../app.login.service';
import { Router } from '@angular/router';

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  errorMsg;
  cgErrorMsg = (evt) => {this.errorMsg = evt;};

  constructor(private registrationService : RegistrationService, private loginService : LoginService, private http: HttpClient,  private router: Router) { }

  ngOnInit() {

    if (this.loginService.isLoggedIn()) {
      this.router.navigate(['user']);
    }

  }

  doRegistration(usr,psw) {

    if(usr== '' || psw == '')
    {
        this.errorMsg = 'Insert all the requested fields';
    }  
    else{

      this.registrationService.registration(usr, psw).subscribe(
      data => {
        this.loginService.login(usr, psw).subscribe(data => this.loginService.redirect())
      },
      data => this.errorMsg = data.error.message
    )

    }

  	

  }

}
