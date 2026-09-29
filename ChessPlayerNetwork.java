package chess.players;

import chess.ChessGame;
import chess.moves.ChessMove;
import game.network.Client;
import game.utility.Properties.PlayerColor;

/**
 * Inherits from a Player, a Board Game Player to provide same
 * interface for a local human player, network player, or phantom AI player
 * and same interface across any game: Checkers, Chess, Othello, Poker, Chinese-Checkers, etc.
 * @author devang
 */
public class ChessPlayerNetwork extends ChessPlayer {
    Client client;
    private ChessPlayerNetwork(ChessGame game, Client networkClient, PlayerColor color)
    {
        super(game,color);
        client = networkClient;
        client.setPlayer(this);
    }
    
    /**
     * Creates a client-server Network Player, in Object factory fashion
     * @param game the game state representation
     * @param networkClient network client, with server connection
     * @param color the color for this networked-player
     * @return a newly constructed Network Player
     */
    public static ChessPlayerNetwork create(ChessGame game, Client networkClient, PlayerColor color)
    {
        return new ChessPlayerNetwork(game,networkClient,color);
    }
    
    /**
     * Sends move to server via this network-client, which then sends the move
     * to the opponent's network-client, which then updates the remote Game state
     * 
     * @param move Move that is made on the current game-state
     */
    /*@Override
    public void persistMove(ChessMove move)
    {
        String loggerMsg = "network client persist move";
        Logger.getLogger(ChessPlayerNetwork.class.getName()).log(Level.FINE,loggerMsg);
        super.persistMove(move);
    }*/
    
    /**
     * Called after this network-client receives a move from the Server, 
     * which received the move from the remote player's network client, after
     * the remote networked player has made it's move
     * @param move Move that the remote opponent made
     */
    /*@Override
    public void commitMove(ChessMove move)
    {
        String loggerMsg = "network client commit move";
        Logger.getLogger(ChessPlayerNetwork.class.getName()).log(Level.FINE,loggerMsg);
        
        // assumes local player and remote player have rotated/inverted boards
        move = rotateMove(move);
        
        super.commitMove(move);
    }*/
    
    private ChessMove rotateMove(ChessMove move)
    {
        return move.rotateMove();
    }
    
/**
     * terminates the client, gui and game
     */
    public void terminate()
    {
        if (client != null) client.endClientListener();
    }

    /**
     * when this player makes a move on the local game-state, this method transfers the Move
     * to the server, which then persists the move to the remote client, and then
     * to the remote player's game state
     * @param move Move which to make
     */
    public void persistMove(ChessMove move)
    {
        if (client != null) client.sendMoveMessage(move);
    }

    /**
     * when the remote player persists a move to the server, and then to this player's 
     * client, this method applies that move to this local game player's game state
     * @param move
     */
    public void commitMove(ChessMove move)
    {
        move = rotateMove(move);
        game.commitMove(this,move);
    }
    
    
    
    
}
