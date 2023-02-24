/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericnode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Blob;
import java.util.AbstractMap.SimpleEntry;

import genericnode.RMI.*;
import java.io.*;
import java.util.*;
import java.net.*;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap.SimpleEntry;


/**
 *
 * @author wlloyd
 */
public class GenericNode 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
         HashMap<String, String>  data = new HashMap<>();

        if (args.length > 0)
        {
            if (args[0].equals("rmis"))
            {
                

                try {


                    Operations obj = new Operations();
                    LocateRegistry.createRegistry(6600);
                    Naming.rebind("rmi://localhost:6600/Add", obj);


                    System.out.println("Server ready");

                } catch (Exception e) {
                    System.out.println("Server exception: " + e.toString());
                    e.printStackTrace();
                }

            }
            if (args[0].equals("rmic"))
            {
                System.out.println("RMI CLIENT");
                String addr = args[1];
                String cmd = args[2];
                String key = (args.length > 3) ? args[3] : "";
                String val = (args.length > 4) ? args[4] : "";

                try {
                    String str = "";
                    Hello hello = (Hello) Naming.lookup("rmi://" + addr + ":6600/Add");

                    if (cmd.equals("put")) {
                        str = hello.put(key, val);



                    }
                    if (cmd.equals("get")) {
                        str = hello.get(key);

                    }
                    if (cmd.equals("store")) {
                        str = hello.store();
                    }
                    if (cmd.equals("del")) {
                        str = hello.del(key);
                    }
                    if (cmd.equals("exit")) {

                        hello.exit();
                    }
                    System.out.println(str);

                } catch (Exception e) {
                    System.out.println("Client exception: " + e.toString());
                    e.printStackTrace();
                }

           
            }
            if (args[0].equals("tc"))
            {
                System.out.println("TCP CLIENT");
                String addr = args[1];
                int port = Integer.parseInt(args[2]);
                String cmd = args[3];
                String key = (args.length > 4) ? args[4] : "";
                String val = (args.length > 5) ? args[5] : "";
            try {
                Socket s = new Socket(addr, port);
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF(cmd+" "+key+"+"+val);
                DataInputStream input = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                String br= input.readUTF();
                System.out.println("Server says: "+br);
                s.close();
            }
            catch (IOException u) {
                System.out.println(u);
            }
        
            }

            if (args[0].equals("ts"))
            {
                System.out.println("TCP SERVER");
                int port = Integer.parseInt(args[1]);
                ServerSocket s =new ServerSocket(port);
               try{
                while(true){
                Socket socket= s.accept();
                DataInputStream dout = new DataInputStream(socket.getInputStream());
                DataOutputStream output= new DataOutputStream(socket.getOutputStream());
                String string= dout.readUTF();
                System.out.println("Connection Established");
                String cmd=string.substring(0,string.indexOf(" "));
               
                String key=string.substring(string.indexOf(" "),string.indexOf("+"));
                String value=string.substring(string.indexOf("+"));
                Operations ops = new Operations();
                String str="data does not found";
                Boolean b=false;
                if(cmd.equals("put") )
                {
	            System.out.println("put....");
				 data.put(key, value);
                 str= "server response:put key=" +key;
                }
				else if (cmd.equals("get")){
                    
                    str ="server response:put key=" +key +"get="+data.get(key);
				 }
				else if(cmd.equals("del")){
			
				 data.remove(key);
                 str="server response:delete key="+key ;
		 
				}
				else if(cmd.equals("store")){
				 
                    for(Map.Entry<String,String> map : data.entrySet()){
                         str+="\n"+"key:" +map.getKey() + "value:" +map.getValue();
                        b=true; 
                        
                    }
                }
                else if(cmd.equals("exit")){
			
                    s.close();
                                    
                    str="Server is closing..." ;
                    break;
            
                   }
               
                if(b==true){
		              str ="server response:"+str;
                    }
                   
                    
                output.writeUTF(str);
                System.out.println("Data sent to client"+str);
                    
            }
            s.close();
                
            } catch(SocketException e)
                 {
                     System.out.println("Server is closing");
                 }
                     
        }
        
            if (args[0].equals("uc"))
            {
                
                System.out.println("UDP CLIENT");
                String cmd = args[3];
                String key = (args.length > 4) ? args[4] : "";
                String val = (args.length > 5) ? args[5] : "";
                int sendport = Integer.parseInt(args[2]);
                int recvport = sendport + 1;
                String addr = args[1];
                try{
                DatagramSocket Socket = new DatagramSocket(sendport);
                System.out.println("CLIENT IS READY");
                String sendData=key+","+val+","+cmd;
                byte[] b = sendData.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(b,b.length,InetAddress.getByName(addr),recvport);
                System.out.println("Sent packet on sendport");
                Socket.send(sendPacket);
                System.out.println("Packet is sent");
                byte[] receiveData= new byte[65535];
                if(!cmd.contains("exit")){
                DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
                Socket.receive(receivePacket);
                System.out.println("Packet Received");
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + modifiedSentence);
                Socket.close();
                }
                else{
                    
                    Socket.close();
                    System.out.println("Client is closing...");


                }
                }catch(SocketException e)
                {
                    System.out.println("Client is closing...");
                }
              
                
                
               
            }
                

            
        
            if (args[0].equals("us"))
            {
                System.out.println("UDP SERVER");
                int port1 = Integer.parseInt(args[1]);
                int sendport=port1+1;  
                DatagramSocket serverSocket = new DatagramSocket(sendport);
                System.out.println("SERVER IS READY");
            try{
                while(true)
                {
                byte[] receiveData =new byte[65535];
                byte[] sendData = new byte[65535];
                System.out.println("Checkpoint 1 crossed");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                System.out.println("Listening on port 1");
                serverSocket.receive(receivePacket);
                System.out.println("Packet Received");
                String cmd="";
                String string = new String(receivePacket.getData());
                 // String key=string.substring(0,string.indexOf(" "));
                //  String value=string.substring(string.indexOf(" "),string.indexOf("+"));
                //   cmd=string.substring((string.indexOf("+"))+1);
                   String[] array = string.toString().split("[,]", 0);

                        String key=array[0];
                         String value=array[1];
                         cmd=array[2];
                  
                 System.out.println("The given key is "+array[0]);
                 System.out.println("The given value is      "+array[1]);
                System.out.println("The given command is "+array[2]);
                String p="put";
               
                  String str="data does not found";
                  System.out.println("put".equals(array[2]));
                //   System.out.println(array[2]);
                //   System.out.println(array[2].toString().equals(p.toString()));
                //   System.out.println(array[2].toString()=="put");
                  
                Boolean b=false;
                  if(array[2].contains("put"))
                 {
                  System.out.println("put....");
                   data.put(array[0], array[1]);
                   str= "server response:put key=" +array[1];
                  
                  }
                
            
                   else if (array[2].contains("get")){
                     System.out.println("put....");
                      
                       str ="server response:put key=" +key +"get="+data.get(key);
                    }
                   else if(array[2].contains("del")){
              
                    data.remove(key);
                    str="server response:delete key="+key ;
           
                   }
                   else if(array[2].contains("store")){
                       for(Map.Entry<String,String> map : data.entrySet()){
                            str+="\n"+"key:" +map.getKey() + "value:" +map.getValue();
                           b=true; 
                          
                       }
                       // Boolean b=false;
                       if(b==true){
                        str ="server response:"+str;
                      }
                     }
                       else if(array[2].contains("exit")){
                        serverSocket.close();
                       str="exit";

                        }
                       
                sendData=str.getBytes();
             InetAddress ip = InetAddress.getLocalHost();
                   DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,ip,port1);
                   serverSocket.send(sendPacket);
                    
            }
            
            }
            catch(SocketException e)
                {
                   System.out.println("Socket is closed");
                }
        
            
          }
        }
    
        else
        {
            String msg = "GenericNode Usage:\n\n" +
                         "Client:\n" +
                         "uc/tc <address> <port> put <key> <msg>  UDP/TCP CLIENT: Put an object into store\n" + 
                         "uc/tc <address> <port> get <key>  UDP/TCP CLIENT: Get an object from store by key\n" + 
                         "uc/tc <address> <port> del <key>  UDP/TCP CLIENT: Delete an object from store by key\n" + 
                         "uc/tc <address> <port> store  UDP/TCP CLIENT: Display object store\n" + 
                         "uc/tc <address> <port> exit  UDP/TCP CLIENT: Shutdown server\n" + 
                         "rmic <address> put <key> <msg>  RMI CLIENT: Put an object into store\n" + 
                         "rmic <address> get <key>  RMI CLIENT: Get an object from store by key\n" + 
                         "rmic <address> del <key>  RMI CLIENT: Delete an object from store by key\n" + 
                         "rmic <address> store  RMI CLIENT: Display object store\n" + 
                         "rmic <address> exit  RMI CLIENT: Shutdown server\n\n" + 
                         "Server:\n" +
                         "us/ts <port>  UDP/TCP SERVER: run udp or tcp server on <port>.\n" +
                         "rmis  run RMI Server.\n";
            System.out.println(msg);
        }
        
        
    }
}

    
    
            
        


