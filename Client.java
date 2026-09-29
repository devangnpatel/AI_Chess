package game.network;

import chess.ChessNetworkWindow;
import chess.moves.ChessMove;
import game.network.GameMessage.MessageType;
import static game.network.GameMessage.MessageType.CONNECTING;
import static game.network.GameMessage.MessageType.MOVE;
import chess.players.ChessPlayerNetwork;
import game.utility.Properties.PlayerColor;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * client for multi-player turn-based games
 * - two players: checkers, chess, Othello
 * - multiple players: poker, Chinese checkers, etc
 * - after a move is made, this client sends the move (serialized) to the server
 *   which then relays the move to the other player(s)
 * 
 * ClientConnection thread: initiates communication with the server
 * ClinetListener thread:   listens for move messages from the server
 *                          and applies that move to the local player's game state
 * ClientSender thread:     after local player commits a move, that move is
 *                          persisted to this client which relays that move to the server
 * @author devang
 */
public class Client {
    private ChessNetworkWindow   gamesWindow;
    private Socket               serverSocket;
    private ClientListenerThread clientListener;
    private ChessPlayerNetwork   player;
    
    public Client(ChessNetworkWindow gamesWindow)
    {
        this.gamesWindow = gamesWindow;
        serverSocket     = null;
        clientListener   = null;
    }
    
    public void setPlayer(ChessPlayerNetwork player)
    {
        this.player = player;
    }
    
    public void startClient(String server, int port)
    {
        ClientConnectionThread connectionThread = new ClientConnectionThread(server,port);
        connectionThread.start();
    }
    
    public void startClientListener()
    {
        clientListener = new ClientListenerThread(serverSocket);
        clientListener.start();             
    }
    
    public void endClientListener()
    {
        clientListener.end();
    }

    public void handleServerMessage(GameMessage msg)
    {
        MessageType msgType  = msg.getType();
        PlayerColor msgColor = msg.getColor();
        ChessMove   msgMove  = msg.getMove();

        switch (msgType)
        {
	        case CONNECTING:
	        	// do nothing
	        	break;
            case CONNECTED:
                // update status panel to display server status
                gamesWindow.clientConnectedMessage();
                break;
            case GAME_STARTED:
                // start PlayerNetwork, initialize color, start local-side game
                gamesWindow.gameStartedMessage(msgColor);
                break;
            case MOVE:
                // persist move: local -> network-client -> server -> remote-client -> remote-local
                player.commitMove(msgMove);
                break;
        }
        
    }
    
    public final void sendConnectingMessage()
    {
        GameMessage msg = new GameMessage(CONNECTING,null,null);
        sendMessage(msg);
    }
    
    public final void sendMoveMessage(ChessMove move)
    {
        PlayerColor color = player.getColor();
        GameMessage msg = new GameMessage(MOVE,color,move);
        sendMessage(msg);
    }

    public void sendMessage(Object serverMessage)
    {
        ClientSenderThread clientSender = new ClientSenderThread(serverSocket, serverMessage);
        clientSender.start();
    }
    
    public class ClientListenerThread extends Thread
    {
        private final Socket clientSocket;
        private boolean stopRequested;
        
        public ClientListenerThread(Socket socket)
        {
            clientSocket  = socket;
            stopRequested = false;
        }
        
        public void end()
        {
            stopRequested = true;
        }
        
        @Override
        public void run()
        {
            try
            {
                clientSocket.setSoTimeout(100);
                while (!stopRequested)
                {
                    try {
                        ObjectInputStream objectInputStream;
                        InputStream inputStream = null;
                        inputStream = clientSocket.getInputStream();
                        objectInputStream = new ObjectInputStream(inputStream);
                        Object msg;
                        msg = objectInputStream.readObject();
                        
                        // parse message here, and respond accordingly
                        if (msg instanceof GameMessage)
                            handleServerMessage((GameMessage)msg);
                        
                    } catch (SocketTimeoutException s) {
                        // do nothing, go through loop again
                    } catch (EOFException | ClassNotFoundException e) {
                        stopRequested = true;
                    }
                }
                if (clientSocket != null)
                {
                    clientSocket.close();
                }
                
            } catch (IOException e) {
                    
            }
        }
    }
    
    public class ClientSenderThread extends Thread
    {
        private Socket clientSocket;
        
        public ClientSenderThread(Socket socket, Object msg)
        {
            clientSocket = socket;
        }
        
        @Override
        public void run()
        {
            OutputStream outputStream;
            ObjectOutputStream objectOutputStream;
            try {
                if ((clientSocket != null) && clientSocket.isConnected() && !clientSocket.isClosed())
                {
                    outputStream = clientSocket.getOutputStream();
                    objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.flush();
                    //objectOutputStream.writeObject(msgObject);
                }
            } catch (IOException e) {
                
            }
        }
    }
    
    public class ClientConnectionThread extends Thread
    {
        private String serverAddress;
        private int    serverPort;
        
        public ClientConnectionThread(String server,int port)
        {
            serverAddress = server;
            serverPort    = port;
        }
        
        @Override
        public void run()
        {
            if (serverSocket == null)
            {
                try {
                    serverSocket = new Socket(serverAddress,serverPort);
                    if (serverSocket.isConnected())
                    {
                        startClientListener();
                        sendConnectingMessage();
                    }
                } catch (IOException e) {
                    
                }
            }
        }
    }
}
