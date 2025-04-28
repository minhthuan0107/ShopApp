export class SignupDto {
    fullname: string;
    phone_number: string;
    address: string;
    password: string;
    retype_password: string;
    date_of_birth: Date;
    facebook_account_id: number=0;
    google_account_id: number=0;
    constructor(data: any) {
        this.fullname = data.fullname;
        this.phone_number = data.phone_number;
        this.address = data.address;
        this.password = data.password;
        this.retype_password = data.retype_password;
        this.date_of_birth = data.date_of_birth;
        this.google_account_id = data.google_account_id || 0;
        this.facebook_account_id= data.facebook_account_id || 0;
    }
}
