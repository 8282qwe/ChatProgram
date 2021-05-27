import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

public class Server {
    private static HashMap<String, ClientHandler> writers = new HashMap<String, ClientHandler>();
    private static HashMap<String, ArrayList<String>> rooms = new HashMap<String, ArrayList<String>>();
    private Statement stmt;

    public Server() {
        try {
            String url = "jdbc:mysql://localhost:3306/chatprogram_db?autoReconnect=true&useSSL=false";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,"root","root");
            this.stmt = con.createStatement();

            ServerSocket ss = new ServerSocket(5056);
            rooms.put("Main room", new ArrayList<String>());
            while (true) {
                Socket socket = ss.accept();
                System.out.println("connection!" + socket);
                ClientHandler t = new ClientHandler(socket, writers, rooms,stmt);
                t.start();
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    class ClientHandler extends Thread {
        private Socket s;
        private BufferedReader in;
        private PrintWriter out;
        private Statement stmt;
        final HashMap<String, ClientHandler> writers;
        final HashMap<String, ArrayList<String>> rooms;
        String T_str;
        private StringTokenizer st;

        public ClientHandler(Socket s, HashMap<String, ClientHandler> writers, HashMap<String, ArrayList<String>> rooms,Statement stmt) {
            this.s = s;
            this.writers = writers;
            this.rooms = rooms;
            this.stmt = stmt;
        }

        @Override
        public void run() {
            try {
                this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                this.out = new PrintWriter(s.getOutputStream(), true);

                while (true) {
                    T_str = in.readLine();

                    System.out.println(T_str);
                    st = new StringTokenizer(T_str, "/");
                    send_message(recieved_message(st));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void login_success(String value) {
            synchronized (writers) {
                writers.put(value, this);
            }
            synchronized (rooms) {
                rooms.get("Main room").add(value);
            }
        }

        public void quitUser(String value) {
            synchronized (writers) {
                writers.remove(value, this);
            }
        }

        public void room_clear(String value) {
            synchronized (rooms) {
                IntStream.range(0, rooms.get(value).size()).forEach(i -> rooms.get("Main room").add(rooms.get(value).get(i)));
                rooms.remove(value);
            }
        }

        private void send_message(int flag) {
            String str = "";
            String user = "";
            String userID = "";
            String sql = "";

            switch (flag) {
                case 0:
                    StringBuilder str1 = new StringBuilder();
                    StringBuilder roomID = new StringBuilder();
                    user = st.nextToken();
                    login_success(user);
                    System.out.println(rooms);
                    for (String i : rooms.keySet()) {
                        roomID.append(i).append(",");
                    }
                    for (String i : writers.keySet()) {
                        str1.append(i).append(",");
                    }
                    str = flag + "/" + roomID + "/" + str1;
                    broadcast(writers,str);
                    System.out.println(str);
                    str = "notice/" + user + "'s Online!";
                    broadcast(writers, str);
                    break;
                case 100:
                    str = T_str;
                    user = st.nextToken();
                    String outmsg = "";
                    quitUser(user);
                    broadcast(writers, str);
                    if(rooms.containsKey(user+"'s Room")){
                        room_clear(user + "'s Room");
                        System.out.println(rooms);
                        outmsg = "notice/" + user + "'s Room is gone!";
                        broadcast(writers, outmsg);
                    }
                    outmsg = "notice/" + user + "' Quit!";
                    broadcast(writers, outmsg);
                    break;
                case 1:
                    str = T_str;
                    userID = st.nextToken();
                    cast(writers,rooms,userID, str);
                    break;
                case 2:
                    String strbuf = st.nextToken();
                    str += "1/Dm From " + st.nextToken() + "/" + st.nextToken();
                    writers.get(strbuf).out.println(str);
                    break;
                case 3:
                    String room_name = st.nextToken();
                    rooms.put(room_name, new ArrayList<String>());
                    str = "3/" + room_name;
                    str1 = new StringBuilder("notice/" + room_name + "'s Open!");
                    broadcast(writers, str);
                    broadcast(writers, str1.toString());
                    break;
                case 4:
                    str1 = new StringBuilder(st.nextToken());
                    userID = st.nextToken();
                    for (String i : rooms.keySet()) {
                        for (int j = 0; j < rooms.get(i).size(); j++) {
                            if (rooms.get(i).get(j).equals(userID)) {
                                System.out.println("ok" + rooms);
                                rooms.get(i).remove(j);
                                break;
                            }
                            System.out.println(rooms);
                        }
                    }
                    rooms.get(str1.toString()).add(userID);
                    System.out.println(rooms);
                    break;
                case 5:
                    str1 = new StringBuilder(st.nextToken());
                    str = "select username from userinfo where username = \""+str1+"\";";
                    try {
                        ResultSet rs = stmt.executeQuery(str);
                        if(rs.next())
                            out.println("5/1");
                        else
                            out.println("5/0");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                case 6:
                    str1 = new StringBuilder(st.nextToken());
                    str = "select username from userinfo where useraddress = \""+str1+"\";";
                    try {
                        ResultSet rs = stmt.executeQuery(str);
                        if(rs.next())
                            out.println("6/0");
                        else
                            out.println("6/1");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                case 7:
                    str1 = new StringBuilder(st.nextToken());
                    String str2 = st.nextToken();
                    String str3 = st.nextToken();
                    sql = String.format("insert into userinfo(username,password,useraddress) values(\"%s\",\"%s\",\"%s\");",
                            str1.toString(),str2,str3);
                    try {
                        stmt.executeUpdate(sql);
                        out.println("7");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                case 8:
                    user = st.nextToken();
                    str1 = new StringBuilder(st.nextToken());
                    sql = String.format("select * from userinfo where username = \"%s\" and password = \"%s\";",
                            user, str1.toString());
                    try {
                        ResultSet rs = stmt.executeQuery(sql);
                        if(rs.next())
                            out.println("8/1/"+user);
                        else
                            out.println("8/2/"+user);

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                case 9:
                    str1 = new StringBuilder(st.nextToken());
                    sql = String.format("select username,password from userinfo where useraddress =\"%s\";", str1.toString());
                    try {
                        ResultSet rs = stmt.executeQuery(sql);
                        if(rs.next()){
                            str1 = new StringBuilder(String.format("9/1/%s/%s", rs.getString(1), rs.getString(2)));
                            out.println(str1);
                        }
                        else{
                            out.println("9/0");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
            }
        }
    }

    private int recieved_message(StringTokenizer st) {
        return Integer.parseInt(st.nextToken());
    }

    private void broadcast(HashMap<String, ClientHandler> writers, String str) {
        for (String i : writers.keySet()) {
            writers.get(i).out.println(str);
            try {
                stmt.executeUpdate(String.format("insert into chat_log(log) values(\"%s\");",str));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    private void cast(HashMap<String, ClientHandler> writers,HashMap<String, ArrayList<String>> rooms, String str,String str1) {
        Collection<ArrayList<String>> values = rooms.values();
        for(ArrayList<String> i : values){
            for(int j = 0; j< i.size(); j++){
                if(str.equals(i.get(j))){
                    for(j = 0; j < i.size();j++){
                        for (String k : writers.keySet()) {
                            if (i.get(j).equals(k)){
                                writers.get(k).out.println(str1);
                                try {
                                    stmt.executeUpdate(String.format("insert into chat_log(log) values(\"%s\");",str1));
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


