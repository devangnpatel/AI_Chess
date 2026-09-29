package game.network;

import chess.moves.ChessMove;
import game.utility.Properties.PlayerColor;
import java.io.Serializable;

/**
 * contains a message to use when transferring new moves between remote players
 * over a client-server connection
 * - two remote clients connect to a server
 * - server relays move messages between the two clients
 * - when logging in to server, a client sends a default message
 * - when client successfully logs in to the server, server replies with a default message
 * - when two clients are logged in, the server sends a game-started message, along
 *   with randomly assigned player colors to both clients
 * 
 * A message has:
 * a type  (connecting or move)
 * a color (identifying the intended recipient)
 * a move  (if the message is a move) to relay between players
 * @author devang
 */
public class GameMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	public final PlayerColor color;
    public final ChessMove   move;
    public final MessageType type;
    
    public enum MessageType implements Serializable
    {
        CONNECTING,
        CONNECTED,
        GAME_STARTED,
        MOVE
    }
    
    public final PlayerColor getColor()
    {
        return color;
    }
    
    public final ChessMove getMove()
    {
        return move;
    }
    
    public final MessageType getType()
    {
        return type;
    }
    
    /**
     * Message to relay between clients and to relay between server and clients
     * @param type type of message (connection -or- move)
     * @param color intended player of recipient
     * @param move move to relay, or null if a connection message
     */
    public GameMessage(MessageType type, PlayerColor color, ChessMove move)
    {
        this.type  = type;
        this.color = color;
        this.move  = move;
    }
}
