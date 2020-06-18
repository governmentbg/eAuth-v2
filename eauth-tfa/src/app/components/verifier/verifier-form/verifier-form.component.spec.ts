import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifierFormComponent } from './verifier-form.component';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastrModule } from 'ngx-toastr';
import { TfaService } from 'src/app/services/tfa.service';
import { HttpClientModule } from '@angular/common/http';
import { UtilService } from 'src/app/services/util.servcie';

describe('VerifierFormComponent', () => {
	let component: VerifierFormComponent;
	let fixture: ComponentFixture<VerifierFormComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [VerifierFormComponent],
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				ToastrModule.forRoot(),
				RouterTestingModule,
				HttpClientModule,
			],
			providers: [TfaService, UtilService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(VerifierFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
