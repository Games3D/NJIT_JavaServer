import java.net.ServerSocket;
import java.net.Socket;

    class ThreadedDataObjectServer {
        public static void main(String[] args ) {

        try {
            ServerSocket s = new ServerSocket(3000);//listening

            //noinspection InfiniteLoopStatement
            while (true) {
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
        @SuppressWarnings("CanBeFinal")
        private Socket incoming;
        @SuppressWarnings("FieldCanBeLocal")
        private SocketUtil IO;

        ThreadedDataObjectHandler(Socket i){
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

        @SuppressWarnings("SameReturnValue")
        private String DB_checkUser(@SuppressWarnings("unused") String input) {
            //check the DB to see if the user is allowed in and what there role is: -1: no logged in, 1 user, 2 admin
            return "2";
        }
    }
