import {Order} from "./order";

export interface Statistics {
  orders: Order[],
  profit: number,
  profitPercentage: number,
  commission: number,
  profitWithCommission: number,
  profitWithCommissionPercentage: number,
  usedMoney: number
}
