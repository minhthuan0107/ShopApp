import { RateResponse } from "./rate.response";

export interface RateListAdminResponse {
  rateResponses: RateResponse[];
  totalPages: number;
  totalItems: number;
  currentPage: number;
}