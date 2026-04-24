import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Route guard that prevents unauthorized access to protected views.
 * Redirects unauthenticated users to the login page.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Determines if a route can be activated based on user authentication status.
   * @returns true if authenticated, or a UrlTree redirecting to login.
   */
  canActivate(): boolean | UrlTree {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    // Redirect to login page
    return this.router.parseUrl('/login');
  }
}
