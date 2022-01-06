import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { TfaService } from 'src/app/services/tfa.service';
import { UtilService } from 'src/app/services/util.servcie';
import { MethodButtonComponent } from './method-button.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastrModule } from 'ngx-toastr';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { TfaMethod } from 'src/app/model/enums/tfaMethod';

describe('MethodButtonComponent', () => {
	let component: MethodButtonComponent;
	let fixture: ComponentFixture<MethodButtonComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [
				HttpClientModule,
				TranslateModule.forRoot(),
				RouterTestingModule,
				ToastrModule.forRoot(),
				SharedMaterialModule,
			],
			providers: [TfaService, HttpClient, UtilService],
			declarations: [MethodButtonComponent],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(MethodButtonComponent);
		const componentInstance = fixture.debugElement.componentInstance;
		const type = TfaMethod.EMAIL;
		componentInstance.methodType = type;
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
