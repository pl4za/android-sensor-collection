package com.ubi.jason.sensorcollect;

import android.util.Log;

import com.ubi.jason.sensorcollect.helper.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by jason on 15-Mar-16.
 */
public class DataEncryptRSA {

    private static final String TAG = "DataEncryptRSA";

    PublicKey publicKey = null;
    String publicKeyString = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgH+K17wpy6MK/wNREyHwLF1vBxjJ\n" +
            "YKIJY7hdqnojVH1IzrbC85OAsqFmTM2qP/EfMFy9F5Ua1iEg0p46LO7AAZKfxE8G\n" +
            "FKdpFLNKCcrBHgLKz1kAdziXju16ExcDNzlMU+g3HNwQbjdcXxQnaXkjrPj8oEnl\n" +
            "9jKe3A5ROTViwMtjAgMBAAE=\n";

    public DataEncryptRSA() {
        publicKey = getPublicKeyFromString(publicKeyString);
        Log.i(TAG, "chave: "+publicKey);
    }

    public static PublicKey getPublicKeyFromString(String key) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] keyByte = decodeBASE64(key);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyByte);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return publicKey;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public String encrypt(String text) {
        String encryptedText;
        try {
            byte[] cipherText = encrypt(text.getBytes("UTF8"), publicKey);
            encryptedText = encodeBASE64(cipherText);
            return encryptedText;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static byte[] encrypt(byte[] text, PublicKey key) {
        try {
            byte[] cipherText = null;
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // encrypt the plaintext using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
            return cipherText;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private static String encodeBASE64(byte[] bytes) {
        // BASE64Encoder b64 = new BASE64Encoder();
        // return b64.encode(bytes, false);
        return Base64.encodeToString(bytes, true);
    }

    private static byte[] decodeBASE64(String text) {
        try {
            return Base64.decode(text);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


}
