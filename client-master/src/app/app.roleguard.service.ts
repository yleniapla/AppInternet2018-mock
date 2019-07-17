import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot } from '@angular/router';
import { LoginService } from './app.login.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService implements CanActivate {

  constructor(public auth: LoginService, public router: Router) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {

      if (!this.auth.isLoggedIn()) {
          this.router.navigate(['login']);
          return false;
      }

      return true;
  }
  
}
