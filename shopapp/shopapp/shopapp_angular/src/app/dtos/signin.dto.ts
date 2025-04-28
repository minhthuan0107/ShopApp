
export class SigninDto {
    phone_number: string
    password: string
    constructor(data: any) {
        this.phone_number = data.phone_number;
        this.password = data.password;
    }
}