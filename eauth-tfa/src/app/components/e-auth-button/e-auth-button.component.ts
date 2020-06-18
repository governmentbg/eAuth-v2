import { Component, OnInit, NgModule } from '@angular/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { CommonModule } from '@angular/common';
import { TfaService } from 'src/app/services/tfa.service';
import { Router } from '@angular/router';
import { Transaction } from 'src/app/model/transacation';
import { ToastrService } from 'ngx-toastr';
import { UtilService } from 'src/app/services/util.servcie';

@Component({
	selector: 'app-e-auth-button',
	templateUrl: './e-auth-button.component.html',
	styleUrls: ['./e-auth-button.component.scss']
})
export class EAuthButtonComponent implements OnInit {
	private _token: string;

	constructor(
		private _tfaService: TfaService,
		private _router: Router,
		private _toastr: ToastrService,
		private _utilService: UtilService
	) {}

	ngOnInit() {}

	public eAuth() {
		this._token = localStorage.getItem('twoFAToken');
		const transaction = new Transaction();

		this._tfaService.sendCode(this._token).subscribe(
			(res) => {
				transaction.tid = res['tid'];
				transaction.method = res['method'];
				transaction.qrCode = res['qrCode'];
				transaction.timestamp = res['timestamp'];

				localStorage.setItem(
					'transaction',
					JSON.stringify(transaction)
				);

				this._router.navigate(['verifier']);
				this._toastr.success(res['message']);

				this._utilService.timeoutMethod();
			},
			() => {
				this._toastr.error('Текста ще се прецизира', 'Грешка');
			}
		);
	}
}

@NgModule({
	declarations: [EAuthButtonComponent],
	imports: [SharedMaterialModule, CommonModule],
	exports: [EAuthButtonComponent],
	providers: [TfaService]
})
export class EAuthButtonModule {}
