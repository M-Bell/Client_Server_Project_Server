package org.shyiko.client_server.final_project;

import org.shyiko.client_server.final_project.services.Receiver;

import java.io.IOException;
import java.net.Socket;

public class ServerProcess implements Runnable {
    private Receiver receiver;
    private final Socket socket;

    public ServerProcess(Socket socket) throws IOException {
        this.socket = socket;
        receiver = new Receiver(socket);
    }

    @Override
    public void run() {
        try {
            while (true) {
                receiver.receiveMessage();
            }
        } finally {
            try {
                receiver.closeConnect();
            } catch (IOException e) {
                System.err.println("Socket not closed...");
            }
        }
    }
}
