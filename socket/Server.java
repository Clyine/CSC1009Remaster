package socket;
import model.*;
import datastore.*;

import java.net.*;
import java.io.*;

public class Server {

    private static final int portNum = 3333;
    ServerSocket server;
    DataStore d;

    public Server() throws Exception{
        this.d = new DataStore();  //load csv into memory
        this.serverRun(d);
    }

    public void serverRun(DataStore d) throws Exception {
        server = new ServerSocket(portNum);
        System.out.println("Starting socket server at port : "+portNum);
        while (true) {
            Socket s = null;
            try {
                s = server.accept();
                System.out.println("A new client is connected : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                System.out.println("Assigning new thread for this client.....");
                Thread t = new ClientHandler(s, dis, dos, d);
                t.start();
            } catch (Exception e) {
                s.close();
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
