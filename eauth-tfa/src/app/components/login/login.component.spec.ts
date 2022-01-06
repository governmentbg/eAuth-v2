import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ToastrModule } from 'ngx-toastr';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CustomInputComponent }
	from '../custom-components/custom-input/custom-input.component';
import { CustomTranslate } from '../pipes/custom-translate.pipe';
import { AttributeType } from 'src/app/model/enums/attributeType';
import { LevelOfAssurance } from 'src/app/model/enums/levelOfAssurance';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ProgressSpinnerService }
	from '../custom-components/progress-spinner/progress-spinner.service';

describe('LoginComponent', () => {
	let component: LoginComponent;
	let fixture: ComponentFixture<LoginComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [
				NoopAnimationsModule,
				TranslateModule.forRoot(),
				SharedMaterialModule,
				ToastrModule.forRoot(),
				RouterTestingModule,
				ReactiveFormsModule,
				FormsModule,
			],
			declarations: [LoginComponent, CustomInputComponent, CustomTranslate],
			providers: [ProgressSpinnerService]
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(LoginComponent);
		component = fixture.componentInstance;
		component.identityProvider = {
			id: '1',
			name: 'test',
			tfaRequierd: false,
			loa: LevelOfAssurance.LOW,
			attributes: [
				{ id: '1', label: {}, mandatory: false, type: AttributeType.EMAIL },
			],
		};
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
