import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { TfaService } from 'src/app/services/tfa.service';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';
import { Transaction } from 'src/app/model/transacation';
import { ToastrService } from 'ngx-toastr';
import { CodeRequest } from 'src/app/model/codeRequest';
import { QrCodeDialogComponent }
	from '../qr-code-dialog/qr-code-dialog.component';
import { MatDialog } from '@angular/material';
import { QrCode } from 'src/app/model/qrCode';
import { Router } from '@angular/router';
import { UtilService } from 'src/app/services/util.servcie';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { appConfig } from 'src/app/app.config';

@Component({
	selector: 'app-verifier',
	templateUrl: './verifier.component.html',
	styleUrls: ['./verifier.component.scss']
})
export class VerifierComponent implements OnInit, AfterViewInit, OnDestroy {
	private _token: string;
	private _unsubscribe: Subject<void> = new Subject();

	public transaction: Transaction = new Transaction();

	public timer: any;
	// public isTimeout: boolean = false;
	public isEmail: boolean;
	public isSms: boolean;
	public isTotp: boolean;

	public cancelAuthoriation = false;
	// public timeLeft: number = 0;
	public methods: any[] = [];
	disableBtn = false;
	previousMethod: any;
	public hasTotpMethod: boolean;

	public get cancelAuthEndpoint() {
		return appConfig.cancelAuthEndpoint;
	}

	public get timeLeft() {
		return this._utilservice.timeLeft;
	}

	public get initialSendFlag(): boolean {
		return JSON.parse(localStorage.getItem('isSend'));
	}

	public set initialSendFlag(isSend: boolean) {
		localStorage.setItem('isSend', isSend.toString());
	}

	constructor(
		public dialog: MatDialog,
		private _tfaService: TfaService,
		private _toastr: ToastrService,
		private _router: Router,
		private _utilservice: UtilService
	) {}

	ngOnInit() {
		this.transaction = JSON.parse(localStorage.getItem('transaction'));
		this._tfaService
			.getAvailableMethods()
			.pipe(takeUntil(this._unsubscribe))
			.subscribe((res: any) => {
				// console.log(res);
				res.map((method) =>
					this.methods.push({ ...method, disableBtn: false })
				);
				console.log(this.methods);
				this.hasDefaultMethod();
			});
	}

	ngAfterViewInit(): void {
		this._utilservice.translateErrorCodes();
		this._utilservice.timeoutMethod();
		this.methodClickValidation();
	}

	public hasDefaultMethod() {
		if (this.initialSendFlag) {
			let noDefautlMethodFlag = true;

			this.methods.forEach((method) => {
				if (method.defaultMethod) {
					this.defaultSendCode();
					noDefautlMethodFlag = false;
				}
			});

			if (noDefautlMethodFlag) {
				this._toastr.info(
					'Вашият метод по подразбиране не е активен, моля изберете друг!', '', {
						enableHtml: true,
					}
				);
			}
		}
	}

	public defaultSendCode() {
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
				this.initialSendFlag = false;
				// this._router.navigate(['verifier']);
				this._toastr.success(res['message'], '', {
					enableHtml: true,
				});
				this.checkForGeneratedCode();

				this._utilservice.timeoutMethod();
			},
			() => {
				this._toastr.error(
					'Неуспешно изпращане, моля опитайте отново!', 'Грешка', {
						enableHtml: true,
					});
			}
		);
	}

	public resendCode(method) {
		const codeReq = new CodeRequest();
		this.transaction = new Transaction();
		codeReq.ctId = this._utilservice.transaction
			? this._utilservice.transaction.tid
			: '';
		codeReq.newCodeType = method.method;
		this._utilservice.timeLeft = 0;
		this.previousMethod = method;

		this._tfaService
			.resendCode(codeReq, this._token)
			.pipe(takeUntil(this._unsubscribe))
			.subscribe((res) => {
				this.transaction.tid = res['tid'];
				this.transaction.method = res['method'];
				this.transaction.timestamp = res['timestamp'];

				localStorage.setItem(
					'transaction',
					JSON.stringify(this.transaction)
				);
				this._toastr.success(res['message']);
				this._utilservice.timeoutMethod();
				this.methodClickValidation();
			}, () => {
				this._toastr.error(
					'Неуспешно изпращане, моля опитайте отново!', 'Грешка', {
						enableHtml: true,
					});
			});
	}

	// Need to be optimize at some point
	public methodClickValidation() {
		this.disableBtn = true;
	}

	public checkForTotpMethod() {
		return (
			this.methods.length > 0 &&
			this.methods.find((method) => method.method === 'TOTP')
		);
	}

	public generateNewCode() {
		this._tfaService
			.generateQRCode(TfaMethod.TOTP, this._token)
			.subscribe((res) => {
				console.log(res);
				this.storeNewTransactionInLS(TfaMethod.TOTP, res);
				this.openDialog();
			});
	}

	public openDialog(): void {
		// this._utilservice.setTransacrion();

		if (this._utilservice.transaction.qrCode) {
			const qrSecretKey = this._utilservice.transaction.qrCode.secretKey;
			const qrImage = this._utilservice.transaction.qrCode.qrImage;

			const dialogRef = this.dialog.open(QrCodeDialogComponent, {
				width: '450px',
				data: { secretKey: qrSecretKey, qrImage: qrImage }
			});

			dialogRef.afterClosed().subscribe(() => {
				console.log('The dialog was closed');
			});
		}
	}

	public cancelAuth() {
		this._router.navigate(['verified']);
		localStorage.clear();
		clearInterval(this._utilservice.timer);
	}

	private checkForGeneratedCode() {
		if (
			this._utilservice.transaction &&
			this._utilservice.transaction.method === 'TOTP' &&
			this._utilservice.transaction.qrCode
		) {
			this.generateNewCode();
		}
	}

	private storeNewTransactionInLS(metod: string, res: Record<string, any>) {
		const newTransaction = new Transaction();

		newTransaction.qrCode = new QrCode();
		if (this._utilservice.transaction) {
			newTransaction.tid = this._utilservice.transaction.tid;
			newTransaction.timestamp = this._utilservice.transaction.timestamp;
		}

		newTransaction.method =
			metod === TfaMethod.TOTP
				? TfaMethod.TOTP
				: this._utilservice.transaction.method;
		newTransaction.qrCode.qrImage = res['qrImage'];
		newTransaction.qrCode.secretKey = res['secretKey'];

		localStorage.setItem('transaction', JSON.stringify(newTransaction));

		return newTransaction;
	}

	ngOnDestroy(): void {
		clearInterval(this.timer);
		localStorage.clear();
		this._unsubscribe.next();
		this._unsubscribe.complete();
	}
}
