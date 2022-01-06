import {
	Component,
	OnInit,
	Output,
	EventEmitter,
	ViewChild,
	ElementRef,
} from '@angular/core';

@Component({
	selector: 'app-verify-input',
	templateUrl: './verify-input.component.html',
	styleUrls: ['./verify-input.component.scss'],
})
export class VerifyInputComponent implements OnInit {
	public digits: any[] = [];

	@ViewChild('firstDigitEl', { static: false }) firstDigitEl: ElementRef;
	@ViewChild('secondDigitEl', { static: false }) secondDigitEl: ElementRef;
	@ViewChild('thirdDigitEl', { static: false }) thirdDigitEl: ElementRef;
	@ViewChild('fourthDigitEl', { static: false }) fourthDigitEl: ElementRef;
	@ViewChild('fifthDigitEl', { static: false }) fifthDigitEl: ElementRef;
	@ViewChild('sixthDigitEl', { static: false }) sixthDigitEl: ElementRef;

	@Output()
	public onVerifyCode: EventEmitter<string> = new EventEmitter();

	constructor() {}

	ngOnInit() {}

	public inputChange(event, index, element?) {
		this.moveFocus(event, element);

		this.digits.splice(index, 1, event.target.value);

		this.verifyCode();
	}

	public moveFocus(ev, element?) {
		if (ev.data && element) {
			setTimeout(() => element.focus());
		}
	}

	public verifyCode() {
		const code = this.digits.join('');
		this.onVerifyCode.emit(code);
	}

	public clear() {
		this.firstDigitEl.nativeElement.value = '';
		this.secondDigitEl.nativeElement.value = '';
		this.thirdDigitEl.nativeElement.value = '';
		this.fourthDigitEl.nativeElement.value = '';
		this.fifthDigitEl.nativeElement.value = '';
		this.sixthDigitEl.nativeElement.value = '';
	}
}
