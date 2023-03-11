package org.shyiko.client_server.final_project.protocol;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;

@Data
@AllArgsConstructor
public class Message implements Serializable {
    private final int wLen;
    private final int cType;
    private final int bUserId;
    private byte[] message;

    public Message(ByteBuffer buffer, int wLen) {
        this.wLen = wLen;
        this.cType = buffer.getInt();
        this.bUserId = buffer.getInt();
        this.message = new byte[wLen - Integer.BYTES * 2];
        buffer.get(this.message, 0, message.length);
    }

    public byte[] getMessageBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(message.length + Integer.BYTES * 2);
        buffer.putInt(cType);
        buffer.putInt(bUserId);
        buffer.put(message);
        byte[] msgBytes = new byte[buffer.capacity()];
        buffer.position(0);
        buffer.get(msgBytes, 0, msgBytes.length);
        return msgBytes;
    }

}
