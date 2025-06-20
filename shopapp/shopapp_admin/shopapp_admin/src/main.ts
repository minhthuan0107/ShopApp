import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { ReactiveFormsModule } from '@angular/forms';
import { routes } from './app/app.routes';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { tokenInterceptor } from './app/interceptors/token.interceptor';
import { headerInterceptor } from './app/interceptors/header.interceptor';
bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(ToastrModule.forRoot()),
    importProvidersFrom(HttpClientModule) ,
    importProvidersFrom(ReactiveFormsModule),
    importProvidersFrom(BrowserAnimationsModule),
    provideHttpClient(withInterceptors([tokenInterceptor,headerInterceptor])),
    [provideRouter(routes)] // Cung cấp router với các route đã định nghĩa     // Cấu hình Toastr tại đây
  ],
  
})
  .catch((err) => console.error(err));
