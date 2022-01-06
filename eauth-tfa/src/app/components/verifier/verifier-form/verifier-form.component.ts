import {
	Component,
	OnInit,
	ViewChild,
	ElementRef,
	Input,
	AfterViewInit,
	OnDestroy,
	HostListener,
	Output,
	EventEmitter
} from '@angular/core';
import { MatButton } from '@angular/material';
import { TfaService } from 'src/app/services/tfa.service';
import { UtilService } from 'src/app/services/util.servcie';
import { OtpRequest } from 'src/app/model/otpRequest';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { appConfig } from 'src/app/app.config';

@Component({
	selector: 'app-verifier-form',
	templateUrl: './verifier-form.component.html',
	styleUrls: ['./verifier-form.component.scss']
})
export class VerifierFormComponent implements OnInit, AfterViewInit, OnDestroy {
	@ViewChild('firstDigitEl', { static: false })
	public firstDigitEl: ElementRef;

	@ViewChild('secondDigitEl', { static: false })
	public secondDigitEl: ElementRef;

	@ViewChild('thirdDigitEl', { static: false })
	public thirdDigitEl: ElementRef;

	@ViewChild('fourthDigitEl', { static: false })
	public fourthDigitEl: ElementRef;

	@ViewChild('fifthDigitEl', { static: false })
	public fifthDigitEl: ElementRef;

	@ViewChild('sixthDigitEl', { static: false })
	public sixthDigitEl: ElementRef;

	@ViewChild('verifyBtn', { static: false })
	public verifyBtn: MatButton;

	@Output()
	public onDefaultSendCode: EventEmitter<any> = new EventEmitter();

	@Input()
	public methods: any[] = [];

	@HostListener('window:unload', ['$event'])
	unloadHandler(event) {
		console.log(event);
	}

	public sendCode = '';

	public _token: string;

	public timer: any;
	public otpRequest = new OtpRequest();

	public get tId() {
		return this._utilService.transaction
			? this._utilService.transaction.tid
			: '';
	}

	public get tMethod() {
		return this._utilService.transaction
			? this._utilService.transaction.method
			: '';
	}

	public get verifyEndpoint() {
		return appConfig.verifyUrl;
	}
	constructor(
		private _tfaService: TfaService,
		private _utilService: UtilService,
		private _toastr: ToastrService,
		private _router: Router
	) {}

	ngOnInit() {
	}

	ngAfterViewInit(): void {
		this._token = localStorage.getItem('twoFAToken');

		this.firstDigitEl.nativeElement.value = '';
		this.secondDigitEl.nativeElement.value = '';
		this.thirdDigitEl.nativeElement.value = '';
		this.fourthDigitEl.nativeElement.value = '';
		this.fifthDigitEl.nativeElement.value = '';
		this.sixthDigitEl.nativeElement.value = '';
	}

	public verifyCode() {
		const otpRequest = new OtpRequest();
		otpRequest.tId = this._utilService.transaction.tid;
		otpRequest.code = this._getCode();
		otpRequest.method = TfaMethod[this._utilService.transaction.method];

		this._tfaService
			.verifyCode(otpRequest, this._token)
			.subscribe((res) => {
				if (res['valid']) {
					this._router.navigate([
						'verified',
						this._utilService.transaction.method
					]);
					// this._toastr.success('Успешно валидиран код')
				} else {
					this._toastr.error('Невалиден код', '', {
						enableHtml: true,
					});
				}
			});
	}

	public moveFocus(ev, element) {
		if (ev.target.value) {
			this.sendCode = this._getCode();
			setTimeout(() => element.focus(), 10);
		}
	}

	public onKeydown(event, focusedElement, deletedElement) {
		if (event.key === 'Backspace') {
			setTimeout(() => focusedElement.focus());
			deletedElement.value = '';
			this.sendCode = this._getCode();
		}
	}

	public clearCode() {
		this.firstDigitEl.nativeElement.value = '';
		this.secondDigitEl.nativeElement.value = '';
		this.thirdDigitEl.nativeElement.value = '';
		this.fourthDigitEl.nativeElement.value = '';
		this.fifthDigitEl.nativeElement.value = '';
		this.sixthDigitEl.nativeElement.value = '';

		this.verifyBtn.disabled = true;
		this.sendCode = '';
	}

	public verifyBtnValidation(): boolean {
		return this.sendCode.length !== 6;
	}

	private _getCode(): string {
		return (
			this.firstDigitEl.nativeElement.value +
			'' +
			this.secondDigitEl.nativeElement.value +
			'' +
			this.thirdDigitEl.nativeElement.value +
			'' +
			this.fourthDigitEl.nativeElement.value +
			'' +
			this.fifthDigitEl.nativeElement.value +
			'' +
			this.sixthDigitEl.nativeElement.value
		);
	}

	ngOnDestroy(): void {
		localStorage.clear();
	}
}
