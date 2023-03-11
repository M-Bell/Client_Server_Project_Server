package org.shyiko.client_server.final_project.util;

import org.shyiko.client_server.final_project.protocol.Packet;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConverterUtil {
    public static Object convertBytesToPOJO(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(new String(bytes).getBytes(StandardCharsets.UTF_8));
        ObjectInput in;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] convertPOJOToBytes(Serializable product) {
        ObjectOutputStream out;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            out = new ObjectOutputStream(bos);
            out.writeObject(product);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static Packet packBytes(byte[] message) {
        return packBytes(0, message);
    }

    public static Packet packBytes(int command, byte[] message) {
        return new Packet((byte) 0, 0, command, 0, message);
    }
}
