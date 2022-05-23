import {Component, OnDestroy, OnInit} from '@angular/core';
import {Chart, registerables} from "chart.js";
import {BotService} from "../../services/bot.service";
import {ActivatedRoute} from "@angular/router";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import 'chartjs-adapter-moment';
import {Order} from "../../interfaces/order";
import {Statistics} from "../../interfaces/statistics";

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.scss']
})
export class StatisticsComponent implements OnInit, OnDestroy {
  notifier = new Subject();
  botId?: string;
  offset = 0;

  statistic?: Statistics;

  COLORS = [
    '#f67019',
    '#f53794',
    '#acc236',
    '#00a950',
    '#8549ba'
  ];

  constructor(private service: BotService, private route: ActivatedRoute) {
    Chart.register(...registerables);
    this.route.params.pipe(takeUntil(this.notifier)).subscribe((result) => {
      this.botId = result['botId'];
    });
  }

  ngOnInit(): void {
    if (this.botId) {
      this.service.getBotChart(this.botId, this.offset).subscribe((result) => {
        const indicators = Object.keys(result.indicators).filter(key => key !== "Stochastic").map((key, index) => {
          return {
            data: result.indicators[key].map(item => {
              return {
                x: item.time,
                y: item.value
              }
            }),
            label: key,
            borderColor: this.COLORS[index],
            backgroundColor: this.COLORS[index],
            borderWidth: 1,
            radius: 0
          }
        })
        const data = {
          datasets: [{
            data: result.points.map(item => {
              return {
                x: item.time,
                y: item.value
              }
            }),
            borderColor: 'rgb(54, 162, 235)',
            backgroundColor: 'rgb(54, 162, 235, 0.5)',
            label: 'price',
            radius: 2
          }, ...indicators]
        };
        // @ts-ignore
        const myChart = new Chart('myChart', {
          options: {
            responsive: true,
            interaction: {
              mode: 'index',
              intersect: false,
            },
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

        const stochastic = Object.keys(result.indicators).filter(key => key === "Stochastic").map((key, index) => {
          return {
            data: result.indicators[key].map(item => {
              return {
                x: item.time,
                y: item.value
              }
            }),
            label: key,
            borderColor: 'blue',
            backgroundColor: 'blue',
            borderWidth: 1,
            radius: 0
          }
        })[0]
        const stochasticData = {
          datasets: [stochastic,
            {
              data: [{
                x: stochastic.data[0].x,
                y: 20
              }, {
                x: stochastic.data[stochastic.data.length - 1].x,
                y: 20
              }],
              label: 'low',
              borderColor: 'red',
              borderWidth: 1,
              radius: 0
            },
            {
              data: [{
                x: stochastic.data[0].x,
                y: 80
              }, {
                x: stochastic.data[stochastic.data.length - 1].x,
                y: 80
              }],
              label: 'high',
              borderColor: 'red',
              borderWidth: 1,
              radius: 0
            }
          ]
        };
        const stochasticChart = new Chart('stochasticChart', {
          options: {
            responsive: true,
            interaction: {
              mode: 'index',
              intersect: false,
            },
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
          data: stochasticData,
        });
      });
      this.service.getStatistic(this.botId).subscribe(result => {
        this.statistic = result;
      })
    }
  }

  ngOnDestroy(): void {
    this.notifier.next(true);
    this.notifier.complete();
  }

}
