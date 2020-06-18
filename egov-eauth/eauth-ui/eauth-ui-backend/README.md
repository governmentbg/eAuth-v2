## [Private signing key and public certificate] (#signing-keys)

The SAML Spring Security library needs a private DSA key / public certificate pair for the IdP / SP which can be re-generated
if you want to use new key pairs.

```bash
openssl req -subj '/O=Organization, CN=eAuth/' -newkey rsa:2048 -new -x509 -days 3652 -nodes -out eAuth.crt -keyout eAuth.pem
```

The Java KeyStore expects a pkcs8 DER format for RSA private keys so we have to re-format that key:

```bash
openssl pkcs8 -nocrypt  -in eAuth.pem -topk8 -out eAuth.der
```

Remove the whitespace, heading and footer from the eAuth.crt and eAuth.der:

```bash
cat eAuth.der |head -n -1 |tail -n +2 | tr -d '\n'; echo
cat eAuth.crt |head -n -1 |tail -n +2 | tr -d '\n'; echo
```

Above commands work on linux distributions. On mac you can issue the same command with `ghead` after you install `coreutils`:

```bash
brew install coreutils

cat eAuth.der |ghead -n -1 |tail -n +2 | tr -d '\n'; echo
cat eAuth.crt |ghead -n -1 |tail -n +2 | tr -d '\n'; echo
```

Add the eAuth key pair to the application.yml file:

```yml
idp:
  private_key: ${output from cleaning the der file}
  certificate: ${output from cleaning the crt file}
```