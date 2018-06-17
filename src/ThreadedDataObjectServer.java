import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

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
        private Connection conn;
        private String USER="";
        private boolean IS_ADMIN=false;


        ThreadedDataObjectHandler(Socket i){
            incoming = i;
        }

        public void run() {
            IO = new SocketUtil(incoming);

            //connecting to the DB
            connectToDB();

            //read in header info form client
            System.out.println("New Connection from: " + IO.read());

            //asking the user to log in
            IO.write("USERNAME AND PASSWORD?");

            while(true) {
                String[] INPUT = IO.read().split("`");

                System.out.println("CMD input: "+INPUT[0]+" parms: "+INPUT[1]==null? "": INPUT[1]);

                switch (INPUT[0]){//CMD switch
                    case "LOGIN":
                        System.out.println("CMD: login");
                        CMD_login(INPUT[1]);
                        break;
                    case "EDIT_USER":
                        System.out.println("CMD: edit users");
                        CMD_EditUser(INPUT[1]);
                        break;
                    case "GET_USERS":
                        System.out.println("CMD: get users");
                        CMD_GetUsers();
                        break;
                    case "GET_USER":
                        System.out.println("CMD: get user");
                        CMD_GetUser(INPUT[1]);
                        break;
                    case "ADD_USERS":
                        System.out.println("CMD: add users");
                        CMD_AddUser(INPUT[1]);
                        break;
                    case "REMOVE_USERS":
                        System.out.println("CMD: delete users");
                        CMD_DeleteUser(INPUT[1]);
                        break;
                    default:
                        IO.write("UNKNOWN CMD");
                }
            }
        }

        private void CMD_GetUser(String input) {
            String users="";

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT * FROM NJIT_CS602.user WHERE userName='"+input+"';");

                rs.next();

                users+=(rs.getString("password").equals("null")? " ":rs.getString("password"))+",";
                users+=(rs.getString("firstName")==null? " ":rs.getString("firstName"))+",";
                users+=(rs.getString("lastName")==null? " ":rs.getString("lastName"))+",";
                users+=(rs.getString("DOB")==null? " ":rs.getString("DOB"))+",";
                users+=(rs.getString("home_phone")==null? " ":rs.getString("home_phone"))+",";
                users+=(rs.getString("cell_phone")==null? " ":rs.getString("cell_phone"))+",";
                users+=(rs.getString("email")==null? " ":rs.getString("email"))+",";
                users+=(rs.getString("isAdmin")==null? " ":rs.getString("isAdmin"))+",";


            } catch (SQLException e) {
                IO.write("NO USERS,");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            IO.write(users);
        }

        private void CMD_GetUsers() {
            String users=",";

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT userName FROM NJIT_CS602.user;");

                while(rs.next()){
                    users+=rs.getString("userName")+",";
                }
            } catch (SQLException e) {
                IO.write("NO USERS,");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            IO.write(users);
        }

        private void CMD_DeleteUser(String input) {
            Statement stmt = null;
            try {
                stmt=conn.createStatement();
                int answer=stmt.executeUpdate("DELETE from NJIT_CS602.user WHERE userName='"+input+"'");
                IO.write(answer+"");
            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void CMD_AddUser(String input) {
            Statement stmt = null;
            try {
                stmt=conn.createStatement();
                int answer=stmt.executeUpdate("INSERT INTO NJIT_CS602.user (userName, password, isAdmin) VALUES ('"+input+"', 'password123', 'no');");
                IO.write(answer+"");
            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void CMD_EditUser(String input) {
            Statement stmt = null;
            try {
                stmt=conn.createStatement();
                System.out.println(input);
                int answer = stmt.executeUpdate(input);
                IO.write(answer+"");

            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void connectToDB() {
            final String connectionUrl = "jdbc:mysql://games3dcreations.ddns.net:3306/NJIT_CS602?autoReconnect=true";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                conn = DriverManager.getConnection(connectionUrl,"NJIT_CS602", "NJIT_CS602");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void CMD_login(String input) {
            //check the DB to see if the user is allowed in and what there role is: -1: no logged in, 1 user, 2 admin

            String[] INPUT=input.split(",");

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT isAdmin FROM NJIT_CS602.user where " +
                        "userName='"+INPUT[0]+"' and " +
                        "password='"+INPUT[1]+"';");

                rs.next();
                String answer=rs.getString("isAdmin");
                System.out.println("answer:"+answer);
                if(!answer.equals(null)) {//sets the current user
                    USER = INPUT[0];

                    if(answer.equals("yes")) {
                        IS_ADMIN=true;
                        IO.write("2");
                    } else {
                        IS_ADMIN=false;
                        IO.write("1");
                    }
                } else
                    IO.write("-1");
            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
