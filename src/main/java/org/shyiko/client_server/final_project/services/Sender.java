package org.shyiko.client_server.final_project.services;

import org.shyiko.client_server.final_project.protocol.Packet;
import org.shyiko.client_server.final_project.util.ConverterUtil;
import org.shyiko.client_server.final_project.util.crypting.Encryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Sender implements ISender {
    private final OutputStream oos;

    public Sender(Socket socket) throws IOException {
        this.oos = socket.getOutputStream();
    }

    @Override
    public void send(byte[] message, InetAddress target) {
        Thread sendingThread = new Thread(() -> {

            Packet sendingPacket = ConverterUtil.packBytes(message);
            encrypt(sendingPacket);
            try {
                oos.write(sendingPacket.getAllData());
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sendingThread.start();
        try {
            sendingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
