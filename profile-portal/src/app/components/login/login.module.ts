import { NgModule } from '@angular/core';
import { LoginComponent } from './login.component';
import { LoginRoutingModule } from './login-routing.module';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';

@NgModule({
    declarations: [ LoginComponent ],
    imports:[
        ReactiveFormsModule,
        FormsModule,
        LoginRoutingModule,
        SharedMaterialModule
    ],
    exports: [ LoginComponent ],
    providers: [AuthService]
})
export class LoginModule {
}