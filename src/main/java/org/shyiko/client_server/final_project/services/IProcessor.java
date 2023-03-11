package org.shyiko.client_server.final_project.services;

import org.shyiko.client_server.final_project.protocol.Message;

public interface IProcessor {
    String process(Message message);
}
