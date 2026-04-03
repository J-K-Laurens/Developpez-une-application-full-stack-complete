import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConnectionComponent } from './pages/connection/connection.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';


const routes: Routes = [
  // Default route - if not logged in go to login, otherwise go to articles
  { path: '', component: HomeComponent },

  // Protected public routes for users who are not logged in
  { path: 'connection', component: ConnectionComponent, canActivate: [UnauthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [UnauthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [UnauthGuard] },

  // Private routes
  { path: 'articles', canActivate: [AuthGuard], loadChildren: () => import('./features/articles/articles.module').then(m => m.ArticlesModule) },
  { path: 'themes', canActivate: [AuthGuard], loadChildren: () => import('./features/topics/topics.module').then(m => m.TopicsModule) },
  { path: 'profile', canActivate: [AuthGuard], loadChildren: () => import('./features/profile/profile.module').then(m => m.ProfileModule) },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],  
})
export class AppRoutingModule {}
