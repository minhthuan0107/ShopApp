import { Injectable } from '@angular/core';
import { Router } from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly ACCESS_TOKEN_KEY = 'access-token';
  private readonly REFRESH_TOKEN_KEY = 'refresh-token';
  private useSessionStorage: boolean = true;


  constructor(private router: Router) { }
  public setUseSessionStorage(useSession: boolean): void {
    this.useSessionStorage = useSession;
  }

  public getAccessToken(): string | null {
    return sessionStorage.getItem(this.ACCESS_TOKEN_KEY)
      || localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }
  public setAccessToken(accessToken: string): void {
    if (this.useSessionStorage) {
      sessionStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    } else {
      localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    }
  }
  public removeToken(): void {
    sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }
  public getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }
  public setRefreshToken(refreshToken: string): void {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    }
  
  logout(): void {
    this.removeToken();
  }

}


