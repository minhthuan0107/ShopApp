(window as any).global = window;
import { tokenInterceptor } from './app/interceptors/token.interceptor';
import { headerInterceptor} from './app/interceptors/header.interceptor';
import { bootstrapApplication } from '@angular/platform-browser';
import { routes } from './app/app.routes'; // Import routes từ app-router
import { ToastrModule } from 'ngx-toastr';
import { importProvidersFrom } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SlickCarouselModule } from 'ngx-slick-carousel';
bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(ToastrModule.forRoot()),
    importProvidersFrom(BrowserAnimationsModule),
    importProvidersFrom(HttpClientModule) ,
    importProvidersFrom(SlickCarouselModule) ,
    importProvidersFrom(ReactiveFormsModule),
    provideHttpClient(withInterceptors([tokenInterceptor,headerInterceptor])),
    [provideRouter(routes)] // Cung cấp router với các route đã định nghĩa     // Cấu hình Toastr tại đây
  ],
  
})
  .catch((err) => console.error(err));
//DetailProductComponent
//OrderComponent