import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { EauthAdminSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [EauthAdminSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [EauthAdminSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class EauthAdminSharedModule {
  static forRoot() {
    return {
      ngModule: EauthAdminSharedModule
    };
  }
}
