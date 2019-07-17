import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { LocationService } from '../app.location.service';
import { LoginService } from '../app.login.service';
import { FileUploader, FileItem, ParsedResponseHeaders } from 'ng2-file-upload';
import { DomSanitizer } from '@angular/platform-browser';

const URL = 'http://localhost:8080/users/archives';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})

// Component for upload and download of user Archives

export class UserComponent implements OnInit {

    loggedUser;
    ownArchives;
    boughtArchives$;

    errorMsg;
	cgErrorMsg = (evt) => {this.errorMsg = evt;};

    constructor(
        private locationService : LocationService,
        private LoginService : LoginService,
        private sanitizer: DomSanitizer
    ) { }


    public uploader:FileUploader = new FileUploader({url: URL, authToken: "Bearer " + localStorage.getItem("id_token")});
    public hasBaseDropZoneOver:boolean = false;

    public fileOverBase(e:any):void {
        this.hasBaseDropZoneOver = e;
    }

    ngOnInit() {
        this.loggedUser = this.LoginService.getUserName();

        this.uploader.onErrorItem = (item, response, status, headers) => this.onErrorItem(item, response, status, headers);
        this.uploader.onSuccessItem = (item, response, status, headers) => this.onSuccessItem(item, response, status, headers);

        this.locationService.getOwnArchives().subscribe( data => this.ownArchives = data );
        this.boughtArchives$ = this.locationService.getBoughtArchives();
    }

    public requestArchive(id:string) {
        this.locationService.getArchive(id).subscribe(
            data => this.generateDownloadJsonUri(data),
            error => this.errorMsg=error.message
        );
    }

    public removeArchive(id:string) {
        this.locationService.removeArchive(id).subscribe(
            ok => this.locationService.getOwnArchives().subscribe( data => this.ownArchives = data ),
            error => this.errorMsg=error.message
        );
    }

    // Callback on upload
    onSuccessItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
        this.locationService.getOwnArchives().subscribe( data => this.ownArchives = data );
    }

    // Callback on error in upload
    onErrorItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
        let error = JSON.parse(response);
        this.errorMsg=error.message;
    }

    // Generate URL for download
    generateDownloadJsonUri(myJson) {
        let sJson = JSON.stringify(myJson);
        let element = document.createElement('a');
        element.setAttribute('href', "data:text/json;charset=UTF-8," + encodeURIComponent(sJson));
        element.setAttribute('download', "archive.json");
        element.style.display = 'none';
        document.body.appendChild(element);
        element.click(); // simulate click
        document.body.removeChild(element);
    }
}
