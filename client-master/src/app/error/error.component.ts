import { Component, OnChanges, Input, Output, SimpleChange, EventEmitter } from '@angular/core';

@Component({
    selector: 'app-error',
    templateUrl: './error.component.html',
    styleUrls: ['./error.component.css']
})

// Dummy component for error handling
export class ErrorComponent implements OnChanges {

    constructor() { }

    @Input()
    errorMessage = undefined;

    @Output()
    changeErrorMessage = new EventEmitter();

    ngOnChanges(change){

        setTimeout( () => {
            this.changeErrorMessage.emit(undefined);
        }, 5000);

    }

}
