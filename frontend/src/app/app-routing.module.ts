import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {BotComponent} from "./components/bot/bot.component";
import {StatisticsComponent} from "./components/statistics/statistics.component";

const routes: Routes = [
  { path: '',
    component: BotComponent
  },
  {
    path: 'bot/:botId',
    component: StatisticsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
