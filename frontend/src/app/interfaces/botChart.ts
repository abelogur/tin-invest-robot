export interface BotChart {
  points: Point[];
  indicators: {[key: string] : Point[]};
}

interface Point {
  value: number;
  time: string;
}
