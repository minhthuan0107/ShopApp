export interface ApiResponse<T> {
    message: string;
    status: string;
    data: T;
  }