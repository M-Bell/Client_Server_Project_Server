package org.shyiko.client_server.final_project.protocol;

public class InvalidCRC16Exception extends Exception{
    public InvalidCRC16Exception(String msg){
        super(msg);
    }
}
