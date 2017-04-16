/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author shivam
 */
import com.creamsugardonut.HttpStaticFileServer;
import com.sun.net.httpserver.HttpServer;
import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
class host extends Thread{
    public void run(){
        try {
            HttpStaticFileServer obj=new HttpStaticFileServer();
        } catch (Exception ex) {
            Logger.getLogger(host.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class server {
 public static void main(String[] args) throws Exception
  {
      int port=3000;
      ServerSocket sersock = new ServerSocket(port); //create a socket
      System.out.println("Server is online on port: "+port); 
      Socket sock = sersock.accept(); //accept incoming clients                          
      BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in)); // reading from keyboard (keyRead object)	                      
      OutputStream ostream = sock.getOutputStream();  //OutputStream ostream = sock.getOutputStream();   
      PrintWriter pwrite = new PrintWriter(ostream, true);
      InputStream istream = sock.getInputStream(); // receiving from server ( receiveRead  object)
      BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
      String receiveMessage, sendMessage="";               
      while(true)
      {
        if((receiveMessage = receiveRead.readLine()) != null)
        {
           String msg1=receiveMessage.toString();
           String[] msg=msg1.split("_");
           //System.out.println("Credentionls Recieved: "+msg1);
           if(msg[0].equals("1")){
              String UserName=msg[1];
              String pass=msg[2];           
              sendMessage=dbmatchpass(UserName,pass);
           }
           else if(msg[0].equals("2")){
            String first=msg[1];
            String last=msg[2];
            String pass=msg[3];
            String gender=msg[4];
            dbenter(first,last,pass,gender);
           }
           else if(msg[0].equals("3")){
               String song=msg[1];
               sendMessage= dbsong(song);
           }
           else if(msg[0].equals("4")){
               int no=Integer.parseInt(msg[1]);
               sendMessage=dbsendsong(no);               
           }
           else if(msg[0].equals("5")){
               sendMessage="Streaming is stopping now";
              // hth.suspend();
           
           }
        }         
        pwrite.println(sendMessage);             
        pwrite.flush();
      }               
    }
       public static String dbsong(String song){
            Connection con;
            String ans="";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_proj","root","try");
            Statement stmt=con.createStatement();
            String cmd="Select * from songs where Song='"+song+"';";
            ResultSet rs=stmt.executeQuery(cmd);
            while(rs.next()){
                ans+=rs.getString("No");
                ans+="#";
                ans+=rs.getString("Song");
                ans+="#";
                ans+=rs.getString("Album");
                ans+="#";
                ans+=rs.getString("Genre");
                ans+="#";
                ans+=rs.getString("Year");
                ans+="_";
            }
        con.close();
        return ans;
	}catch(Exception e){
	System.out.println(e);
	}
        return "";       
       }
          static host hth=new host();
     public static String dbsendsong(int no){
        Connection con;
        String ans;
        try{
	Class.forName("com.mysql.jdbc.Driver");
	con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_proj","root","try");
	Statement stmt=con.createStatement();
        String cmd="Select * from song_location where No="+no+";";
        ResultSet rs=stmt.executeQuery(cmd);
        System.out.println(cmd);
        if (rs.next()){
            String loc=rs.getString("Location");
         

            hth.setDaemon(true);
            hth.start();
            String sendToClient="http://127.0.0.1:8283/songs/"+loc;
            System.out.println(sendToClient);
            return sendToClient;
        }
        con.close();
	}catch(Exception e){
	System.out.println(e);
	}
         return "";
         }  
     public static String dbenter(String first,String last,String pass, String gender){
         Connection con;
        String ans;
        try{
	Class.forName("com.mysql.jdbc.Driver");
	con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_proj","root","try");
	Statement stmt=con.createStatement();
        String cmd="insert into users values('"+first+"','"+last+"','"+pass+"','"+gender+"');";
        System.out.println(cmd);
        int rs=stmt.executeUpdate(cmd);
        if(rs==1){
            ans="Welcome";
        }
        else{
            ans="Error Try again";
        }
        con.close();
        return ans;
	}catch(Exception e){
	System.out.println(e);
	}
        return "";
     }
     public static String dbmatchpass(String user,String pass){
     	Connection con;
        String ans;
        try{
	Class.forName("com.mysql.jdbc.Driver");
        System.out.println(user);
	con=DriverManager.getConnection("jdbc:mysql://localhost:3306/java_proj","root","try");
	Statement stmt=con.createStatement();
        String cmd="Select * from users where first_name= '"+user+"'and password ='"+pass+"';";
        ResultSet rs=stmt.executeQuery(cmd);
        
        if(rs.isBeforeFirst()){
            ans="Welcome to Login";
        }
        else{
            ans="No user of such passwprd and combination exists";
        }
        con.close();
        return ans;
	}catch(Exception e){
	System.out.println(e);
	}
        return "";
     }
}
