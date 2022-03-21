import java.io.*;
import java.net.*;
import java.util.Scanner;
  
// Client class
public class client 
{
    public static void main(String[] args) throws IOException 
    {
        try
        {
            Scanner scn = new Scanner(System.in);
              
            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");
      
            // establish the connection with server port 3333
            Socket s = new Socket(ip, 3333);
      
            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      
            // the following loop performs the exchange of
            // information between client and client handler
            while (true) 
            {
                
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                  
                // If client sends exit,close this connection 
                // and then break from the while loop
                if(tosend.equals("exit"))
                {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
            }
              
            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}