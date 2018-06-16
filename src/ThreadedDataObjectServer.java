import java.io.*;
import java.net.*;

    public class ThreadedDataObjectServer {
        public static void main(String[] args ) {

        try {
            ServerSocket s = new ServerSocket(3000);//listening

            for (;;) {
                Socket incoming = s.accept( );//getting a connection
                new ThreadedDataObjectHandler(incoming).start();//starting a new thread
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

    class ThreadedDataObjectHandler extends Thread {
        private Socket incoming;
        private SocketUtil IO;

        public ThreadedDataObjectHandler(Socket i){
            incoming = i;
        }

        public void run() {
            IO = new SocketUtil(incoming);

            //read in header info form client
            System.out.println("New Connection from: " + IO.read());

            //asking the user to log in
            IO.write("USERNAME AND PASSWORD?");

            //reading the username and password
            String UP_INPUT = IO.read();
            String auth = DB_checkUser(UP_INPUT);
            IO.write(auth);
        }

        private String DB_checkUser(String input) {
            //check the DB to see if the user is allowed in and what there role is: -1: no logged in, 1 user, 2 admin
            return "2";
        }
    }
