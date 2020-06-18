import { Pipe, PipeTransform, ChangeDetectorRef } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Pipe({ name: 'customTranslate'})
export class CustomTranslate implements PipeTransform {
	constructor(private _translate: TranslateService,
        private _cdr: ChangeDetectorRef) {
	}

	transform(value: any, locale: any) {
		if (locale === 'bg') {
			return value.bg;
		} else {
			return value.en;
		}


		// return label;
	}
}
