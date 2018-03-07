## Basics

TLS/SSL are cryptographic protocols used to encrypt computer network communication.

### TLS Scenarios

1. Server is authenticated by the client only +
2. Client is authenticated by server additionally to a) (*Mutual Authentication*)

### Terms

*Keystore (JKS, PKCS12)*

Contains private key(s) and certificate(s) used by TLS/SSL servers/clients to authenticate themselves to the other party.

*Truststore (JKS)*

Contains certificates of trusted servers/clients or trusted CAs, no key(s) contained.

### Appendix A: Using `keytool` for creating your own stores

See this tutorial for more details: https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6er/

#### Create your own KeyStore in JKS format

1. CA Create: https://stackoverflow.com/questions/21297139/how-do-you-sign-certificate-signing-request-with-your-certification-authority

2. *Create PrivateKey:* keytool -keystore clientkeystore -genkey -alias client
*Create Certificate signing request:* keytool -keystore clientkeystore -certreq -alias client -keyalg rsa -file client.csr
3. *Sign the CSR*: openssl  x509  -req  -CA ca-certificate.pem.txt -CAkey ca-key.pem.txt -in client.csr -out client.cer  -days 365  -CAcreateserial

4. keytool -import -keystore clientkeystore -file ca-certificate.pem.txt
5. keytool –import –keystore clientkeystore –file client.cer –alias client -alias theCARoot


####  Creating a TrustStore

Simply add your CAs to a keystore...

1. keytool -import -file ca.cert -alias firstCA -keystore myTrustStore