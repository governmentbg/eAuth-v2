import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomInputComponent } from './custom-input.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { CustomTranslate } from '../../pipes/custom-translate.pipe';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('CustomInputComponent', () => {
	let component: CustomInputComponent;
	let fixture: ComponentFixture<CustomInputComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [CustomInputComponent, CustomTranslate],
			imports: [
				NoopAnimationsModule,
				TranslateModule.forRoot(),
				SharedMaterialModule,
			],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(CustomInputComponent);
		component = fixture.componentInstance;
		const attribute: any = { id: '1', label: 'test', type: 'test' };
		component.attribute = attribute;

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
