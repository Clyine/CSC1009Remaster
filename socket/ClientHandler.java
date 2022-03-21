package socket;

import datastore.*;
import terminal.*;

import java.io.*;
import java.net.*;


// THIS IS ATM TERMINAL
public class ClientHandler extends Thread {

    final DataInputStream dis;
    final DataOutputStream dos;
    //final Socket s;
    final DataStore d;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, DataStore d) {// dataref
        //this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.d = d;
    }

    @Override
    public void run() {// pass ref to run

        Pages menu = new Pages(this.dis, this.dos, this.d); //Initialise menus

        try{
            menu.authPage(); //generate login page
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Program spoilt");
            System.exit(1);
        }


        while (true) {
            try {
                menu.printPrompt(); //generate main menu prompts
                String selection = dis.readUTF(); //take input from Inputstream
                if (selection.equals("9")) { //if 9 is selected, break out of loop
                    break;
                } else {
                    switch (selection) {
                        case "1":
                            menu.balancePage(); //generate balance menu
                            break;
                        case "2":
                            menu.statementPage(); //generate statement menu
                            break;
                        case "3":
                            menu.depositPage(); //generate deposit menu
                            break;
                        case "4":
                            menu.withdrawPage(); //generate withdrawal menu
                            break;
                        case "5":
                            menu.transferPage(); //generate transfer menu
                            break;
                        case "6":
                            menu.detailsPage();
                            break;
                        default:
                            dos.writeUTF("\nInvalid Selection. Press enter to continue");
                            dis.readUTF();
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Client disconnected");
                return;
            }
        }

        try {
            System.out.println("Program successful, terminating..");
            this.dis.close();
            this.dos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
