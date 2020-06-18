import { Component, OnInit, AfterViewInit } from "@angular/core";
import { MatDialog } from '@angular/material/dialog';
import { ChangeNameDialog } from '../change-name-dialog/change-name-dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { User } from 'src/app/model/user';
import { Observable } from 'rxjs';
import { appConfig } from 'src/app/app.config';
import { AuthService } from 'src/app/services/auth.service';
import { SharedServive } from 'src/app/services/shared.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { startWith, map } from 'rxjs/operators';
import { Address } from 'src/app/model/address';
import CyrillicToTranslit from '../../services/CyrillicToTransit.js';

@Component({
  selector: "app-regiter",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"]
})
export class RegisterComponent implements OnInit, AfterViewInit {
  jwt = `eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c0l0bUhRZkRwYUNCbWZ3Y0JqSzZ3PT0iLCJpc3MiOiJLcmFzc2ltaXIgQmxhZ29ldiBBbnRvbm92IiwiaWF0IjoxNTg1NjM5NDU1LCJleHAiOjE1ODU2Mzk3NTV9.t7eQkR2f_OjDT8SxsIZW-e-YsxB3njg7IBkkICyrv9vz148DsWUB2XF-EpHkDDUcc-MnZbT1PjJXoxu0XstRZA`

  public twoFATypes: any[] = [
    { id: 1, value: "sms", desc: "SMS", isChecked: false },
    { id: 2, value: "email", desc: "EMAIL", isChecked: false },
    { id: 3, value: "totp", desc: "TOTP", isChecked: false }
  ];

  public registerForm: FormGroup;
  public authType: any;
  public user = new User();

  public filteredRegions: Observable<any[]>;
  public optionAddress: any[] = [];
  public authToken: string;
  public fullName: string;
  public commonRulesAccept: boolean;
  public token: string;
  public profileName: string;

  public get isValidForm() {
    return (this.registerForm.invalid && this.authType !== 'undefined') || !this.commonRulesAccept;
  }

  public get agreementUrl() {
    return appConfig.agreementsUrl;
  }

  constructor(
    private _formBuilder: FormBuilder,
    private _authService: AuthService,
    private _sharedService: SharedServive,
    private _toastr: ToastrService,
    private _router: Router,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.registerForm = this._formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['+359', Validators.required],
      address: [''],
    });

    this.filteredRegions = this.registerForm.get("address").valueChanges.pipe(
      startWith(""),
      map(address =>
        address ? this._filteredAddresses(address) : this.optionAddress.slice()
      )
    );

    this.getLogedUser();
    this.getUserData();

  }

  ngAfterViewInit() {
    console.log(localStorage.getItem('user'));
    this.token = JSON.parse(localStorage.getItem('user')) ? JSON.parse(localStorage.getItem('user')).token : null;
    console.log(this.token);

  }

  private checkForMarthingNames(token) {
    console.log(token);
    console.log(this.fullName);

    if (token && this.fullName) {
      let nameFromToken = this.transliteration(this.getNameFromToken(token));
      let profileName = this.transliteration(this.fullName);
      console.log(nameFromToken);
      console.log(profileName);
      if (nameFromToken !== profileName) {
        this.openDialog(nameFromToken);
      }
    }
  }

  openDialog(nameFromToken): void {
    const dialogRef = this.dialog.open(ChangeNameDialog, {
      width: '350px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'Yes') {
        this.fullName = nameFromToken;
      }
    });
  }

  public onCheckChange(event, id): void {
    const index = this.twoFATypes.findIndex(_ => _.id === id);
    if (!(index > -1)) return;

    this.twoFATypes[index].isChecked = event.checked;
  }

  public save(): void {
    this.user.names = this.fullName;
    this.user.email = this.registerForm.get("email").value;
    this.user.phoneNumber = this.registerForm.get("phoneNumber").value;
    this.user.preferred2FA = 
      this.authType ? this.authType.desc : this._toastr.error('Моля изберете метод за двуфакторна автентикация(EMAIL, SMS или TOTP)');

    this.user.address = new Address();
    this.user.address.ekatte = this.registerForm.get('address').value !== '' ? this.registerForm.get('address').value : null;

    if (this.user.address.ekatte === null) {
      this.user.address = null;
    }

    this._authService.createProfile(this.user, this.authToken)
      .subscribe(res => {
        this._toastr.success(
          'Имейл: ' + this.user.email + ', Телефон: ' + this.user.phoneNumber,
          "Успешено създаден/обновен профил");
        this._router.navigate(['/']);
      },
      err => {
        // this._toastr.error("Грешка", err);
      }
    );
  }

  public getUserData() {
    this._authService.getUserProfile(this.authToken).subscribe(
      res => {
        this.setUserProfileData(res);
        // this._toastr.success("Успешено създаден профил");
      },
      (err: Response) => {
        if (err.status === 404) {
          this._toastr.error("Вашият профил не е намерен", "Ако нямате профил, моля въведете вашите данни");
        } else {
          this._toastr.error("Грешка", err.text.toString());
        }
      }
    );
  }

  public displayAddress(address?: any): string | undefined {
    return address ? address.fullName : undefined;
  }

  public getLogedUser() {
    this._authService.authUserProfile().subscribe((res: Response) => {
      this.token = res.toString();

      let token = JSON.stringify({token: res.toString()});
      
      localStorage.setItem('user', token);
      this.authToken = JSON.parse(localStorage.getItem('user')).token;
    })
  }

  public cyrillicTransform() {
    this.fullName = this.fullName.replace(/[^а-яё]/i, " ");
  }

  public goToAgreements() {
    window.open(appConfig.agreementsUrl, "_blank");
  }

  private setUserProfileData(userProfile) {
    this.user = userProfile.user;

    this.fullName = this.user.names;
    this.registerForm.get('email').setValue(this.user.email);
    this.registerForm.get('phoneNumber').setValue(this.user.phoneNumber);
    if (this.user.address) {
      this.registerForm.get('address').setValue(this.user.address.ekatte);
    }

    this.authType = this.getPreferred2Fa(this.user.preferred2FA);

    this.checkForMarthingNames(this.token);
  }

  private getPreferred2Fa(type) {
    let preferedType = this.twoFATypes.find(t => t.desc === type);
    return preferedType;
  }

  private _filteredAddresses(value: string): any[] {
    if (value.length >= 2) {
      const filterValue = typeof value === "string" ? value : "";

      this._sharedService.getPlaces(filterValue, this.authToken).subscribe(res => {
        this.optionAddress = res;
      });

      return this.optionAddress.filter(address =>
        address.fullName.includes(filterValue)
      );
    }
  }

  private transliteration(name) {
    let ctl = new CyrillicToTranslit({ preset: "bg" });
    // if (ctl.reverse(name) === 'Стоян Мaджaров') {
    //   this._toastr.info('Съвпадение');
    // }
    // else {
    //   this._toastr.info('Няма съвпадение');
    // }

    return ctl.reverse(name);
  }

  private getNameFromToken(accessToken) {
    if (accessToken) {
        let jwtData = accessToken.split('.')[1];
        console.log(jwtData);
        let decodeJwtJSONData = window.atob(jwtData);
        console.log(decodeJwtJSONData);
        let decodedJwtData = JSON.parse(decodeJwtJSONData);

        return decodedJwtData.iss;
    }
}
}
