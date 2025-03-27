package chatapplication;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class ChatServer {
    private static final int PORT = 5000;
    private static final HashSet<PrintWriter> writers = new HashSet<>();
    private static DefaultListModel<String> userListModel = new DefaultListModel<>();
    private static JList<String> userList = new JList<>(userListModel);
    private static JFrame frame;

    public static void main(String[] args) throws Exception {
        System.out.println("Chat Server is running...");
        final ServerSocket listener = new ServerSocket(PORT);

        
        frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

       
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Users Online"));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userPanel.add(userScrollPane, BorderLayout.CENTER);
        
        frame.add(userPanel, BorderLayout.WEST);

    
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            while (true) {
                new ClientHandler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;

        public ClientHandler(final Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (writers) {
                        if (!name.isEmpty()) {
                            writers.add(out);
                            addUserToList(name);
                            break;
                        }
                    }
                }

                out.println("NAMEACCEPTED " + name);

                String message;
                while ((message = in.readLine()) != null) {
                    if (!message.isEmpty()) {
                    
                        saveMessage(name, message);
                    
                        synchronized (writers) {
                            for (PrintWriter writer : writers) {
                                writer.println("MESSAGE " + name + ": " + message);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    synchronized (writers) {
                        writers.remove(out);
                        removeUserFromList(name);  
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Socket close error: " + e.getMessage());
                }
            }
        }

        private void addUserToList(String name) {
            SwingUtilities.invokeLater(() -> {
                userListModel.addElement(name); 
            });
        }

        private void removeUserFromList(String name) {
            SwingUtilities.invokeLater(() -> {
                userListModel.removeElement(name);  
            });
        }

        private void saveMessage(final String sender, final String content) {
            try (Connection conn = SQLconnect.getConnection()) {
                String sql = "INSERT INTO Messages (sender, content, timestamp) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, sender);
                stmt.setString(2, content);
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error saving message: " + e.getMessage());
            }
        }
    }
}
