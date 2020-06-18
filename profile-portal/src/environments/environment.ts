// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: true,
  hmr       : false,
  // apiEndpoint: '/api/',
  apiUrl: 'https://eauth.egov.bg/profilebe/api/',
  url: '',
  baseURL: 'https://eauth.egov.bg',
  localhostAuth: 'http://eauth.egov.bg:4201/auth/token',
  agreementsUrl: 'https://unifiedmodel.egov.bg/wps/wcm/connect/unifiedmodel.egov.bg-1196/d84bdd52-f525-4d54-8798-583dd164a10e/GC_eAuthentication+.pdf?MOD=AJPERES&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh&CVID=mOVIFIh',
  profileCallback: '/eaft/authorize'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
