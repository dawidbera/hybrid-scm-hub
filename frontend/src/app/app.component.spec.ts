import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { AuthService } from './core/services/auth.service';
import { of } from 'rxjs';

describe('AppComponent', () => {
  let authServiceMock: any;

  beforeEach(() => {
    authServiceMock = {
      isLoggedIn$: of(false),
      logout: jasmine.createSpy('logout')
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [AppComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
