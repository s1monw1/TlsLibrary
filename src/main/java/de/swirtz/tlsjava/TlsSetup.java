package de.swirtz.tlsjava;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class TlsSetup {
    public static void main(String[] args) throws IOException {
        StoreType truststore = new StoreType("src/test/resources/truststore.jks", "jks", "12345678", null);
        new TlsSetup().createSSLSocket("kotlinlang.org", 443, new TLSConfiguration("TLSv1.2", null, truststore));
    }

    public Socket createSSLSocket(String host, int port, TLSConfiguration tlsConfiguration) throws IOException {
        String tlsVersion = tlsConfiguration.getProtocol();
        StoreType keystore = tlsConfiguration.getKeystore();
        StoreType trustStore = tlsConfiguration.getTruststore();
        try {
            SSLContext ctx = SSLContext.getInstance(tlsVersion);
            TrustManager[] tm = null;
            KeyManager[] km = null;
            if (trustStore != null) {
                tm = getTrustManagers(trustStore.getFilename(), trustStore.getPassword().toCharArray(),
                        trustStore.getStoretype(), trustStore.getAlgorithm());
            }
            if (keystore != null) {
                km = createKeyManagers(keystore.getFilename(), keystore.getPassword(),
                        keystore.getStoretype(), keystore.getAlgorithm());
            }
            ctx.init(km, tm, new SecureRandom());
            SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            sslSocket.setSoTimeout(10_000);
            sslSocket.setEnabledProtocols(new String[]{tlsVersion});
            sslSocket.startHandshake();
            return sslSocket;
        } catch (Exception e) {
            throw new IllegalStateException("Not working :-(", e);
        }
    }


    private static TrustManager[] getTrustManagers(final String path, final char[] password,
                                                   final String storeType,
                                                   final String algorithm)
            throws Exception {
        TrustManagerFactory fac = TrustManagerFactory.getInstance(algorithm == null ? "SunX509" : algorithm);
        KeyStore ks = KeyStore.getInstance(storeType == null ? "JKS" : storeType);
        Path storeFile = Paths.get(path);
        ks.load(new FileInputStream(storeFile.toFile()), password);
        fac.init(ks);
        return fac.getTrustManagers();
    }

    private static KeyManager[] createKeyManagers(final String filename, final String password,
                                                  final String keyStoreType, final String algorithm) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance(keyStoreType == null ? "PKCS12" : keyStoreType);
        ks.load(new FileInputStream(filename), password.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm == null ? "SunX509" : algorithm);
        kmf.init(ks, password.toCharArray());
        return kmf.getKeyManagers();
    }
}


class TLSConfiguration {
    private final String protocol;
    private final StoreType keystore;
    private final StoreType truststore;

    public TLSConfiguration(String protocol, StoreType keystore, StoreType truststore) {
        this.protocol = protocol;
        this.keystore = keystore;
        this.truststore = truststore;
    }

    public String getProtocol() {
        return protocol;
    }

    public StoreType getKeystore() {
        return keystore;
    }

    public StoreType getTruststore() {
        return truststore;
    }

}

class StoreType {
    private final String filename;
    private final String storetype;
    private final String password;
    private final String algorithm;

    public StoreType(String filename, String storetype, String password, String algorithm) {
        this.filename = filename;
        this.storetype = storetype;
        this.password = password;
        this.algorithm = algorithm;
    }

    public String getFilename() {
        return filename;
    }


    public String getStoretype() {
        return storetype;
    }

    public String getPassword() {
        return password;
    }

    public String getAlgorithm() {
        return algorithm;
    }

}

