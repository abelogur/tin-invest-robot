import {OrderReasonType} from "../types/orderReason.type";
import {TradeType} from "../types/trade.type";

export interface Order {
  price: number,
  commission: number,
  type: TradeType,
  action: string,
  time: string,
  reason: OrderReasonType
}
