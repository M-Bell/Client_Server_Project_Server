package org.shyiko.client_server.final_project.services;

import org.shyiko.client_server.final_project.protocol.InvalidCRC16Exception;
import org.shyiko.client_server.final_project.protocol.NoMagicByteException;
import org.shyiko.client_server.final_project.protocol.Packet;
import org.shyiko.client_server.final_project.util.crypting.Decryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Receiver implements IReceiver {
    private final Processor processor;
    private final Sender sender;

    private final InputStream in;
    private final OutputStream out;
    private final Socket socket;

    public Receiver(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.processor = new Processor();
        this.sender = new Sender(socket);
    }

    @Override
    public void receiveMessage() {
        Thread receiverThread = new Thread(() -> {
            try {
                Packet receivedPacket = new Packet(readPacketBytes());
                decrypt(receivedPacket);
                System.out.println("---------------------------------------" + "\nMessage from client " + receivedPacket.getBSrc() + "\nCommand " + receivedPacket.getBMsg().getCType() + "\nMessage " + new String(receivedPacket.getBMsg().getMessage()) + "\n---------------------------------------");
                String response = processor.process(receivedPacket.getBMsg());
                sender.send(response.getBytes(), socket.getInetAddress());

            } catch (NoMagicByteException ignored) {
            } catch (SocketException e) {
                try {
                    closeConnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (InvalidCRC16Exception | IOException e) {
                String response = "Invalid packet";
                sender.send(response.getBytes(), socket.getInetAddress());

            }
        });
        receiverThread.start();
        try {
            receiverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void decrypt(Packet receivedPacket) throws InvalidCRC16Exception {
        try {
            receivedPacket.getBMsg().setMessage(Decryptor.decrypt(receivedPacket.getBMsg().getMessage()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        receivedPacket.setWLen(receivedPacket.getBMsg().getMessage().length + Integer.BYTES * 2);
        if (!receivedPacket.checkCRC16()) {
            throw new InvalidCRC16Exception("Invalid CRC16");
        }
    }

    private byte[] readPacketBytes() throws IOException {
        synchronized (in) {

            byte bMagic = (byte) in.read();
            if (bMagic != 0x13) return "1".getBytes();
            byte bSrc = (byte) in.read();
            long bPktId = ByteBuffer.wrap(in.readNBytes(Long.BYTES)).getLong();
            int wLen = ByteBuffer.wrap(in.readNBytes(Integer.BYTES)).getInt();
            short wCrc16 = ByteBuffer.wrap(in.readNBytes(Short.BYTES)).getShort();
            int cType = ByteBuffer.wrap(in.readNBytes(Integer.BYTES)).getInt();
            int bUserId = ByteBuffer.wrap(in.readNBytes(Integer.BYTES)).getInt();
            byte[] msg = ByteBuffer.wrap(in.readNBytes(wLen - Integer.BYTES * 2)).array();
            short wMsgCrc16 = ByteBuffer.wrap(in.readNBytes(Short.BYTES)).getShort();

            byte[] packet = new byte[18 + wLen];
            ByteBuffer buffer = ByteBuffer.allocate(18 + wLen);
            buffer.put(bMagic);
            buffer.put(bSrc);
            buffer.putLong(bPktId);
            buffer.putInt(wLen);
            buffer.putShort(wCrc16);
            buffer.putInt(cType);
            buffer.putInt(bUserId);
            buffer.put(msg);
            buffer.putShort(wMsgCrc16);
            int lastPos = buffer.position();
            buffer.position(0);
            buffer.get(packet, 0, lastPos);
            return packet;
        }
    }

    public void closeConnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
