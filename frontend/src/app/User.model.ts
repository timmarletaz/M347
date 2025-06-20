export interface UserModel {
  firstname: string;
  lastname: string;
  email: string;
}

export interface LoginResponse {
  token: string;
  expires: Date;
  user: UserModel;
}
