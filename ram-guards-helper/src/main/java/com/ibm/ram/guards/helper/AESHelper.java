package com.ibm.ram.guards.helper;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.nimbusds.jose.EncryptionMethod.A128CBC_HS256;

/**
 * @author seanyu
 */
public class AESHelper {


    public static String convertSecretKeyToString(SecretKey secretKey){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static SecretKey convertStringToSecretKey(String secretKey){
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String generateSecretKeyString(){

        return convertSecretKeyToString(generateSecretKey());
    }

    public static SecretKey generateSecretKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(A128CBC_HS256.cekBitLength());
        return keyGen.generateKey();
    }

    public static void main(String[] args) {
        String key = generateSecretKeyString();
        System.out.println(key);
        convertStringToSecretKey(key);
    }
}
