import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { ChangeNameDialog } from '../change-name-dialog/change-name-dialog';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  public loginForm: FormGroup;

  constructor(private _formBuiler: FormBuilder,
    private _authService: AuthService,
    private _router: Router,
    public dialog: MatDialog
    ) { }

  ngOnInit() {
    this.loginForm = this._formBuiler.group({
      egn: [],
      fullName: []
    })
  }

  public login(): void {
    this._authService.authUserProfile().subscribe((res: Response) => {
      this._router.navigate(['/register'])
    },(err) => {
      if (err.status === 401) {
        // window.location.href = '/eaft/authorize'
        window.location.href = '/eaft/authorize'
      }
    })

  }
  
  register() {
    this._router.navigate(['register']);
  }

}
