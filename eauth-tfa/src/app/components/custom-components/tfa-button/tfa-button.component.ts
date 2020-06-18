import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { UtilService } from 'src/app/services/util.servcie';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';

@Component({
	selector: 'app-tfa-button',
	templateUrl: './tfa-button.component.html',
	styleUrls: ['./tfa-button.component.scss']
})
export class TfaButtonComponent implements OnInit {
	@Input()
	public tooltipText: string;

	@Input()
	public iconName: string;

	@Input()
	public methodName: string;

	@Input()
	public isResendBtn: boolean;

	@Input()
	public styleClass: any;

	@Input()
	public prevMethod: any;

	@Input()
	public currentMethod: any;

	@Output()
	public onResendCode: EventEmitter<any> = new EventEmitter();

	public get isTotp() {
		return this.currentMethod.method === TfaMethod.TOTP;
	}

	constructor(private _utilService: UtilService) {}

	ngOnInit() {}

	public resendCode(): void {
		// this.prevMethod ? (this.prevMethod.disabledBtn = false) : '';

		this.onResendCode.emit({
			name: this.methodName,
			// prevMethod: this.prevMethod
		});

		// this.prevMethod = this.currentMethod;
	}

	public hasIcon() {
		return this.iconName !== '';
	}
}
