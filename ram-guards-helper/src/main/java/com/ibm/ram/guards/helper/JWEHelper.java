package com.ibm.ram.guards.helper;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;


/**
 * @author seanyu
 */
public class JWEHelper {

    public static String generateJWEStringWithCekUsingRSA(String payloadContent, String publicKeyRelativePath, String cek) throws Exception {
        JWEAlgorithm alg = JWEAlgorithm.RSA_OAEP_256;
        EncryptionMethod enc = EncryptionMethod.A128CBC_HS256;
        JWEHeader header = new JWEHeader(alg, enc,
                null, null, null, null, null, null, null, null, null, null, null,
                CompressionAlgorithm.DEF, null, null, null, 0, null, null, null, null);
        Payload payload = new Payload(payloadContent);
        JWEObject jweObject = new JWEObject(header, payload);
        jweObject.encrypt(new RSAEncrypter(RSAHelper.readRsaPublicKey(publicKeyRelativePath), AESHelper.convertStringToSecretKey(cek)));
        return jweObject.serialize();
    }

    public static String generateJWEStringWithSymmetricKey(String payloadContent, String symmetricKey) throws Exception {
        JWEAlgorithm alg = JWEAlgorithm.DIR;
        EncryptionMethod enc = EncryptionMethod.A128CBC_HS256;
        JWEHeader header = new JWEHeader(alg, enc,
                null, null, null, null, null, null, null, null, null, null, null,
                CompressionAlgorithm.DEF, null, null, null, 0, null, null, null, null);
        Payload payload = new Payload(payloadContent);
        JWEObject jweObject = new JWEObject(header, payload);
        jweObject.encrypt(new DirectEncrypter(AESHelper.convertStringToSecretKey(symmetricKey)));
        return jweObject.serialize();
    }

    public static String decryptJWEPayloadWithCekOrSymmetricKey(String jweString, String cekOrSymmetricKey) throws Exception {
        JWEObject jwe = JWEObject.parse(jweString);
        jwe.decrypt(new DirectDecrypter(AESHelper.convertStringToSecretKey(cekOrSymmetricKey), true));
        return jwe.getPayload().toString();
    }

    public static String getJWEHeaderWithCekOrSymmetricKey(String jweString) throws Exception {
        JWEObject jwe = JWEObject.parse(jweString);
        return jwe.getHeader().toString();
    }

    public static String decryptJWEPayloadWithPrivateKey(String jweString, String privateKeyRelativePath) throws Exception {
        JWEObject jwe = JWEObject.parse(jweString);
        jwe.decrypt(new RSADecrypter(RSAHelper.readRsaPrivateKey(privateKeyRelativePath)));
        return jwe.getPayload().toString();
    }

}
