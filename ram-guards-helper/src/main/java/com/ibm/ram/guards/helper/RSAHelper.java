package com.ibm.ram.guards.helper;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author seanyu
 */
public class RSAHelper {

    public static RSAPublicKey readRsaPublicKey(String relativePath) throws Exception {
        PemReader pemReader = new PemReader(new BufferedReader(new InputStreamReader(new ClassPathResource(relativePath).getInputStream())));
        PemObject pemObject = pemReader.readPemObject();
        pemReader.close();
        String suffix = relativePath.substring(relativePath.length()-3, relativePath.length());
        if ("crt".equals(suffix)){
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            InputStream input = new ByteArrayInputStream(pemObject.getContent());
            X509Certificate cert = (X509Certificate) fact.generateCertificate(input);
            return (RSAPublicKey)cert.getPublicKey();
        }else if ("pem".equals(suffix)){
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(pemObject.getContent()));
            return pubKey;
        }else {
            throw new UnsupportedOperationException();
        }
    }

    public static RSAPrivateKey readRsaPrivateKey(String relativePath) throws Exception {
        PemReader pemReader = new PemReader(new FileReader(new File(RSAHelper.class.getResource(relativePath).getPath())));
        PemObject pemObject = pemReader.readPemObject();
        pemReader.close();
        /* Add PKCS#8 formatting */
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(0));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
        v2.add(DERNull.INSTANCE);
        v.add(new DERSequence(v2));
        v.add(new DEROctetString(pemObject.getContent()));
        ASN1Sequence seq = new DERSequence(v);
        byte[] privKey = seq.getEncoded("DER");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(privKey));
    }

}
