import {LevelOfAssurance} from './enums/levelOfAssurance';
import {IdentityAttributes} from './identityAttributes';

export interface LoginResponse {
    inResponseTid: string;
    relayState: string;
    loa: LevelOfAssurance;
    providerId: string;
    attributes: IdentityAttributes [];
}
