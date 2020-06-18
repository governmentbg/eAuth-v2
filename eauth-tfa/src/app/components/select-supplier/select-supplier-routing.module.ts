import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SelectSupplierComponent} from './select-supplier.component';

const routes: Routes = [
	{
		path: '',
		component: SelectSupplierComponent,
	},

];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class SelectSupplierRoutingModule {}
