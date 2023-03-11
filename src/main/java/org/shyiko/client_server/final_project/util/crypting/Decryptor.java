package org.shyiko.client_server.final_project.util.crypting;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Decryptor {
    public static byte[] decrypt(byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CryptingUtil.TRANSFORMATION);
        SecretKey secretKey = new SecretKeySpec(CryptingUtil.KEY.getBytes(StandardCharsets.UTF_8), CryptingUtil.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(message);
    }
}
