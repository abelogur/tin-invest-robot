import {Component, OnDestroy, OnInit} from '@angular/core';
import {Chart, registerables} from "chart.js";
import {BotService} from "../../services/bot.service";
import {ActivatedRoute} from "@angular/router";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import 'chartjs-adapter-moment';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.scss']
})
export class StatisticsComponent implements OnInit, OnDestroy {
  notifier = new Subject();
  botId?: string;
  offset = 0;

  constructor(private service: BotService, private route: ActivatedRoute) {
    Chart.register(...registerables);
    this.route.params.pipe(takeUntil(this.notifier)).subscribe((result) => {
      this.botId = result['botId'];
    });
  }

  ngOnInit(): void {
    if (this.botId) {
      this.service.getStatistics(this.botId, this.offset).subscribe((result) => {
        const data = {
          datasets: [{
            data: result.points.map(item => {return {x: item.time,
              y: item.value}}),
            borderColor: 'rgb(75, 192, 192)',
          }]
        };
        // @ts-ignore
        const myChart = new Chart('myChart', {
          options: {
            scales: {
              x: {
                type: 'time',
                time: {
                  unit: 'month'
                }
              }
            }
          },
          type: 'line',
          data: data,
        });
      });
    }
  }

  ngOnDestroy(): void {
    this.notifier.next(true);
    this.notifier.complete();
  }

}
