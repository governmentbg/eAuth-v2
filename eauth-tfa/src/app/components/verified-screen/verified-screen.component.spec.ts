import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VerifiedScreenComponent} from './verified-screen.component';
import { RouterTestingModule } from '@angular/router/testing';
import { UtilService } from 'src/app/services/util.servcie';
import { HttpClientModule } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ToastrModule } from 'ngx-toastr';

describe('VerifiedScreenComponent', () => {
	let component: VerifiedScreenComponent;
	let fixture: ComponentFixture<VerifiedScreenComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [VerifiedScreenComponent],
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				ToastrModule.forRoot(),
				RouterTestingModule,
				HttpClientModule,
			],
			providers: [UtilService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(VerifiedScreenComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
