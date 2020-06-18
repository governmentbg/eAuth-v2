import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AppFooterComponent} from './app-footer.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';

describe('AppFooterComponent', () => {
	let component: AppFooterComponent;
	let fixture: ComponentFixture<AppFooterComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [AppFooterComponent],
			imports: [TranslateModule.forRoot(), SharedMaterialModule],
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AppFooterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
