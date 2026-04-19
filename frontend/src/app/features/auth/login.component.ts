import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

/**
 * Component providing user authentication functionality.
 * Handles login credentials submission and session initialization.
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  /** User-provided username */
  username = '';
  /** User-provided password */
  password = '';
  /** Error message to display on authentication failure */
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Submits login credentials to the authentication service.
   * On success, stores the token and redirects to the home page.
   */
  onSubmit(): void {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.error = 'Invalid credentials';
      }
    });
  }
}
