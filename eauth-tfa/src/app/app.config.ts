import {environment} from '.././environments/environment';

export const appConfig = {
	apiUrl: environment.apiUrl,
	url: environment.url,
	qesUrl: environment.qesUrl,
	cancelAuthEndpoint: environment.cancelAuthEndpoint,
	serviceProvider1Url: environment.serviceProvider1Url,
	serviceProvider2Url: environment.serviceProvider2Url,
	serviceProvider3Url: environment.serviceProvider3Url,
	loginEndpoint: environment.loginEndpoint,
	verifyUrl: environment.verifyUrl,
	profileUrl: environment.profileUrl,
};
