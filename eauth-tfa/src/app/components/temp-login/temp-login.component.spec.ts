import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TempLoginComponent } from './temp-login.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ToastrModule } from 'ngx-toastr';
import { RouterTestingModule } from '@angular/router/testing';
import { TfaButtonComponent }
	from '../custom-components/tfa-button/tfa-button.component';
import { TfaService } from 'src/app/services/tfa.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { UtilService } from 'src/app/services/util.servcie';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TempLoginService } from './temp-login.service';

describe('TempLoginComponent', () => {
	let component: TempLoginComponent;
	let fixture: ComponentFixture<TempLoginComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				ToastrModule.forRoot(),
				RouterTestingModule,
				ReactiveFormsModule,
				FormsModule,
				HttpClientTestingModule,
			],
			declarations: [TempLoginComponent, TfaButtonComponent],
			providers: [TfaService, UtilService, TempLoginService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TempLoginComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
