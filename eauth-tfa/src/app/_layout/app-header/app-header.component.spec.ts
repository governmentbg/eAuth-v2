import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AppHeaderComponent} from './app-header.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';

describe('AppHeaderComponent', () => {
	let component: AppHeaderComponent;
	let fixture: ComponentFixture<AppHeaderComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [AppHeaderComponent],
			imports: [TranslateModule.forRoot(), SharedMaterialModule],
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AppHeaderComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
