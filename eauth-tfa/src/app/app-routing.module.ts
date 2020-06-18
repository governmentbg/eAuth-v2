import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppLayoutComponent }
	from './_layout/app-layout/app-layout.component';

const routes: Routes = [
	// Site routes goes here
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
			{
				path: '',
				loadChildren: () =>
					import(
						'../app/components/temp-login/temp-login.module'
					).then((m) => m.TempLoginModule)
			},
			{
				path: 'sso-tfa',
				pathMatch: 'full',
				loadChildren: () =>
					import('../app/components/verifier/verifier.module').then(
						(m) => m.VerifierModule
					)
			},
			// { path: 'sso-tfa/:error', loadChildren: () => import('../app/components/verifier/verifier.module').then(m => m.VerifierModule) },
			{
				path: 'verified',
				loadChildren: () =>
					import(
						'../app/components/verified-screen/verified-screen.module'
					).then((m) => m.VerifiedScreenModule)
			},
			{
				path: 'verified/:method',
				loadChildren: () =>
					import(
						'../app/components/verified-screen/verified-screen.module'
					).then((m) => m.VerifiedScreenModule)
			}
		]
	},
	{
		path: 'ssologin',
		loadChildren: () =>
			import(
				'../app/components/select-supplier/select-supplier.module'
			).then((m) => m.SelectSupplierModule)
	},
	{
		path: 'ssologin/:error',
		loadChildren: () =>
			import(
				'../app/components/select-supplier/select-supplier.module'
			).then((m) => m.SelectSupplierModule)
	},
	// no layout routes
	// { path: 'login', component: LoginComponent},
	// { path: 'register', component: RegisterComponent },
	// otherwise redirect to home
	{ path: '**', redirectTo: '' }
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule {}
