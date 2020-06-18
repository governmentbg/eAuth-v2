import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'profile-portal';

  constructor(private _translateService: TranslateService) {
    // Use a language
    this._translateService.use('bg');
  }
}
