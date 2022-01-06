import { Component, OnInit, Input } from '@angular/core';
import { Transaction } from 'src/app/model/transacation';
import { CodeRequest } from 'src/app/model/codeRequest';
import { TfaService } from 'src/app/services/tfa.service';
import { UtilService } from 'src/app/services/util.servcie';
import { ToastrService } from 'ngx-toastr';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';

@Component({
	selector: 'app-method-button',
	templateUrl: './method-button.component.html',
	styleUrls: ['./method-button.component.scss'],
})
export class MethodButtonComponent implements OnInit {
	@Input()
	public tooltipText: string;

	@Input()
	public methodType: TfaMethod;

	@Input()
	public token: string;

	@Input()
	public disabledButton: boolean;

	constructor(
		private _tfaService: TfaService,
		private _utilService: UtilService,
		private _toastr: ToastrService
	) {}

	ngOnInit() {
		console.log(this.methodType);
	}

	public resendCode() {
		const resendTransaction = new Transaction();
		const codeReq = new CodeRequest();
		codeReq.ctId = this._utilService.transaction.tid;
		codeReq.newCodeType = this.methodType;

		this._tfaService.resendCode(codeReq, this.token).subscribe((res) => {
			resendTransaction.tid = res['tid'];
			resendTransaction.method = res['method'];
			resendTransaction.timestamp = res['timestamp'];

			localStorage.setItem(
				'transaction',
				JSON.stringify(resendTransaction)
			);
			this._toastr.success(res['message'], '', {
				enableHtml: true,
			});
			// this.timeoutMethod()
			// this.methodClickValidation();
		});
	}
}
