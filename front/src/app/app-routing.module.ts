import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConnectionComponent } from './pages/connection/connection.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';

const routes: Routes = [
  // Routes publiques
  { path: '', component: HomeComponent, canActivate: [UnauthGuard] },
  { path: 'connection', component: ConnectionComponent, canActivate: [UnauthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [UnauthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [UnauthGuard] },

  // Routes privées
  { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'articles', component: ListComponent, canActivate: [AuthGuard], loadChildren: () => import('./features/article/article.module').then(m => m.ArticleModule) },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],  
})
export class AppRoutingModule {}
