package org.shyiko.client_server.final_project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTestThread implements Runnable {
    private boolean canRun = true;
    private static byte id = 0;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private final InetAddress serverAddress;
    private byte[] packet;

    public ClientTestThread(InetAddress addr, byte[] packet) {
        System.out.println("Starting client " + (++id));
        this.packet = packet;
        serverAddress = addr;
        int reconnectTries = 0;
        while (reconnectTries++ < 10) {
            if (!tryConnect(serverAddress)) {
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                return;
            }
        }
        System.err.println("Server unavailable");
        canRun = false;
    }

    public boolean tryConnect(InetAddress addr) {
        try {
            socket = new Socket(addr, 9090);
        } catch (IOException e) {
            System.err.println("Cannot connect to server");
            return false;
        }
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e2) {
                System.err.println("Socket not closed");
            }
        }
        return true;
    }

    public boolean trySend(byte[] packet) {
        int reconnectTries = 0;
        while (reconnectTries++ < 5) {
            try {
                out.write(packet);
                out.flush();
                in.read();
            } catch (IOException e) {
                if (!tryConnect(serverAddress)) {
                    synchronized (Thread.currentThread()) {
                        try {
                            Thread.currentThread().wait(3000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    continue;
                }
                System.out.println("Connection established");
                reconnectTries = 0;
                return false;
            }
        }
        return true;
    }


    @Override
    public void run() {
        if (!canRun) return;
        synchronized (Thread.currentThread()) {
            try {

                if (trySend(packet)) {
                    System.out.println("Packet sent");
                } else {
                    System.err.println("Packet not sent");
                }

            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Socket not closed");
                }
            }

        }
    }
}
