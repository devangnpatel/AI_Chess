package chess.players;

import chess.ChessGame;
import chess.moves.ChessMove;
import game.utility.Properties.PlayerColor;

/**
 * Overarching superclass for all players: local, network, AI, board-game, card-game
 * @author devang
 */
public abstract class ChessPlayer {    
    protected final ChessGame   game;
    protected final PlayerColor color;
    public abstract void persistMove(ChessMove move);
    
    protected ChessPlayer(ChessGame game, PlayerColor color)
    {
        this.game  = game;
        this.color = color;
    }
    
    public PlayerColor getColor()
    {
        return color;
    }
    
    public void commitMove(ChessMove move)
    {
        game.commitMove(this,move);
    }
}
