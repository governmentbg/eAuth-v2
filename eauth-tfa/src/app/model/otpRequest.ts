import {TfaMethod} from './enums/tfaMethod';

export class OtpRequest {
    tId = '';
    code: string;
    method: TfaMethod
}
