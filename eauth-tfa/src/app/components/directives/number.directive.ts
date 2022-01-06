import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
	selector: '[numberOnly]',
})
export class NumberOnlyDirective {
	private regex: RegExp = new RegExp(/^[-+]?[\s\d]+$/g);

	private specialKeys: Array<string> = ['Backspace', 'Tab', 'End', 'Home'];

	constructor(private el: ElementRef) {}
	@HostListener('keydown', ['$event'])
	onKeyDown(e: KeyboardEvent) {
		if (
			this.specialKeys.indexOf(e.key) !== -1 ||
			(e.keyCode === 65 && e.ctrlKey === true) || // Allow: Ctrl+A
			(e.keyCode === 67 && e.ctrlKey === true) || // Allow: Ctrl+C
			(e.keyCode === 86 && e.ctrlKey === true) || // Allow: Ctrl+V
			(e.keyCode === 88 && e.ctrlKey === true) || // Allow: Ctrl+X
			(e.keyCode === 65 && e.metaKey === true) || // Cmd+A (Mac)
			(e.keyCode === 67 && e.metaKey === true) || // Cmd+C (Mac)
			(e.keyCode === 86 && e.metaKey === true) || // Cmd+V (Mac)
			(e.keyCode === 88 && e.metaKey === true)
		) {
			return;
		}
		const current: string = this.el.nativeElement.value;
		const next: string = current.concat(e.key);
		if (next && !String(next).match(this.regex)) {
			e.preventDefault();
		}
	}
}
