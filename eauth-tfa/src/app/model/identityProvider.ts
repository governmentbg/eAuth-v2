import {LevelOfAssurance} from './enums/levelOfAssurance';
import {AuthenticationAttribute} from './authenticationAttribute';

export interface IdentityProvider {
    id: string;
    name: string;
    tfaRequierd: boolean;
    loa: LevelOfAssurance;
    attributes: AuthenticationAttribute[];
}
