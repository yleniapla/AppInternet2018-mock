import { Injectable } from '@angular/core';
import * as moment from 'moment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { User } from './user.class';
import { Observable } from 'rxjs';
import { tap, shareReplay } from 'rxjs/operators';
import decode from 'jwt-decode';
import { Router, CanActivate, ActivatedRouteSnapshot } from '@angular/router';


@Injectable({
    providedIn: 'root',
})
export class RegistrationService {

    user;

    constructor(private http: HttpClient, private router: Router) {

    }

    registration(username:string, password:string) {

        localStorage.removeItem("id_token");
        localStorage.removeItem("expires_at");

        let user = new User(username, password);

        const headers = new HttpHeaders()
            .set('Content-Type', 'application/json')

        let body = JSON.stringify({
            'username' : user.user,
            'password' : user.pass
        });

        let req = this.http.post('http://localhost:8080/registration', body, {headers: headers});

        return req;


    }

}
