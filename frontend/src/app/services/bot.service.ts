import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Bot} from "../interfaces/bot";
import {environment} from 'src/environments/environment';
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

  getStatistics(botUuid: string, offset: number): Observable<Statistics> {
    let params = new HttpParams();

    // Begin assigning parameters

    params = params.append('offset', offset + '');
    return this.http.get<Statistics>(`${this.BASE_URL}/indicator/bot/${botUuid}`, {params})
  }
}
