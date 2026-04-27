import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';

/**
 * Service for handling user authentication and session management.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';
  private loggedInSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  
  /**
   * Observable tracking the current login status.
   */
  isLoggedIn$ = this.loggedInSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Performs user login.
   * @param username The username.
   * @param password The password.
   * @returns An Observable with the authentication token.
   */
  login(username: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        this.loggedInSubject.next(true);
      })
    );
  }

  /**
   * Logs out the user by removing the token from local storage.
   */
  logout(): void {
    localStorage.removeItem('token');
    this.loggedInSubject.next(false);
  }

  /**
   * Checks if a user is currently logged in.
   * @returns true if a token exists in local storage.
   */
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  /**
   * Retrieves the authentication token from local storage.
   * @returns The token string or null if not found.
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
