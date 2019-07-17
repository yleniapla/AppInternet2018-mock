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
export class LoginService {

    user;

    constructor(private http: HttpClient, private router: Router) {

    }

    login(username:string, password:string ) {
        localStorage.removeItem("id_token");
        localStorage.removeItem("expires_at");

        let user = new User(username, password);

        const headers = new HttpHeaders()
            .set('Content-Type', 'application/x-www-form-urlencoded')
            .set('authorization', user.authorization)

        let body = new HttpParams();
        body = body.set('username', user.user)
                .set('password', user.pass)
                .set('client_id', user.clientid)
                .set('grant_type', user.granttype);


        let req = this.http.post('http://localhost:8080/oauth/token', body, {headers: headers}).pipe(
                tap(res => this.setSession(res, user))
            );

        return req;
    }

    private setSession(authResult, user) {
        // insert user params into browser Local Storage
        const expiresAt = moment().add(authResult.expires_in,'second');

        localStorage.setItem('id_token', authResult.access_token);
        localStorage.setItem("expires_at", JSON.stringify(expiresAt.valueOf()) );
        localStorage.setItem("username", user.user);
    }

    logout() {
        localStorage.removeItem("id_token");
        localStorage.removeItem("expires_at");
        localStorage.removeItem("username");
    }

    public isLoggedIn() {
        return moment().isBefore(this.getExpiration());
    }

    isLoggedOut() {
        return !this.isLoggedIn();
    }

    getExpiration() {
        const expiration = localStorage.getItem("expires_at");
        const expiresAt = JSON.parse(expiration);
        return moment(expiresAt);
    }

    getUserName() {
        const username = localStorage.getItem("username");
        return username;
    }

    getToken() {
        return localStorage.getItem("id_token");
    }

    getTokenPayload() {
        let token = this.getToken();
        console.dir(token);
        if(token)
            return decode(this.getToken());
        else
            return null;
    }

    public getRoles() {
        let token = this.getTokenPayload();
        console.dir(token);
        if(token)
            return token.authorities;
        else
            return null;
    }

    public redirect() {

        let roles = this.getRoles();
        if (roles == null){
            this.router.navigate(['login']);
            return;
        }
        else{
            if (roles.indexOf('ROLE_USER') != -1 || roles.indexOf('ROLE_ADMIN') != -1 ){
                this.router.navigate(['user']);
                return;
            }
            this.router.navigate(['customer']);
            return;
        }

    }
}
