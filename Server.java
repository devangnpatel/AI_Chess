package game.network;

import chess.moves.ChessMove;
import game.network.GameMessage.MessageType;
import static game.network.GameMessage.MessageType.CONNECTED;
import static game.network.GameMessage.MessageType.GAME_STARTED;
import static game.network.GameMessage.MessageType.MOVE;
import game.utility.Properties.PlayerColor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * game server for multi-player turn-based games
 * - two players: checkers, chess, othello
 * - multiple players: poker, chinese checkers, etc
 * - after a move is made, the client sends the move (serialized) to this server
 *   which then relays the move to the other player(s)
 * 
 * PlayerConnection thread: keeps track of a player, and the socket connected to that client
 *                          and listens for move messages from clients
 * ServerConnection thread: listens for client connections, and creates PlayerConnections
 *                          with the uniquely created server socket
 * ServerSender thread:     sends/relays move messages to clients
 * @author devang
 */
public class Server {
    
    public class PlayerConnection {
        private Socket playerSocket;
        private PlayerColor color;
        private ServerListenerThread playerListener;
        
        public PlayerConnection(Socket socket)
        {
            playerSocket = socket;
            playerListener = new ServerListenerThread(socket);
            
            sendServerConnectedMessage();
        }
        
        public final void sendServerConnectedMessage()
        {
            GameMessage msg = new GameMessage(CONNECTED,null,null);
            sendMessage(msg);
        }
        
        public final void sendGameStartedMessage()
        {
            GameMessage msg = new GameMessage(GAME_STARTED,color,null);
            sendMessage(msg);
        }
        
        public final void sendMoveMessage(ChessMove move)
        {
            GameMessage msg = new GameMessage(MOVE,color,move);
            sendMessage(msg);
        }
        
        public void sendMessage(Object serverMessage)
        {
            ServerSenderThread serverSender = new ServerSenderThread(playerSocket, serverMessage);
            serverSender.start();
        }
        
        public void setColor(PlayerColor color)
        {
            this.color = color;
        }
        
        public PlayerColor getColor()
        {
            return color;
        }
        
        public void startListenerThread()
        {
            Thread listenerThread = new Thread(playerListener);
            listenerThread.start();
        }
        
        public void endListenerThread()
        {
            try {
                if (playerListener != null)
                {
                    playerListener.end();
                    playerListener.join(500);
                    playerListener = null;
                }

                if (playerSocket != null)
                {
                    playerSocket.close();
                    playerSocket = null;
                }
            } catch (InterruptedException | IOException e) {
                String loggerMsg = "server socket failure: " + e.getMessage();
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE,loggerMsg, e);
            }
        }
    }
    
    public void handleClientMessage(GameMessage msg)
    {
        PlayerColor color = msg.getColor();
        MessageType type  = msg.getType();
        ChessMove   move  = msg.getMove();
        
        if (type == null)  return;
        if (move == null)  return;
        if (color == null) return;
        if (type != MOVE)  return;
        
        for (PlayerConnection player : players)
        {
            if (player.getColor() == color)
            {
                player.sendMoveMessage(move);
            }
        }
    }
    
    public void initGame()
    {
        initPlayerColors();
        
        for (PlayerConnection player : players)
        {
            player.sendGameStartedMessage();
            player.startListenerThread();
        }
        
        updateGameStatus("game started");
    }
    
    public void initPlayerColors()
    {
        if (Math.random() > 0.5)
        {
            players[0].setColor(PlayerColor.WHITE);
            players[1].setColor(PlayerColor.BLACK);
        }
        else
        {
            players[0].setColor(PlayerColor.BLACK);
            players[1].setColor(PlayerColor.WHITE);
        }
    }
    
    public void endGame()
    {
        updateGameStatus("");
        updateServerStatus("server terminated");
        
        terminate();
    }
    
    private PlayerConnection[] players;
    private ServerConnectionThread serverConnectionThread;
    
    public static final int DEFAULT_SERVER_PORT = 8080;
    private final int serverPort;
    private JFrame serverFrame;
    private JLabel serverStatus;
    private JLabel gameStatus;

    private String getServerAddress() {
        String address;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            address  = ip.getHostAddress(); // -or- ip.getHostname();
            address += ":";
            address += String.valueOf(serverPort);
        } catch (UnknownHostException e) {
            address = "";
        }
        return address;
    }

    private void terminate()
    {
        for (PlayerConnection player : players)
        {
            if (player != null)
                player.endListenerThread();
        }
        
        if (serverConnectionThread != null)
            serverConnectionThread.closeServerSocket();
        
        serverFrame.dispose();
    }
    
    private void updateServerStatus(String status)
    {
        serverStatus.setText(status);
        serverStatus.repaint();
    }
    
    private void updateGameStatus(String status)
    {
        gameStatus.setText(status);
        gameStatus.repaint();
    }
    
    public Server(String gameName)
    {        
        serverPort = DEFAULT_SERVER_PORT;
        
        players = new PlayerConnection[2];
        serverConnectionThread  = new ServerConnectionThread(serverPort);
        serverConnectionThread.start();
        
        startServerWindow();
    }

    public void startServerWindow()
    {
        JPanel serverPanel = new JPanel();
        GridLayout serverPanelLayout = new GridLayout(5,1);
        serverPanel.setLayout(serverPanelLayout);
        
        JLabel serverTitleLabel = new JLabel("                  server started                   ",SwingConstants.CENTER);
        JLabel serverAddressLabel = new JLabel(getServerAddress(),SwingConstants.CENTER);
        JButton serverDisconnectButton = new JButton(" disconnect ");
        serverStatus = new JLabel("connected players: 0",SwingConstants.CENTER);
        gameStatus = new JLabel("",SwingConstants.CENTER);
        serverTitleLabel.setVisible(true);
        serverAddressLabel.setVisible(true);
        serverDisconnectButton.setVisible(true);
        serverStatus.setVisible(true);
        gameStatus.setVisible(true);
        serverPanel.add(serverTitleLabel);
        serverPanel.add(serverAddressLabel);
        serverPanel.add(serverDisconnectButton);
        serverPanel.add(serverStatus);
        serverPanel.add(gameStatus);
        serverPanel.setVisible(true);
        
        serverDisconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminate();
            }
        });
        
        serverFrame = new JFrame("Games Server");
        serverFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                terminate();
            }
        });

        serverFrame.add(serverPanel);
        int xLocation = 250;
        int yLocation = 250;
        serverFrame.setLocation(xLocation,yLocation);
        serverFrame.setSize(100,100); 
        serverFrame.pack();
        serverFrame.setResizable(false);
        serverFrame.setVisible(true); 
    }
    
    public class ServerSenderThread extends Thread {

        private final Socket clientSocket;

        public ServerSenderThread(Socket socket, Object msg) {
            clientSocket = socket;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            ObjectOutputStream objectOutputStream;
            try {
                if ((clientSocket != null) && clientSocket.isConnected())
                {
                    outputStream = clientSocket.getOutputStream();
                    objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.flush();
                    //objectOutputStream.writeObject(msgObject);
                }
            } catch (IOException e) {
                String loggerMsg = "server sender thread: " + e.getMessage();
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE,loggerMsg, e);
            }
        }
    }

    public class ServerListenerThread extends Thread {

        private final Socket clientSocket;
        private boolean stopRequested = false;
        
        public ServerListenerThread(Socket socket)
        {
            clientSocket  = socket;
        }

        public void end()
        {
            stopRequested = true;
        }
        
        @Override
        public void run()
        {
            try {
                clientSocket.setSoTimeout(100);
                while (!stopRequested)
                {
                    ObjectInputStream objectInputStream;
                    try {
                        InputStream inputStream = null;
                        inputStream = clientSocket.getInputStream();
                        objectInputStream = new ObjectInputStream(inputStream);
                        Object msg;
                        msg = objectInputStream.readObject();
                        
                        // parse Message here, and respond accordingly
                        if ((msg != null) && (msg instanceof GameMessage))
                            handleClientMessage((GameMessage)msg);
                    } catch (SocketTimeoutException s) {
                        // do nothing, go through loop again
                    } catch (SocketException s) {
                        stopRequested = true;
                        terminate();
                        String loggerMsg = "server listener thread: server-socket failure (" + s.getMessage() + ")";
                        Logger.getLogger(Server.class.getName()).log(Level.FINE,loggerMsg, s);
                    } catch (EOFException | ClassNotFoundException e) {
                        stopRequested = true;
                        terminate();
                        String loggerMsg = "server listener thread: " + e.getMessage();
                        Logger.getLogger(Server.class.getName()).log(Level.FINE,loggerMsg, e);
                    }
                }
                
                if (clientSocket != null)
                    clientSocket.close();
                
            } catch (IOException e) {
                String loggerMsg = "server listener thread: " + e.getMessage();
                Logger.getLogger(Server.class.getName()).log(Level.FINE,loggerMsg, e);
            }
        }
    }
    
    public class ServerConnectionThread extends Thread
    {
        private ServerSocket serverSocket = null;
        private final int serverPort;
        private int numPlayers = 0;

        public ServerConnectionThread(int port)
        {
            serverPort = port;
        }

        public void closeServerSocket()
        {
            try {
                if (serverSocket != null)
                    serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                String loggerMsg = "server connection thread: server socket is trying to close an already closed socket";
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE,loggerMsg, e);
            }
        }

        @Override
        public void run() {
            if (serverSocket == null) {
                try {
                    serverSocket = new ServerSocket(serverPort);

                    while (numPlayers < 2)
                    {
                        Socket socket = serverSocket.accept();
                        players[numPlayers] = new PlayerConnection(socket);
                        numPlayers++;
                        updateServerStatus("connected players: " + Integer.toString(numPlayers));
                    }
                    
                    initGame();
                    closeServerSocket();
                    
                } catch (IOException e) {
                    String loggerMsg = "server connection thread: probably a socket bind failure";
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE,loggerMsg, e);
                }
            }
        }
    }
}
