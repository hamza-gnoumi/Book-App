import { Router } from '@angular/router';
import { AuthenticationService } from './../../services/services/authentication.service';
import { Component } from '@angular/core';
import { AuthenticationRequest } from 'src/app/services/models';
import { TokenService } from 'src/app/services/token/token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private tokenService: TokenService) {
  }
  authRequest: AuthenticationRequest = { email: '', password: '' };
  errorMsg: Array<string> = [];

  login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        // save the token
        this.tokenService.token = res.token as string;
        this.router.navigate(['books']);
      },
      error: (err) => {

        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        }
        else {
          this.errorMsg.push(err.error.error);
        }
      }
    })
  }
  register() {
    this.router.navigate(['register']);
  }

}
