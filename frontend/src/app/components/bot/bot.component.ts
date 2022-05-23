import { Component, OnInit } from '@angular/core';
import {BotService} from "../../services/bot.service";
import {Bot} from "../../interfaces/bot";
import {Subject} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {takeUntil} from "rxjs/operators";

@Component({
  selector: 'app-bot',
  templateUrl: './bot.component.html',
  styleUrls: ['./bot.component.scss']
})
export class BotComponent implements OnInit {
  bots: Bot[] = [];

  constructor(private service: BotService) {
  }

  ngOnInit(): void {
    this.service.getBots().subscribe((bots: Bot[]) => {
      this.bots = bots;
    })
  }

}
