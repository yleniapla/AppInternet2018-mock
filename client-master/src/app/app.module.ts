import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { LocationService } from './app.location.service';
import { RoleGuardService as RoleGuard } from './app.roleguard.service';
import { LoginService } from './app.login.service';

import { AuthInterceptor } from './app.login.interceptor'

import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { LeafletDrawModule } from '@asymmetrik/ngx-leaflet-draw';

import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DlDateTimePickerDateModule } from 'angular-bootstrap-datetimepicker';

import { RouterModule, Routes } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { FileUploadModule } from 'ng2-file-upload';

import { NgxChartsModule } from '@swimlane/ngx-charts';

import { MultiplyPipe } from './multiply.pipe';

import {
  MatButtonModule,
  MatCheckboxModule,
  MatFormFieldModule,
  MatRadioModule,
  MatInputModule,
  MatRippleModule,
  MatNativeDateModule,
  MatDialogModule
} from '@angular/material';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { LocationComponent, DialogContentExampleDialog } from './location/location.component';
import { LoginComponent } from './login/login.component';
import { UserComponent } from './user/user.component';
import { RegistrationComponent } from './registration/registration.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ErrorComponent } from './error/error.component';
import { MenuComponent } from './menu/menu.component';
import { LogoutComponent } from './logout/logout.component';

const appRoutes: Routes = [
  { path: 'buy', component: LocationComponent, canActivate: [RoleGuard]},
  { path: 'user', component: UserComponent, canActivate: [RoleGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent},
  { path: 'logout', component: LogoutComponent},
  { path: '**', redirectTo: '/user', pathMatch: 'full' }
];


@NgModule({
  declarations: [
    AppComponent,
    LocationComponent,
    LoginComponent,
    UserComponent,
    RegistrationComponent,
    DialogContentExampleDialog,
    ErrorComponent,
    MenuComponent,
    LogoutComponent,
    MultiplyPipe
  ],
  imports: [
    BrowserModule,
    NgbModule.forRoot(),
    LeafletModule.forRoot(),
    LeafletDrawModule.forRoot(),
    BrowserAnimationsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule, MatInputModule, MatRippleModule, MatNativeDateModule, MatRadioModule,
    MatDatepickerModule,
    ReactiveFormsModule,
    FormsModule,
    DlDateTimePickerDateModule,
    MatSelectModule,
    MatDialogModule,
    MatTableModule,
    NgxChartsModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true }
    ),
    HttpClientModule,
    FileUploadModule
  ],
  exports: [
    BrowserAnimationsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatFormFieldModule, MatInputModule, MatRippleModule, MatNativeDateModule, MatRadioModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    FileUploadModule
  ],
  entryComponents: [
      DialogContentExampleDialog
  ],
  providers: [
    LocationService, FormsModule, LoginService,
    {
        provide: HTTP_INTERCEPTORS,
        useClass: AuthInterceptor,
        multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
