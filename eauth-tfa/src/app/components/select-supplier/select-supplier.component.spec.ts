import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectSupplierComponent } from './select-supplier.component';
import { TranslateModule } from '@ngx-translate/core';
import { LoginComponent } from '../login/login.component';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ToastrModule } from 'ngx-toastr';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LangButtonComponent }
	from '../custom-components/lang-button/lang-button.component';
import { CustomTranslate }
	from '../pipes/custom-translate.pipe';
import { CustomInputComponent }
	from '../custom-components/custom-input/custom-input.component';
import { AuthService } from 'src/app/services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { UtilService } from 'src/app/services/util.servcie';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SelectSupplierComponent', () => {
	let component: SelectSupplierComponent;
	let fixture: ComponentFixture<SelectSupplierComponent>;

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
				HttpClientModule,
			],
			declarations: [
				SelectSupplierComponent,
				LoginComponent,
				LangButtonComponent,
				CustomTranslate,
				CustomInputComponent,
			],
			providers: [AuthService, UtilService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(SelectSupplierComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
