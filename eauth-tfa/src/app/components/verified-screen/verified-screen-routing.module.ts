import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {VerifiedScreenComponent} from './verified-screen.component';

const routes: Routes = [
	{
		path: '',
		component: VerifiedScreenComponent,
	},
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class VerifiedScreenRoutingModule {}
