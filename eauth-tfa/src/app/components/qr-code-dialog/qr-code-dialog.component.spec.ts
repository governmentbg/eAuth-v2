import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QrCodeDialogComponent } from './qr-code-dialog.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ToastrModule } from 'ngx-toastr';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import {
	BrowserAnimationsModule,
	NoopAnimationsModule,
} from '@angular/platform-browser/animations';
import { TfaService } from 'src/app/services/tfa.service';
import { HttpClientModule } from '@angular/common/http';
import { UtilService } from 'src/app/services/util.servcie';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

describe('QrCodeComponent', () => {
	let component: QrCodeDialogComponent;
	let fixture: ComponentFixture<QrCodeDialogComponent>;

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
				BrowserAnimationsModule,
				HttpClientModule
			],
			declarations: [QrCodeDialogComponent],
			providers: [
				TfaService,
				UtilService,
				{
					provide: MatDialogRef,
					useValue: {},
				},
				{ provide: MAT_DIALOG_DATA, useValue: {} },
			],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(QrCodeDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
