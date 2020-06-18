module.exports = {
  'env': {
    'browser': true,
    "node": true,
    'es6': true,
    "jasmine": true
  },
  'extends': [
    "eslint:recommended",
    "google",
  ],
  'globals': {
    'Atomics': 'readonly',
    'SharedArrayBuffer': 'readonly',
  },
  'parser': '@typescript-eslint/parser',
  'parserOptions': {
    'ecmaVersion': 2018,
    'sourceType': 'module',
  },
  'plugins': [
    '@typescript-eslint'
  ],
  "overrides": [
    {
      "files": [
        "**/*.spec.js",
        "**/*.spec.jsx"
      ]
    }
  ],
  'rules': {
    "require-jsdoc": ["off"],
    "new-cap": ["error", { "capIsNewExceptions": 
      [
        "NgModule", 
        "Injectable", 
        "Component", 
        "ViewChild", 
        "Output", 
        "Input",
        "HostListener",
        "Inject"] }
      ],
    'max-len': ["error", { "code": 85, "ignoreComments": true }],
    "object-curly-spacing": "off",
    "comma-dangle": "off",
    "indent": ["error", "tab"],
    "no-tabs": ["error", { "allowIndentationTabs": true }],
    "no-unused-vars": "off",
    "@typescript-eslint/no-unused-vars": "error",
    "operator-linebreak": ["error", "after", { "overrides": { "?": "ignore", ":": "ignore" } }],
    "new-cap": ["error", { "capIsNew": false }]
  },
};
