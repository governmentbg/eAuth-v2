import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterComponent } from './register.component';
import { RegisterRoutingModule } from './register-routing.module';
import { AuthService } from 'src/app/services/auth.service';
import { SharedServive } from 'src/app/services/shared.service';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PhoneMaskDirective } from 'src/app/directives/phone-mask.directive';

@NgModule({
  declarations: [RegisterComponent, PhoneMaskDirective],
  imports: [
    CommonModule,
    RegisterRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    SharedMaterialModule
  ],
  providers: [AuthService, SharedServive]
})
export class RegisterModule { }
