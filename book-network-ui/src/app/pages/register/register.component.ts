import { AuthenticationService } from './../../services/services/authentication.service';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegistrationRequest } from 'src/app/services/models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) { }


  registerRequest: RegistrationRequest = {
    email: '',
    firstname: '',
    lastname: '',
    password: ''
  };
  errorMsg: Array<string> = [];

  login() {
    this.router.navigate(['login']);
  }
  register() {
    this.errorMsg = [];
    this.authService.register(
      { body: this.registerRequest }
    ).subscribe({
      next: (res) => {
        // todo save the token

        this.router.navigate(['activate-account']);
      },
      error: (err) => {
        console.log(err);

        this.errorMsg = err.error.validationErrors;


      }
    })
  }

}
