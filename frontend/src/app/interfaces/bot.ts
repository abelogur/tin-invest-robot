import {BotEnvType} from "../types/botEnv.type";
import {OrderReasonType} from "../types/orderReason.type";

export interface Bot {
  uuid: string;
  start: string;
  strategy: string;
  botEnv: BotEnvType;
  instrument: string;
  instrumentTicket: string;
  numberOfOrders: number;
  profit: number;
  profitPercentage: number;
  currency: string;
  telegramBotChatId: string;
  errors: ErrorBot[];
}

export interface ErrorBot {
  reason: OrderReasonType,
  errorMessage: string;
  errorCode: string;
  instant: string;
}
