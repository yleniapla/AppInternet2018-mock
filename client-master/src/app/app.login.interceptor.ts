import { Injectable } from '@angular/core';
import { Observable } from 'rxjs'; 
import {   HttpRequest,  HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    intercept(req: HttpRequest<any>,
              next: HttpHandler): Observable<HttpEvent<any>> {

        const idToken = localStorage.getItem("id_token");

        // Insert JWT for all requests, except login and registration

        if (idToken && !req.headers.has("authorization")) {
            const cloned = req.clone({
                headers: req.headers.set("Authorization",
                    "Bearer " + idToken)
            });

            return next.handle(cloned);
        }
        else {
            return next.handle(req);
        }
    }
}