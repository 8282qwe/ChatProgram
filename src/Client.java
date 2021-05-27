import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client extends JFrame implements ActionListener,Runnable {

    //채팅창에 필요한 변수들
    private final JFrame MainFrame = new JFrame();
    JTextArea Chat = new JTextArea();
    private final JTextField Send_mg = new JTextField();
    JLabel Login_people = new JLabel("Online");
    JList<String> People_list = new JList<>();
    JButton Send_personal = new JButton("Send DM");
    JLabel Room = new JLabel("Chat Room List");
    JList<String> Room_list = new JList<>();
    JButton Join_room = new JButton("Join");
    JButton Make_room = new JButton("Make");
    JButton Send_text = new JButton("Send");

    //로그인시 필요한 변수들
    private final JFrame LoginFrame = new JFrame();
    private final JTextField Login_textField = new JTextField();
    private final JPasswordField Login_passwordField = new JPasswordField();

    JLabel ID_label = new JLabel("ID");
    JLabel PW_label = new JLabel("PW");

    JButton Join_button = new JButton("Join");
    JButton Search_button = new JButton("Search");
    JButton Login_button = new JButton("Login");


    //회원가입시 필요한 변수들
    private final JFrame JoinFrame = new JFrame();
    private final JTextField ID_textField = new JTextField();
    private final JPasswordField Join_passwordField = new JPasswordField();
    private final JTextField Email_textField = new JTextField();
    JButton ID_check_button = new JButton("Check");
    JButton Email_check_button = new JButton("Check");
    JButton Join_complete_button = new JButton("Join");
    JButton Join_canncel_button = new JButton("Canncel");
    int id_flag = 0;
    int email_flag = 0;

    //네트워크 통신을 위한 변수들
    private BufferedReader in;
    private PrintWriter out;
    private StringTokenizer st;
    Vector<String> user_list = new Vector<String>();
    Vector<String> room_list = new Vector<String>();

    public void Login_frame() {
        LoginFrame.setTitle("Login");
        LoginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LoginFrame.setBounds(100, 100, 377, 220);
        JPanel login_contentPane = new JPanel();
        login_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        LoginFrame.setContentPane(login_contentPane);
        login_contentPane.setLayout(null);

        Login_textField.setBounds(98, 28, 251, 33);
        login_contentPane.add(Login_textField);
        Login_textField.setColumns(10);

        ID_label.setBounds(26, 37, 36, 15);
        login_contentPane.add(ID_label);

        PW_label.setBounds(26, 95, 57, 15);
        login_contentPane.add(PW_label);

        Join_button.setBounds(26, 148, 97, 23);
        login_contentPane.add(Join_button);
        Join_button.addActionListener(this);

        Search_button.setBounds(135, 148, 97, 23);
        login_contentPane.add(Search_button);
        Search_button.addActionListener(this);

        Login_button.setBounds(244, 148, 97, 23);
        login_contentPane.add(Login_button);
        Login_button.addActionListener(this);

        Login_passwordField.setBounds(98, 86, 251, 33);
        login_contentPane.add(Login_passwordField);

        LoginFrame.setVisible(true);
        Thread t = new Thread(this);
        t.start();
    }

    public void Main_frame() {
        MainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                out.println("100/"+Login_textField.getText());
                MainFrame.setVisible(false);
                MainFrame.dispose();
            }
        });
        MainFrame.setBounds(100, 100, 721, 606);
        JPanel main_contentPane = new JPanel();
        main_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        MainFrame.setContentPane(main_contentPane);
        main_contentPane.setLayout(null);

        JScrollPane Chat_scrollPane = new JScrollPane();
        Chat_scrollPane.setBounds(167, 10, 480, 460);
        main_contentPane.add(Chat_scrollPane);

        Chat_scrollPane.setViewportView(Chat);
        Chat.setEditable(false);
        Send_mg.setBounds(167, 493, 390, 31);
        main_contentPane.add(Send_mg);
        Send_mg.setColumns(10);

        Login_people.setBounds(36, 15, 81, 15);
        main_contentPane.add(Login_people);

        JScrollPane People_scrollPane = new JScrollPane();
        People_scrollPane.setBounds(22, 40, 112, 157);
        main_contentPane.add(People_scrollPane);

        People_scrollPane.setViewportView(People_list);
        People_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Send_personal.setBounds(22, 207, 112, 23);
        main_contentPane.add(Send_personal);
        Send_personal.addActionListener(this);

        Room.setBounds(36, 240, 81, 15);
        main_contentPane.add(Room);

        JScrollPane Room_scrollPane = new JScrollPane();
        Room_scrollPane.setBounds(22, 270, 112, 184);
        main_contentPane.add(Room_scrollPane);

        Room_scrollPane.setViewportView(Room_list);

        Join_room.setBounds(20, 464, 114, 23);
        main_contentPane.add(Join_room);
        Join_room.addActionListener(this);

        Make_room.setBounds(22, 497, 112, 23);
        main_contentPane.add(Make_room);
        Make_room.addActionListener(this);

        Send_text.setBounds(563, 493, 81, 31);
        main_contentPane.add(Send_text);
        Send_text.addActionListener(this);

        MainFrame.setVisible(true);
        Thread t = new Thread(this);
        t.start();
    }

    public void Join_frame(){
        JoinFrame.setTitle("Join!");
        JoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JoinFrame.setBounds(100, 100, 439, 271);
        JPanel join_contentPane = new JPanel();
        join_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        JoinFrame.setContentPane(join_contentPane);
        join_contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Username");
        lblNewLabel.setBounds(12, 40, 58, 15);
        join_contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Password");
        lblNewLabel_1.setBounds(12, 88, 57, 15);
        join_contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("e-mail");
        lblNewLabel_2.setBounds(12, 142, 44, 15);
        join_contentPane.add(lblNewLabel_2);

        ID_textField.setBounds(86, 37, 220, 21);
        join_contentPane.add(ID_textField);
        ID_textField.setColumns(10);

        Join_passwordField.setBounds(86, 85, 220, 21);
        join_contentPane.add(Join_passwordField);

        Email_textField.setBounds(86, 139, 220, 21);
        join_contentPane.add(Email_textField);
        Email_textField.setColumns(10);

        ID_check_button.setBounds(318, 36, 97, 23);
        join_contentPane.add(ID_check_button);
        ID_check_button.addActionListener(this);

        Email_check_button.setBounds(318, 138, 97, 23);
        join_contentPane.add(Email_check_button);
        Email_check_button.addActionListener(this);

        Join_complete_button.setBounds(86, 196, 97, 23);
        join_contentPane.add(Join_complete_button);
        Join_complete_button.addActionListener(this);

        Join_canncel_button.setBounds(209, 196, 97, 23);
        join_contentPane.add(Join_canncel_button);
        Join_canncel_button.addActionListener(this);

        JoinFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == Login_button){
            out.println("8/"+Login_textField.getText()+"/"+String.valueOf(Login_passwordField.getPassword()));
        }
        if(e.getSource() == Send_text){
            out.println("1/"+Login_textField.getText()+"/"+Send_mg.getText());
        }
        if(e.getSource() == Send_personal){
            Chat.append("Dm To "+ People_list.getSelectedValue() + ":" + Send_mg.getText().trim()+"\n");
            out.println("2/"+People_list.getSelectedValue()+"/"+Login_textField.getText()+"/"+
                    Send_mg.getText());
        }
        if(e.getSource() == Make_room){
            out.println("3/"+Login_textField.getText()+"'s Room");
        }
        if(e.getSource() == Join_room){
            out.println("4/"+Room_list.getSelectedValue()+"/"+Login_textField.getText());
        }
        if(e.getSource() == Join_button ){
            Join_frame();
        }
        if(e.getSource() == ID_check_button){
            out.println("5/"+ID_textField.getText());
        }
        if(e.getSource() == Email_check_button){
            out.println("6/"+Email_textField.getText());
        }
        if(e.getSource() == Join_complete_button){
            out.println("5/"+ID_textField.getText());
            out.println("6/"+Email_textField.getText());
            if(id_flag == 1 && email_flag == 1){
                out.println("7/"+ID_textField.getText()+"/"+String.valueOf(Join_passwordField.getPassword())+"/"+Email_textField.getText());
            }
            else{
                if(id_flag == 0){
                    JOptionPane.showMessageDialog(null,"Check the ID!","Can't Confirm!",
                            JOptionPane.ERROR_MESSAGE);
                }
                if(email_flag == 0){
                    JOptionPane.showMessageDialog(null,"Check the E-Mail!","Can't Confirm!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if(e.getSource() == Search_button){
            JOptionPane Search = new JOptionPane();
            String Email = JOptionPane.showInputDialog("Input your E-mail!");

            out.println("9/"+Email);
        }
        if(e.getSource() == Join_canncel_button){
            JoinFrame.dispose();
        }
    }


    public Client() {
        try {
            Socket s = new Socket("localhost", 5056);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.Login_frame();
    }

    @Override
    public void run() {
        while (true){
            try {
                String msg = in.readLine();

                System.out.println(msg);
                inmessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void inmessage(String msg){
        st = new StringTokenizer(msg,"/");
        String flag = st.nextToken();

        switch (flag) {
            case "0" -> {
                String stb_r = st.nextToken();
                String stb = st.nextToken();
                StringTokenizer sttb_r = new StringTokenizer(stb_r, ",");
                while (sttb_r.hasMoreTokens()) {
                    room_list.add(sttb_r.nextToken());
                    TreeSet<String> buf_list = new TreeSet<String>(room_list);
                    Vector<String> room_list = new Vector<String>(buf_list);
                    Room_list.setListData(room_list);
                }

                StringTokenizer sttb = new StringTokenizer(stb, ",");
                while (sttb.hasMoreTokens()) {
                    user_list.add(sttb.nextToken());
                    TreeSet<String> buf_list = new TreeSet<String>(user_list);
                    Vector<String> user_list = new Vector<String>(buf_list);
                    People_list.setListData(user_list);
                }

            }
            case "1" -> Chat.append(st.nextToken() + ":" + st.nextToken().trim() + "\n");
            case "100" -> {
                String buffer = st.nextToken();
                String buffer_r = buffer + "'s Room";
                for (int i = 0; i < room_list.size(); i++) {
                    if (room_list.get(i).equals(buffer_r)) {
                        room_list.remove(i);
                    }
                }
                for (int i = 0; i < user_list.size(); i++) {
                    if (user_list.get(i).equals(buffer)) {
                        user_list.remove(i);
                    }
                }
                TreeSet<String> buf_list = new TreeSet<String>(user_list);
                Vector<String> user_list = new Vector<String>(buf_list);
                TreeSet<String> buf_list_r = new TreeSet<String>(room_list);
                Vector<String> room_list = new Vector<String>(buf_list_r);
                People_list.setListData(user_list);
                Room_list.setListData(room_list);
            }
            case "3" -> {
                String stb = st.nextToken();
                room_list.add(stb);
                TreeSet<String> buf_list = new TreeSet<String>(room_list);
                Vector<String> room_list = new Vector<String>(buf_list);
                Room_list.setListData(room_list);
            }
            case "5" -> {
                String stb = st.nextToken();
                if (stb.equals("1")) {
                    JOptionPane.showMessageDialog(null, "You can't use this ID!", "Can't Use!",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    id_flag = 1;
                    JOptionPane.showMessageDialog(null, "You can use this ID!", "Can Use!",
                            JOptionPane.PLAIN_MESSAGE);
                }
            }
            case "6" -> {
                String stb = st.nextToken();
                if (stb.equals("1")) {
                    email_flag = 1;
                    JOptionPane.showMessageDialog(null, "You can use this E-Mail!", "Can Use!",
                            JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "You can't use this E-Mail!", "Can't Use!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case "7" -> {
                JOptionPane.showMessageDialog(null, "Welcome!", "Welcome!",
                        JOptionPane.PLAIN_MESSAGE);
                JoinFrame.dispose();
            }
            case "8" -> {
                String stb = st.nextToken();
                if (stb.equals("1")) {
                    stb = st.nextToken();
                    LoginFrame.dispose();
                    MainFrame.setTitle(stb + "'s Chat!");
                    out.println("0/" + stb);
                    Main_frame();
                } else {
                    JOptionPane.showMessageDialog(null, "You can't Login!!", "Can't Login!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case "9" -> {
                String stb = st.nextToken();
                if (stb.equals("1")) {
                    JOptionPane.showMessageDialog(null, "ID : " + st.nextToken() + "\nPW : " + st.nextToken(), "Result",
                            JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "We can't Find!!", "We can't find!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case "notice" -> Chat.append(st.nextToken() + "\n");
        }
    }
}
