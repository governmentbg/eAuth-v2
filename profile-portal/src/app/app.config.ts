import { environment } from '.././environments/environment';

export const appConfig = {
    apiUrl: environment.apiUrl,
    url: environment.url,
    localhostAuth: environment.localhostAuth,
    agreementsUrl: environment.agreementsUrl,
    baseURL: environment.baseURL,
    profileCallback: environment.profileCallback
};