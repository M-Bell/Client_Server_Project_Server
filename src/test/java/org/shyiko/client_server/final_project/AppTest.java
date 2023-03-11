package org.shyiko.client_server.final_project;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shyiko.client_server.final_project.dao.ProductStorage;
import org.shyiko.client_server.final_project.models.Product;
import org.shyiko.client_server.final_project.protocol.Packet;
import org.shyiko.client_server.final_project.util.*;
import org.shyiko.client_server.final_project.util.crypting.Encryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AppTest {
    ProductStorage storage = ProductStorage.getInstance();

    @BeforeClass
    public static void reset() {
        DBUtil.setDBForTest();

    }

    @Test
    public void addNewProductsTest() {
        int repeats = 50;
        int allProd = storage.listProducts(Criteria.NAME, "", SortingType.ASC).size();
        try {
            InetAddress addr = InetAddress.getByName(null);
            for (int i = 0; i < repeats; ++i) {
                Packet sendingPacket = ConverterUtil.packBytes(Command.CREATE_PRODUCT.getValue(), ModelParser.parseProductToJSON(new Product(0, "Prod spam " + i, "", "", 10, 10, 1)).getBytes(StandardCharsets.UTF_8));
                encrypt(sendingPacket);
                Thread client = new Thread(new ClientTestThread(addr, sendingPacket.getAllData()));
                client.start();
                client.join();
            }
        } catch (UnknownHostException e) {
            assert (false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            int realAmount = storage.listProducts(Criteria.NAME, "", SortingType.ASC).size();
            int expected = repeats + allProd;
            assert (expected == realAmount);
        }
    }

    private void encrypt(Packet sendingPacket) {
        try {
            Encryptor.encrypt(sendingPacket.getBMsg());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        sendingPacket.setWLen(sendingPacket.getBMsg().getMessage().length + Integer.BYTES * 2);
    }

}
