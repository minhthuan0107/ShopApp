import { User } from "../../models/user.model";

export interface UserListAdminResponse {
  userResponses: User[];
  totalPages: number;
  totalItems: number;
  currentPage: number;
}