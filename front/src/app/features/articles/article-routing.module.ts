import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
//import { FormComponent } from './components/form/form.component';
//import { DetailComponent } from './components/detail/detail.component';
import { ListComponent } from './components/list/list.component';


const routes: Routes = [
  { title: 'Articles', path: '', component: ListComponent },
  //{ title: 'Articles - detail', path: 'detail/:id', component: DetailComponent },
  //{ title: 'Articles - update', path: 'update/:id', component: FormComponent },
  //{ title: 'Articles - create', path: 'create', component: FormComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ArticleRoutingModule { }
