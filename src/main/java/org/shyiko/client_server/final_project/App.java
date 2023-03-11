package org.shyiko.client_server.final_project;

import org.shyiko.client_server.final_project.dao.ProductStorage;
import org.shyiko.client_server.final_project.util.DBUtil;
import org.shyiko.client_server.final_project.util.NetworkUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


/**
 * Hello world!
 */
public class App   {
    ProductStorage storage = ProductStorage.getInstance();
    private static int b;
    static ServerSocket s = null;

    public static void main(String[] args) {
        DBUtil.setDBForTest();
        try {
            s = new ServerSocket(NetworkUtil.PORT);
            System.out.println("Server started");
            while (true) {
                Socket socket = s.accept();
                new Thread(new ServerProcess(socket)).start();
            }
        } catch (SocketTimeoutException e) {
            try {
                System.err.println("Server waits for client for too long...\n" +
                        "Server shuts down...");
                if (s != null) {
                    s.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            if (s != null && !s.isClosed())
                s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


