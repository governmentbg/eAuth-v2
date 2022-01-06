import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyInputComponent } from './verify-input.component';

describe('VerifyInputComponent', () => {
	let component: VerifyInputComponent;
	let fixture: ComponentFixture<VerifyInputComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [VerifyInputComponent]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(VerifyInputComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
