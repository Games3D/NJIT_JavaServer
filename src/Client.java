import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    Socket socketToServer;
    SocketUtil IO;
    JFrame frame;
    String CURRENT_USER="";

    public static void main(String[] arg) {
        new Client();
    }

    public Client() {
        buildGUI();//builds the frame

        makeConnection();//starts the connection
    }

    private void loadGUI_Password() {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel LoginPanel = new JPanel();
        frame.setContentPane(LoginPanel);
        BorderLayout borderLayout = new BorderLayout();
        LoginPanel.setLayout(borderLayout);

        //making input panel
        JPanel InputPanel = new JPanel();

        InputPanel.add(new JLabel("USERNAME:"));
        JTextField username = new JTextField(20);
        username.requestFocusInWindow();
        InputPanel.add(username);

        InputPanel.add(new JLabel("PASSWORD:"));
        JTextField password = new JTextField(20);
        InputPanel.add(password);

        LoginPanel.add(InputPanel, BorderLayout.NORTH);

        //making Picture
        System.out.println(this.getClass().getResource("JoshuaMain.jpg"));
        ImageIcon img = new ImageIcon(getClass().getClassLoader().getResource("JoshuaMain.jpg").getPath());
        LoginPanel.add(new JLabel(img));


        //making submit button
        JButton submit = new JButton("Login");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                    //sending username and password
                        System.out.println(username.getText() + "," + password.getText());
                        IO.write(username.getText() + "," + password.getText());

                        switch (IO.read()){
                            case "-1":
                                CURRENT_USER = "";
                                JOptionPane.showMessageDialog(null,
                                        "Not Logged In. Try aging.", "NOT Logged In", JOptionPane.WARNING_MESSAGE);
                                break;
                            case "1":
                                CURRENT_USER = username.getText();
                                JOptionPane.showMessageDialog(null,
                                        "Welcome User", "Logged In", JOptionPane.INFORMATION_MESSAGE);
                                loadGUI_DashBoard(false);
                                break;
                            case "2":
                                CURRENT_USER = username.getText();
                                JOptionPane.showMessageDialog(null,
                                        "Welcome Admin", "Logged In", JOptionPane.INFORMATION_MESSAGE);
                                loadGUI_SelectAccount();
                                break;
                            default:
                                JOptionPane.showMessageDialog(null,
                                        "Unknown reply from server", "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                }else {
                    JOptionPane.showMessageDialog(null,
                            "username or password is empty, try again", "WARNING", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        LoginPanel.add(submit, BorderLayout.SOUTH);

        frame.setContentPane(LoginPanel);
        frame.pack();
    }

    private void loadGUI_DashBoard(boolean EDIT) {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel Panel = new JPanel();
        frame.setContentPane(Panel);
        BorderLayout borderLayout = new BorderLayout();
        Panel.setLayout(borderLayout);

        //top search field
        JPanel InputPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(1,3);
        InputPanel.setLayout(gridLayout);
        InputPanel.setBorder(BorderFactory.createTitledBorder("Search for users to view"));

        InputPanel.add(new JLabel("Enter Username:"));
        JComboBox<String> username = new JComboBox<>();
        getUsers(username);
        InputPanel.add(username);

        Panel.add(InputPanel, BorderLayout.NORTH);


        //center account info
        JPanel info = new JPanel();
        GridLayout gridLayout1 = new GridLayout(7,1);
        info.setLayout(gridLayout1);
        info.setBorder(BorderFactory.createTitledBorder("Selected user info"));

        JPanel userFor = new JPanel();
        userFor.add(new JLabel("Showing info for \""+CURRENT_USER+"\""));
        userFor.setBorder(BorderFactory.createBevelBorder(4));
        info.add(userFor);

        JPanel infoin = new JPanel();
        infoin.add(new JLabel("First Name:"));
        JTextField FirstName = new JTextField(30);
        infoin.add(FirstName);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Last Name:"));
        JTextField LastName = new JTextField(30);
        infoin.add(LastName);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("DOB:"));
        JTextField DOB = new JTextField(10);//TODO make a jdatepicker
        infoin.add(DOB);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Home Phone:"));
        JTextField Home_Phone = new JTextField(12);
        Home_Phone.setToolTipText("xxx-xxx-xxxx");
        infoin.add(Home_Phone);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Cell Phone:"));
        JTextField Cell_Phone = new JTextField(12);
        Cell_Phone.setToolTipText("xxx-xxx-xxxx");
        infoin.add(Cell_Phone);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Email:"));
        JTextField Email = new JTextField(30);
        infoin.add(Email);
        info.add(infoin);

        if (!EDIT) {
            FirstName.setEditable(false);
            LastName.setEditable(false);
            DOB.setEditable(false);
            Home_Phone.setEditable(false);
            Cell_Phone.setEditable(false);
            Email.setEditable(false);
        }

        Panel.add(info);


        //Log out and back
        JPanel logoutP = new JPanel();
        if (EDIT){//back for admin
            JButton back = new JButton("Back");
            back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadGUI_SelectAccount();
                }
            });
            logoutP.add(back);
        }

        JButton logout = new JButton("Log out");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGUI_Password();
            }
        });
        logoutP.add(logout);

        Panel.add(logoutP, BorderLayout.SOUTH);

        frame.setContentPane(Panel);
        frame.pack();
    }

    private void getUsers(JComboBox<String> username) {
        //TODO
    }

    private void loadGUI_SelectAccount() {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel Panel = new JPanel();
        frame.setContentPane(Panel);
        BorderLayout borderLayout = new BorderLayout();
        Panel.setLayout(borderLayout);

        JPanel contents = new JPanel();
        BoxLayout boxLayout = new BoxLayout(contents, BoxLayout.Y_AXIS);
        contents.setLayout(boxLayout);

        //editing info for the select user button
        JButton edit = new JButton("Edit Users");
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGUI_DashBoard(true);
            }
        });
        contents.add(edit);


        //add user button
        JButton add = new JButton("Add User");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });
        contents.add(add);


        //delete user combo box
        JPanel inner = new JPanel();
        inner.setBorder(BorderFactory.createTitledBorder("Select user to edit"));
        inner.add(new JLabel("Select Username:"));
        JComboBox<String> username = new JComboBox<>();
        getUsers(username);
        inner.add(username);
        contents.add(inner);

        Panel.add(contents, BorderLayout.CENTER);

        //Log out
        JPanel logoutP = new JPanel();
        JButton logout = new JButton("Log out");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGUI_Password();
            }
        });
        logoutP.add(logout);

        Panel.add(logoutP, BorderLayout.SOUTH);

        frame.setContentPane(Panel);
        frame.pack();
    }

    private void buildGUI() {
        //making the frame
        frame = new JFrame("Accounts");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setResizable(false);

        //building and showing the frame
        frame.pack();
        frame.setVisible(true);
    }

    private void makeConnection(){
        try{
            DataObject myObject = new DataObject();//new sending object
            socketToServer = new Socket("127.0.0.1", 3000);//making the connection
            //afsaccess2.njit.edu

            IO = new SocketUtil(socketToServer);//making the input and output

            //sending the connection header to the server
            String host="UNKNOWN";
            try {
                host =InetAddress.getByName("www.example.com").getHostAddress();
            } catch (UnknownHostException e) {}
            IO.write("Connecting to Server from: "+host);

            //reading the password request
            String INPUT=IO.read();
                System.out.println(INPUT);
                if (INPUT.equals("USERNAME AND PASSWORD?"))
                    loadGUI_Password();//loads the password content
                else{
                    JOptionPane.showMessageDialog(null,
                            "Loging request expected but not recieved","ERROR",JOptionPane.ERROR_MESSAGE);
                }
            } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (ConnectException e){
            JOptionPane.showMessageDialog(null,
                    "Can't connect to the Server right now so im closing","ERROR",JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        catch (IOException e1) {
            JOptionPane.showMessageDialog(null,
                    e1.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        try {
            socketToServer.close();
            IO.close();
            frame.setVisible(false);
            frame.dispose();
        } finally {
            super.finalize();
        }
    }
}
