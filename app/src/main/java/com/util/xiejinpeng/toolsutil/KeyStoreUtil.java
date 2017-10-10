package com.util.xiejinpeng.toolsutil;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class KeyStoreUtil {

    private static KeyStore keyStore = initKeyStore();

    public static KeyStore initKeyStore() {
        if (keyStore != null)
            return keyStore;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    //get all keys

    public static List<String> getKeyAliases() {
        List<String> keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyAliases;
    }

    public static boolean isAliasExist(String alias) {
        for (int i = 0; i < getKeyAliases().size(); i++) {
            if (TextUtils.equals(getKeyAliases().get(i),alias))
                return true;
        }
        return false;
//        return Stream.of(getKeyAliases())
//                .anyMatch(a -> TextUtils.equals(a, alias));
    }

    //generate key pair

    public static void generateNewKeys(Context context, String alias) {

        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=CN, O=Android Authority , C=COMPANY"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator =
                        KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //delete key pair

    public static void deleteKey(final String alias) {
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    //Encryption

    public static String encryptString(String encryptStr, String alias) {
        try {

            if (TextUtils.isEmpty(encryptStr)) {
                return encryptStr;
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();


            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(encryptStr.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();

            String encryptedStr = Base64.encodeToString(vals, Base64.DEFAULT);
            return encryptedStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptStr;
    }

    //Decryption

    public static String decryptString(String cipherStr, String alias) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
//            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherStr, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            String decryptStr = new String(bytes, 0, bytes.length, "UTF-8");

            return decryptStr;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherStr;
    }


}