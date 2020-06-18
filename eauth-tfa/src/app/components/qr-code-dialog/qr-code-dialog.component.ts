import { Component, OnInit, Inject } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { TfaService } from 'src/app/services/tfa.service';
import { QrCode } from 'src/app/model/qrCode';
import { Transaction } from 'src/app/model/transacation';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';

@Component({
	selector: 'app-qr-code',
	templateUrl: './qr-code-dialog.component.html',
	styleUrls: ['./qr-code-dialog.component.scss']
})
export class QrCodeDialogComponent implements OnInit {
	private _token: string;
	private _transaction: Transaction = new Transaction();

	public imageUrl: SafeUrl;
	public securityCode: string;

	constructor(
		private _tfaService: TfaService,
		private _sanitizer: DomSanitizer,
		public dialogRef: MatDialogRef<QrCodeDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: QrCode
	) {}

	ngOnInit() {
		this._token = localStorage.getItem('twoFAToken');
		this._transaction = JSON.parse(localStorage.getItem('transaction'));

		this.securityCode = this.data.secretKey;
		this.imageUrl = this._sanitizer.bypassSecurityTrustUrl(
			'data:image/png;base64,' + this.data.qrImage
		);
	}

	public generateNewQrCode() {
		this._tfaService
			.generateQRCode(TfaMethod.TOTP, this._token)
			.subscribe((res) => {
				console.log(res);
				const newTransaction = this.storeNewTransactionInLS(
					TfaMethod.TOTP,
					res
				);

				this.securityCode = newTransaction.qrCode.secretKey;
				this.imageUrl = this._sanitizer.bypassSecurityTrustUrl(
					'data:image/png;base64,' + newTransaction.qrCode.qrImage
				);
			});
	}

	private storeNewTransactionInLS(method: string, res: Record<string, any>) {
		const newTransaction = new Transaction();

		newTransaction.qrCode = new QrCode();
		newTransaction.tid = this._transaction.tid;
		newTransaction.method =
			method === TfaMethod.TOTP
				? TfaMethod.TOTP
				: this._transaction.method;
		newTransaction.qrCode.qrImage = res['qrImage'];
		newTransaction.qrCode.secretKey = res['secretKey'];
		newTransaction.timestamp = this._transaction.timestamp;

		localStorage.setItem('transaction', JSON.stringify(newTransaction));

		return newTransaction;
	}
}
