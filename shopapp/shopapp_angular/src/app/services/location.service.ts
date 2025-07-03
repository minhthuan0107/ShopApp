import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private apiUrl = `${environment.apiBaseUrl}/provinces`;

  constructor(private http: HttpClient) { }
  getProvinces(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  getDistrictsByProvince(provinceCode: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/p/${provinceCode}`);
  }

  getWardsByDistrict(districtCode: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/d/${districtCode}`);
  }
}

