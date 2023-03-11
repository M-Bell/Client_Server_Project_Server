package org.shyiko.client_server.final_project.protocol;

import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;

@Data
public class Packet implements Serializable {
    private final static int PACKET_HEADER_SIZE = 14;
    private byte bMagic = 0x13;
    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16;
    private Message bMsg;
    private short wMsgCRC16;

    public Packet(ByteBuffer inputByteBuffer) throws NoMagicByteException {
        if (this.bMagic != inputByteBuffer.get()) {
            throw new NoMagicByteException("No magic byte");
        }
        this.bSrc = inputByteBuffer.get();
        this.bPktId = inputByteBuffer.getLong();
        this.wLen = inputByteBuffer.getInt();
        this.wCrc16 = inputByteBuffer.getShort();
        this.bMsg = new Message(inputByteBuffer, this.wLen);
        this.wMsgCRC16 = inputByteBuffer.getShort();
    }

    public Packet(byte[] inputBytes) throws NoMagicByteException {
        ByteBuffer inputByteBuffer = ByteBuffer.wrap(inputBytes);
        if (this.bMagic != inputByteBuffer.get()) {
            throw new NoMagicByteException("No magic byte");
        }
        this.bSrc = inputByteBuffer.get();
        this.bPktId = inputByteBuffer.getLong();

        this.wLen = inputByteBuffer.getInt();
        this.wCrc16 = inputByteBuffer.getShort();
        this.bMsg = new Message(inputByteBuffer, this.wLen);
        this.wMsgCRC16 = inputByteBuffer.getShort();
    }

    public Packet(byte clientId, long pktID, int commandType, int userId, byte[] msg) {
        this.bSrc = clientId;
        this.bPktId = pktID;
        this.wLen = msg.length + Integer.BYTES * 2;
        this.wCrc16 = CRC16.getCRC16(getHeaderBytes());
        this.bMsg = new Message(this.wLen, commandType, userId, msg);
        this.wMsgCRC16 = CRC16.getCRC16(bMsg.getMessageBytes());
    }


    public boolean checkCRC16() {
        return checkHeaderCRC16() && checkMessageCRC16();
    }

    private boolean checkHeaderCRC16() {
        byte[] headerBytes = getHeaderBytes();
        short newCrc16 = CRC16.getCRC16(headerBytes);
        return newCrc16 == this.wCrc16;
    }

    private boolean checkMessageCRC16() {
        byte[] msgBytes = this.getBMsg().getMessageBytes();
        short newCrc16 = CRC16.getCRC16(msgBytes);
        return newCrc16 == this.wMsgCRC16;
    }

    private byte[] getHeaderBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_HEADER_SIZE);
        buffer.put(bMagic);
        buffer.put(bSrc);
        buffer.putLong(bPktId);
        buffer.putInt(wLen);
        byte[] headerBytes = new byte[buffer.capacity()];
        buffer.position(0);
        buffer.get(headerBytes, 0, headerBytes.length);
        return headerBytes;
    }

    public byte[] getAllData() {
        ByteBuffer buffer = ByteBuffer.allocate(18 + getBMsg().getMessage().length + Integer.BYTES * 2);
        buffer.put(bMagic);
        buffer.put(bSrc);
        buffer.putLong(bPktId);
        buffer.putInt(wLen);
        buffer.putShort(wCrc16);
        buffer.putInt(bMsg.getCType());
        buffer.putInt(bMsg.getBUserId());
        buffer.put(bMsg.getMessage());
        buffer.putShort(wMsgCRC16);
        byte[] allBytes = new byte[buffer.capacity()];
        buffer.position(0);
        buffer.get(allBytes);
        return allBytes;
    }
}
