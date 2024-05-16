import { RouterModule, Routes } from '@angular/router';
import { RegisterScreenComponent } from './screens/register-screen/register-screen.component';
import { ListScreenComponent } from './screens/list-screen/list-screen.component';
import { NgModule } from '@angular/core';
import { DetailsScreenComponent } from './screens/details-screen/details-screen.component';

export const routes: Routes = [
  { path: '', redirectTo: '/register', pathMatch: 'full'},
  { path: 'register', component: RegisterScreenComponent},
  { path: 'list', component: ListScreenComponent},
  { path: 'user/:id', component: DetailsScreenComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
