import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {TempLoginComponent} from './temp-login.component';

const routes: Routes = [
	{
		path: '',
		component: TempLoginComponent,
	},
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class TempLoginRoutingModule {}
