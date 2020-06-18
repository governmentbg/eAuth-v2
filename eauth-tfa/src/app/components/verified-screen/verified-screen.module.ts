import {NgModule} from '@angular/core';
import {VerifiedScreenComponent} from './verified-screen.component';
import {SharedMaterialModule} from 'src/app/shared/shared-material.module';
import {CommonModule} from '@angular/common';
import {VerifiedScreenRoutingModule} from './verified-screen-routing.module';
import {FormsModule} from '@angular/forms';
import {QrCodeDialogModule} from '../qr-code-dialog/qr-code-dialog.module';
import {TfaService} from 'src/app/services/tfa.service';
import {QrCodeDialogComponent}
	from '../qr-code-dialog/qr-code-dialog.component';

@NgModule({
	declarations: [VerifiedScreenComponent],
	entryComponents: [QrCodeDialogComponent],
	imports: [
		VerifiedScreenRoutingModule,
		SharedMaterialModule,
		CommonModule,
		FormsModule,
		QrCodeDialogModule,
	],
	exports: [VerifiedScreenComponent],
	providers: [TfaService],
})
export class VerifiedScreenModule {}
