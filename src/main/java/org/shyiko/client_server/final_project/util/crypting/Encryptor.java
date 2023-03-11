package org.shyiko.client_server.final_project.util.crypting;

import org.shyiko.client_server.final_project.protocol.Message;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    public static byte[] encrypt(Message msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CryptingUtil.TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(CryptingUtil.KEY.getBytes(StandardCharsets.UTF_8), CryptingUtil.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(msg.getMessage());
        msg.setMessage(new byte[encrypted.length]);
        System.arraycopy(encrypted, 0, msg.getMessage(), 0, msg.getMessage().length);
        return msg.getMessage();
    }
}
