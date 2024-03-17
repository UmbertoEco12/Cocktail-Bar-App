package com.example.lso_project.Helpers;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.biometric.BiometricPrompt;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class BiometricAuthHelper {

//    private Executor executor;
//    private BiometricPrompt biometricPrompt;
//    private BiometricPrompt.PromptInfo promptInfo;

    private final static String TAG = "BiometricAuthHelper";

    private final static String KEY = "1234567890";

    public static void generateKey()
    {
        try{
            generateSecretKey(new KeyGenParameterSpec.Builder(KEY,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    // Invalidate the keys if the user has registered a new biometric
                    // credential, such as a new fingerprint. Can call this method only
                    // on Android 7.0 (API level 24) or higher. The variable
                    // "invalidatedByBiometricEnrollment" is true by default.
                    .setInvalidatedByBiometricEnrollment(true)
                    .build());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    private void StoreBiometric(BiometricPrompt.AuthenticationResult result, IHandleServerResponse response)
//    {
//        // get byte data from biometric result
//        byte[] biometricData = result.getCryptoObject().getCipher().getIV();
//        String hash = generateHash(biometricData);
//
//        // store hash with current user id
//    }
//
//    private void compareBiometric(BiometricPrompt.AuthenticationResult result)
//    {
//        // get byte data from biometric result
//        byte[] biometricData = result.getCryptoObject().getCipher().getIV();
//        String hash = generateHash(biometricData);
//
//        // compare hash with stored hash
//    }
//
//    public static void loginBiometric(BiometricPrompt.AuthenticationResult result, IHandleServerResponse response)
//    {
//        // get byte data from biometric result
//        byte[] biometricData = result.getCryptoObject().getCipher().getIV();
//        String hash = generateHash(biometricData);
//        // check login on server
//        SocketLoginBiometricRequest.getInstance().loginBiometricRequest(hash, response);
//    }
//
//    public static void insertBiometric(BiometricPrompt.AuthenticationResult result, IHandleServerResponse response)
//    {
//        // get byte data from biometric result
//        byte[] biometricData = result.getCryptoObject().getCipher().getIV();
//        String hash = generateHash(biometricData);
//        Log.d(TAG, String.format("%d", hash.length()));
//        // check login on server
//        SocketInsertUserBiometricRequest.getInstance().insertBiometricRequest(hash, response);
//    }
//    public static String generateHash(byte[] data) {
//        try {
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//            byte[] hashBytes = messageDigest.digest(data);
//            return new String(hashBytes);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    public static SecretKey getSecretKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null);
        return ((SecretKey)keyStore.getKey(KEY, null));
    }

    public static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }
}
