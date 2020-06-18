import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedMaterialModule} from '../../shared/shared-material.module';
import {QrCodeDialogComponent} from './qr-code-dialog.component';

@NgModule({
	declarations: [QrCodeDialogComponent],
	imports: [
		ReactiveFormsModule,
		FormsModule,
		SharedMaterialModule,
	],
	exports: [QrCodeDialogComponent],
})
export class QrCodeDialogModule {
}
