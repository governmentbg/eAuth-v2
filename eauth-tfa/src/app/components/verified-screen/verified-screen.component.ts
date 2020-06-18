import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UtilService } from 'src/app/services/util.servcie';

@Component({
	selector: 'app-verified-screen',
	templateUrl: './verified-screen.component.html',
	styleUrls: ['./verified-screen.component.scss']
})
export class VerifiedScreenComponent implements OnInit {
	private _errorMessage = `Отказахте или изтече времето 
    за потвърждаване на вашата автентикация.
    Моля започнете от начало!`;

	@Input()
	public isTimeout: boolean;

	public verifiedMethod: string;
	public message: string;
	public image: string;

	constructor(
		private _activateRoute: ActivatedRoute,
		private _router: Router,
		private _utilService: UtilService
	) {}

	ngOnInit() {
		this._activateRoute.paramMap.subscribe((params: any) => {
			this.verifiedMethod = params.params['method'];
		});

		console.log(this.isTimeout);
		this.showScreenDetails();
	}

	public goToHome() {
		this._router.navigate(['/']);
	}

	private showScreenDetails() {
		if (this.verifiedMethod && this.verifiedMethod !== '') {
			this.message = this.addMethodTypeToSentance(this.verifiedMethod);
			this.image = 'assets/img/success.png';
		} else {
			this.message = this._errorMessage;
			this.image = 'assets/img/error.png';
		}
	}

	private addMethodTypeToSentance(verifiedMethod) {
		return `Вие успешно потвърдихте вашата 
      идентичност посредством ${verifiedMethod} средство. 
      Следва пренасочване към системата на доставчика на ЕАУ.`;
	}
}
