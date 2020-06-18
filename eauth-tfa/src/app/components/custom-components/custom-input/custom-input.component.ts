import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AuthenticationAttribute } from 'src/app/model/authenticationAttribute';
import { AttributeType } from 'src/app/model/enums/attributeType';
import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'app-custom-input',
	templateUrl: './custom-input.component.html',
	styleUrls: ['./custom-input.component.scss']
})
export class CustomInputComponent implements OnInit {
	@Output()
	public onInputChange: EventEmitter<any> = new EventEmitter();

	@Input()
	public attribute: AuthenticationAttribute;

	public value = '';

	public get attributeId() {
		return '__' + this.attribute.id + '__';
	}

	public get authAttrType() {
		return this.attribute.type;
	}

	public get lang() {
		return this._translate.currentLang;
	}

	public get label() {
		return this.attribute.label;
	}

	constructor(private _translate: TranslateService) {}

	ngOnInit() {
		console.log(this.attribute.type);
		console.log(this._translate.currentLang);
	}

	public getAuthAttributeType() {
		if (this.authAttrType === AttributeType.SECRET) {
			return 'password';
		}
	}

	public inputChange() {
		this.onInputChange.emit({
			value: this.value,
			id: this.attribute.id
		});
	}
}
