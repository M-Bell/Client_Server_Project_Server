package org.shyiko.client_server.final_project.services;

import java.net.InetAddress;

public interface ISender {
    void send(byte[] message, InetAddress target);
}
