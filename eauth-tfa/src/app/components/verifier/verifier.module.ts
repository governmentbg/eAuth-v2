import { NgModule } from '@angular/core';
import { VerifierComponent } from './verifier.component';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { CommonModule } from '@angular/common';
import { VerifierRoutingModule } from './verifier-routing.module';
import { FormsModule } from '@angular/forms';
import { QrCodeDialogModule } from '../qr-code-dialog/qr-code-dialog.module';
import { TfaService } from 'src/app/services/tfa.service';
import { QrCodeDialogComponent }
	from '../qr-code-dialog/qr-code-dialog.component';
import { VerifiedScreenModule }
	from '../verified-screen/verified-screen.module';
import { UtilService } from 'src/app/services/util.servcie';
import { TfaButtonModule }
	from 'src/app/components/custom-components/tfa-button/tfa-button.module';
import { VerifierFormModule } from './verifier-form/verifier-form.module';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
	declarations: [VerifierComponent],
	entryComponents: [QrCodeDialogComponent],
	imports: [
		VerifierRoutingModule,
		SharedMaterialModule,
		CommonModule,
		FormsModule,
		QrCodeDialogModule,
		VerifiedScreenModule,
		TfaButtonModule,
		VerifierFormModule,
		TranslateModule
	],
	exports: [VerifierComponent],
	providers: [TfaService, UtilService]
})
export class VerifierModule {}
