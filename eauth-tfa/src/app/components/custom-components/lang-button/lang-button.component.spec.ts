import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LangButtonComponent } from './lang-button.component';
import { TranslateModule } from '@ngx-translate/core';
import { HttpClientModule } from '@angular/common/http';
import { UtilService } from 'src/app/services/util.servcie';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastrModule } from 'ngx-toastr';

describe('LangButtonComponent', () => {
	let component: LangButtonComponent;
	let fixture: ComponentFixture<LangButtonComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				RouterTestingModule,
				ToastrModule.forRoot(),
				HttpClientModule
			],
			declarations: [LangButtonComponent],
			providers: [UtilService],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(LangButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
