import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TfaButtonComponent } from './tfa-button.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastrModule } from 'ngx-toastr';
import { TfaService } from 'src/app/services/tfa.service';
import { HttpClientModule } from '@angular/common/http';
import { UtilService } from 'src/app/services/util.servcie';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';

describe('TfaButtonComponent', () => {
	let component: TfaButtonComponent;
	let fixture: ComponentFixture<TfaButtonComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TfaButtonComponent],
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				RouterTestingModule,
				ToastrModule.forRoot(),
				HttpClientModule,
			],
			providers: [TfaService, UtilService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TfaButtonComponent);
		component = fixture.componentInstance;
		component.currentMethod = { disabledBtn: false, method: TfaMethod.EMAIL };
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
