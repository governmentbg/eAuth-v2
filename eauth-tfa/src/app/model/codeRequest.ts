import {TfaMethod} from './enums/tfaMethod';

export class CodeRequest {
    ctId: string;
    newCodeType: TfaMethod;
}
