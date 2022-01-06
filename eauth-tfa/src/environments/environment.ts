export const environment = {
	production: false,
	hmr: false,
	// apiEndpoint: '/api/',
	apiUrl: '/api/',
	url: 'https://eauth-test.egov.bg/tfauthbe/',
	qesUrl: 'http://eauth-test.egov.bg:4200/qes',
	cancelAuthEndpoint: '/tfauthbe/api/idp/cancel-auth',
	serviceProvider1Url: 'https://eauth-test.egov.bg/test-sp-low',
	serviceProvider2Url: 'https://eauth-test.egov.bg/test-sp-substaintial',
	serviceProvider3Url: 'https://eauth-test.egov.bg/test-sp-high',
	loginEndpoint: '/tfauthbe/api/idp/login',
	verifyUrl: '/tfauthbe/api/idp/verify-tfa',
	profileUrl: 'https://eauth-test.egov.bg/profilebe',
	invalidateSSO: 'https://eauth-test.egov.bg/tfauthbe/logout',
	napPikURL: 'https://eauth-test.egov.bg/tfauthbe/napeid-pik'
};
