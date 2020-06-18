import { NgModule } from '@angular/core';

import { EauthAdminSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [EauthAdminSharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [EauthAdminSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class EauthAdminSharedCommonModule {}
