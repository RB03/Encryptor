package com.burman.rohit.encryptor.algo;

/**
 * Created by Rohit on 2/13/2016.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Rohit
 */
public class Crypto {
    private  String password;
    private  int keySize;
    private  SecretKeyFactory factory;
    private  Cipher cipher;

    public Crypto(String password, int keySize) throws Exception {
        this.password = password;
        this.keySize = keySize;
        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    public boolean encrypt(File inputFile, File outputFile, Boolean delete) throws Exception {
        FileInputStream inFile = new FileInputStream(inputFile);
        FileOutputStream outFile = new FileOutputStream(outputFile);

        // generate random bytes to use as salt
        byte[] salt = new byte[8];
        Random rnd = new Random();
        rnd.nextBytes(salt);

        SecretKey secret = makeKey(password, salt, keySize);
        cipher.init(Cipher.ENCRYPT_MODE, secret);

        // write salt and IV at the beginning of file
        outFile.write(salt);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        outFile.write(iv);

        // encrypt data
        useKeyOnData(inFile, outFile);

        inFile.close();
        outFile.flush();
        outFile.close();

        if (delete) inputFile.delete();
        return true;
    }

    public boolean decrypt(File inputFile, File outputFile, Boolean delete) throws Exception {
        FileInputStream inFile = new FileInputStream(inputFile);
        FileOutputStream outFile = new FileOutputStream(outputFile);

        // read salt and IV from the beginning of file
        byte[] salt = new byte[8];
        inFile.read(salt);
        byte[] iv = new byte[16];
        inFile.read(iv);

        SecretKey secret = makeKey(password, salt, keySize);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

        // decrypt data
        useKeyOnData(inFile, outFile);

        inFile.close();
        outFile.flush();
        outFile.close();
         if (delete) inputFile.delete();
        return true;
    }

    // make key from password and salt
    private SecretKey makeKey(String password, byte[] salt, int keysize) throws InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, keysize);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private void useKeyOnData(FileInputStream inFile, FileOutputStream outFile) throws Exception {
        byte[] input = new byte[64];
        int bytesRead;
        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null) {
                outFile.write(output);
            }
        }

        byte[] output = cipher.doFinal();
        if (output != null) {
            outFile.write(output);
        }
    }
}
