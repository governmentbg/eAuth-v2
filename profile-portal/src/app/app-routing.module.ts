import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppLayoutComponent } from './_layout/app-layout/app-layout.component';
import { AppComponent } from './app.component';

const routes: Routes = [
    
  //Site routes goes here 
  // { 
  //     path: '', 
  //     component: SiteLayoutComponent,
  //     children: [
  //       { path: '', component: HomeComponent, pathMatch: 'full'},
  //       { path: 'about', component: AboutComponent },
  //       { path: 'test/:id', component: AboutComponent }
  //     ]
  // },
  
  // App routes goes here here
  { 
      path: '',
      component: AppLayoutComponent, 
      children: [
        { path: '', loadChildren: () => import('../app/components/login/login.module').then(m => m.LoginModule) },
        { path: 'register', loadChildren: () => import('./components/register/register.module').then(m => m.RegisterModule) },
      ]
  },

  //no layout routes
  // { path: 'login', component: LoginComponent},
  // { path: 'register', component: RegisterComponent },
  // otherwise redirect to home
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
