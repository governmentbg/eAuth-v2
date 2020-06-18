import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppLayoutComponent } from './app-layout.component';
import { AppFooterComponent } from '../app-footer/app-footer.component';
import { AppHeaderComponent } from '../app-header/app-header.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('AppLayoutComponent', () => {
	let component: AppLayoutComponent;
	let fixture: ComponentFixture<AppLayoutComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [
				AppLayoutComponent,
				AppFooterComponent,
				AppHeaderComponent,
			],
			imports: [
				TranslateModule.forRoot(),
				SharedMaterialModule,
				RouterTestingModule,
			],
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AppLayoutComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
