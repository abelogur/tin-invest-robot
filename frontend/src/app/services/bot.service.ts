import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Bot} from "../interfaces/bot";
import {environment} from 'src/environments/environment';
import {BotChart} from "../interfaces/botChart";
import {Statistics} from "../interfaces/statistics";

@Injectable({
  providedIn: 'root'
})
export class BotService {
  private readonly BASE_URL = environment.BASE_URL;

  constructor(private http: HttpClient) {
  }

  getBots(): Observable<Bot[]> {
    console.log(this.BASE_URL);
    return this.http.get<Bot[]>(`${this.BASE_URL}/bot`)
  }

  getBotChart(botUuid: string, offset: number): Observable<BotChart> {
    let params = new HttpParams();

    // Begin assigning parameters

    params = params.append('offset', offset + '');
    return this.http.get<BotChart>(`${this.BASE_URL}/indicator/bot/${botUuid}`, {params})
  }

  getStatistic(botUuid: string): Observable<Statistics> {
    return this.http.get<Statistics>(`${this.BASE_URL}/statistic/bot/${botUuid}`)
  }
}
