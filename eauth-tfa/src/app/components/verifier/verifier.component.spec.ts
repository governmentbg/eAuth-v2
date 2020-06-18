import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifierComponent } from './verifier.component';
import { TranslateModule } from '@ngx-translate/core';
import { HttpClientModule } from '@angular/common/http';
import { VerifierFormComponent } from './verifier-form/verifier-form.component';
import { TfaButtonComponent }
	from '../custom-components/tfa-button/tfa-button.component';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { TfaService } from 'src/app/services/tfa.service';
import { ToastrModule } from 'ngx-toastr';
import { RouterTestingModule } from '@angular/router/testing';
import { UtilService } from 'src/app/services/util.servcie';

describe('VerifierComponent', () => {
	let component: VerifierComponent;
	let fixture: ComponentFixture<VerifierComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				ToastrModule.forRoot(),
				RouterTestingModule,
				HttpClientModule,
			],
			declarations: [
				VerifierComponent,
				VerifierFormComponent,
				TfaButtonComponent,
			],
			providers: [TfaService, UtilService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(VerifierComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
