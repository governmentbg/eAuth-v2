import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {EAuthButtonComponent} from './e-auth-button.component';
import { TfaService } from 'src/app/services/tfa.service';
import { HttpClientModule } from '@angular/common/http';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastrModule } from 'ngx-toastr';
import { UtilService } from 'src/app/services/util.servcie';
import { TranslateModule } from '@ngx-translate/core';

describe('EAuthButtonComponent', () => {
	let component: EAuthButtonComponent;
	let fixture: ComponentFixture<EAuthButtonComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [EAuthButtonComponent],
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				RouterTestingModule,
				ToastrModule.forRoot(),
				HttpClientModule
			],
			providers: [
				TfaService,
				UtilService
			]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(EAuthButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
