package chess;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.moves.MovePromotion;
import chess.moves.MovePromotion.PieceType;
import chess.pieces.ChessPiece;
import chess.pieces.PieceBishop;
import chess.pieces.PieceKing;
import chess.pieces.PieceKnight;
import chess.pieces.PiecePawn;
import chess.pieces.PieceQueen;
import chess.pieces.PieceRook;
import chess.players.ChessPlayerCPU;
import chess.players.ChessPlayerHuman;
import chess.players.ChessPlayer;
import chess.players.ChessPlayerNetwork;
import game.utility.Location;
import game.utility.Properties;
import game.utility.Properties.Direction;
import game.utility.Properties.PlayerColor;
import game.graphics.GraphicsBoard;
import game.network.Client;
import static game.utility.Properties.PlayerColor.WHITE;
import static game.utility.Properties.PlayerColor.BLACK;
import java.awt.Cursor;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Overarching Class to establish a Chess Game<br>
 * - establishes players, colors for players, board directions for a player<br>
 * - initializes GUI<br>
 * - initializes Network connection for client-server games<br>
 * - maintains the state of the game board and the current player's turn<br>
 * - handles the turn-based game loop
 * 
 * @author devang
 */
public class ChessGame {
    protected GraphicsBoard     gui;
    protected ChessBoardState   currentBoardState;
    protected PlayerColor       currentPlayerColor;
    protected Properties        properties;
    protected ChessPlayer       currentPlayer;
    protected ChessPlayer[]     players;

    /**
     * A Chess Game for 2 local human players on same screen
     */
    public ChessGame()
    {
        super();
    }

    /**
     * A Chess Game for 1 player against a Phantom CPU AI-controlled player<br>
     * - colors are indexed as red or black in the code<br>
     * - the properties class map colors to any java.awt.Color in java canvas graphics
     * @param level input Difficulty Level [1-5] of game against AI CPU
     */    
    public static void newChessAIGame(int level)
    {
        ChessGame game = new ChessGame();
        
        // order of initialization is important . . .
        game.initializeBoard();
        game.initializeGUI();
        game.initializePlayersAIGame(game.gui,level);
        game.initializeProperties();
        game.initializePieces();
        game.initializeGame();
    }    /**
     * A Chess Game for 1 player against a Phantom CPU AI-controlled player<br>
     * - colors are indexed as red or black in the code<br>
     * - the properties class map colors to any java.awt.Color in java canvas graphics
     * @param level input Difficulty Level [1-5] of game against AI CPU
     */    
    public static void newWarpedChessAIGame(int level)
    {
        ChessGame game = new ChessGame();
        
        // order of initialization is important . . .
        game.initializeBoard();
        game.initializeGUI();
        game.initializePlayersAIGame(game.gui,level);
        game.initializeProperties();
        game.initializePieces();
        game.initializeGame();
    }
    
    /**
     * A Chess Game for 2 players on the same local screen: initializes the players<br>
     * - colors are indexed as red or black in the code<br>
     * - the properties class map colors to any java.awt.Color in java canvas graphics
     */
    public static void newChessLocalGame()
    {
        ChessGame game = new ChessGame();
        
        // order of initialization is important . . .
        game.initializeBoard();
        game.initializeGUI();                      // needs board
        game.initializePlayersLocalGame(game.gui); // needs gui
        game.initializeProperties();               // needs players
        game.initializePieces();                   // needs players, properties
        game.initializeGame();                     // needs players, gui
    }
    
    /**
     * A Chess Game for 2 players over network: initializes the players<br>
     * - colors are indexed as red or black in the code<br>
     * - the properties class map colors to any java.awt.Color in java canvas graphics
     * @param color indexed color of the local player
     * @param client client object for socket-communication
     */
    public static void newChessNetworkGame(PlayerColor color, Client client)
    {
        ChessGame game = new ChessGame();
        
        // order of initialization is important . . .
        game.initializeBoard();
        game.initializeGUI();
        game.initializePlayersNetworkGame(game.gui,color,client);
        game.initializeProperties();
        game.initializePieces();
        game.initializeGame();
    }

    /**
     * initializes the GUI for a board game
     */
    public void initializeGUI()
    {
        gui = new GraphicsBoard("Student's CHESS");
    }

    /**
     * initializes the Properties(color,directions,dimensions) for a chess game
     */
    public void initializeProperties()
    {
        if (players[0].getColor().compareTo(WHITE) == 0)
            properties = Properties.init(Direction.UP,Direction.DOWN);
        else
            properties = Properties.init(Direction.DOWN,Direction.UP);
    }

    /**
     * initializes 2 players
     * - this method is a shell:
     *   the three methods, LocalGame, AIGame, NetworkGame need to be called instead
     */
    public void initializePlayers()
    {
        /*
        initializePlayersLocalGame(gui);
        initializePlayersAIGame(gui);
        initializePlayersNetworkGame(gui,color,client);
        */
    }
    
    protected void initializePlayersLocalGame(GraphicsBoard gui)
    {
        players            = new ChessPlayer[2];
        players[0]         = ChessPlayerHuman.create(this,gui,WHITE);
        players[1]         = ChessPlayerHuman.create(this,gui,BLACK);
    }
    
    protected void initializePlayersAIGame(GraphicsBoard gui,int level)
    {
        PlayerColor humanPlayerColor;
        PlayerColor CPUPlayerColor;
        if (Math.random() > 0.5)
            humanPlayerColor = PlayerColor.WHITE;
        else
            humanPlayerColor = PlayerColor.BLACK;
        
        CPUPlayerColor     = Properties.oppositeColor(humanPlayerColor);
        
        players            = new ChessPlayer[2];
        players[0]         = ChessPlayerHuman.create(this,gui,humanPlayerColor);
        players[1]         = ChessPlayerCPU.create(this,CPUPlayerColor,level);
    }
    
    protected void initializePlayersNetworkGame(GraphicsBoard gui, PlayerColor color, Client networkClient)
    {
        
        PlayerColor localPlayerColor  = color;
        PlayerColor remotePlayerColor = Properties.oppositeColor(color);
        
        players = new ChessPlayer[2];
        players[0] = ChessPlayerHuman.create(this,gui,localPlayerColor);
        players[1] = ChessPlayerNetwork.create(this,networkClient,remotePlayerColor);
    }
    
    /**
     * sets up the pieces on the game board
     */
    public void initializeBoard()
    {
        currentBoardState = new ChessBoardState();
    }
    
    public void initializeGame()
    {
        currentPlayerColor = Properties.INITIAL_PLAYER_COLOR;
        if (currentPlayerColor == players[0].getColor())
            currentPlayer = players[0];
        else
            currentPlayer = players[1];
        
        gui.init(currentBoardState,properties);
        
        if ((currentPlayer == players[1]) && (players[1] instanceof ChessPlayerCPU))
        {            
            gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ((ChessPlayerCPU)currentPlayer).determineMove();
        }
    }
    
    public void initializePieces()
    {        
        if (players[0].getColor() == WHITE)
            initializePiecesRedControl();
        else
            initializePiecesBlackControl();
        setPiecesProperties((Properties)properties);
    }
    
    private void setPiecesProperties(Properties properties)
    {
        for (ChessPiece piece : currentBoardState.getPieces())
        {
            ((ChessPiece)piece).setProperties(properties);
        }
    }
    private void initializePiecesRedControl()
    {
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(0,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(1,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(2,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(3,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(4,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(5,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(6,6));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(7,6));
        
        currentBoardState.setPiece(PieceRook.create(WHITE),Location.at(0,7));
        currentBoardState.setPiece(PieceKnight.create(WHITE),Location.at(1,7));
        currentBoardState.setPiece(PieceBishop.create(WHITE),Location.at(2,7));
        currentBoardState.setPiece(PieceQueen.create(WHITE),Location.at(3,7));
        currentBoardState.setPiece(PieceKing.create(WHITE),Location.at(4,7));
        currentBoardState.setPiece(PieceBishop.create(WHITE),Location.at(5,7));
        currentBoardState.setPiece(PieceKnight.create(WHITE),Location.at(6,7));
        currentBoardState.setPiece(PieceRook.create(WHITE),Location.at(7,7));
        
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(0,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(1,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(2,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(3,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(4,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(5,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(6,1));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(7,1));
        
        currentBoardState.setPiece(PieceRook.create(BLACK),Location.at(0,0));
        currentBoardState.setPiece(PieceKnight.create(BLACK),Location.at(1,0));
        currentBoardState.setPiece(PieceBishop.create(BLACK),Location.at(2,0));
        currentBoardState.setPiece(PieceQueen.create(BLACK),Location.at(3,0));
        currentBoardState.setPiece(PieceKing.create(BLACK),Location.at(4,0));
        currentBoardState.setPiece(PieceBishop.create(BLACK),Location.at(5,0));
        currentBoardState.setPiece(PieceKnight.create(BLACK),Location.at(6,0));
        currentBoardState.setPiece(PieceRook.create(BLACK),Location.at(7,0));
    }
    
    private void initializePiecesBlackControl()
    {   
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(0,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(1,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(2,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(3,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(4,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(5,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(6,6));
        currentBoardState.setPiece(PiecePawn.create(BLACK),Location.at(7,6));
        
        currentBoardState.setPiece(PieceRook.create(BLACK),Location.at(0,7));
        currentBoardState.setPiece(PieceKnight.create(BLACK),Location.at(1,7));
        currentBoardState.setPiece(PieceBishop.create(BLACK),Location.at(2,7));
        currentBoardState.setPiece(PieceKing.create(BLACK),Location.at(3,7));
        currentBoardState.setPiece(PieceQueen.create(BLACK),Location.at(4,7));
        currentBoardState.setPiece(PieceBishop.create(BLACK),Location.at(5,7));
        currentBoardState.setPiece(PieceKnight.create(BLACK),Location.at(6,7));
        currentBoardState.setPiece(PieceRook.create(BLACK),Location.at(7,7));
        
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(0,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(1,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(2,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(3,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(4,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(5,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(6,1));
        currentBoardState.setPiece(PiecePawn.create(WHITE),Location.at(7,1));
        
        currentBoardState.setPiece(PieceRook.create(WHITE),Location.at(0,0));
        currentBoardState.setPiece(PieceKnight.create(WHITE),Location.at(1,0));
        currentBoardState.setPiece(PieceBishop.create(WHITE),Location.at(2,0));
        currentBoardState.setPiece(PieceKing.create(WHITE),Location.at(3,0));
        currentBoardState.setPiece(PieceQueen.create(WHITE),Location.at(4,0));
        currentBoardState.setPiece(PieceBishop.create(WHITE),Location.at(5,0));
        currentBoardState.setPiece(PieceKnight.create(WHITE),Location.at(6,0));
        currentBoardState.setPiece(PieceRook.create(WHITE),Location.at(7,0));
    }
        
    /**
     * called after a move is made: changes the player turn to who has the current move
     */
    public void togglePlayer()
    {
        currentPlayerColor = Properties.oppositeColor(currentPlayerColor);
        if (currentPlayer == players[0])
            currentPlayer = players[1];
        else if (currentPlayer == players[1])
            currentPlayer = players[0];
                
        if (currentPlayer instanceof ChessPlayerCPU)
        {
            gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else
        {
            gui.setCursor(Cursor.getDefaultCursor());
        }
    }
    
    /**
     * sent from a Player (indicated in argument) to apply the move to the board state<br>
     * - performs the move<br>
     * - persists the move to the opponent's game state representation<br>
     * - updates the change to whoever has the current move in this board state<br>
     * - checks for end of game
     * 
     * @param player the Player making the move
     * @param move the Move to make
     */
    public void commitMove(ChessPlayer player, ChessMove move)
    {
        if (player != currentPlayer)      return;
        if (move == null)                 return;
        if (!(move instanceof ChessMove)) return;
        
        ChessMove chessMove = (ChessMove)move;
        if (player instanceof ChessPlayerCPU && move instanceof MovePromotion)
        {
            // ((MovePromotion)chessMove).setNewPieceType(PieceType.QUEEN);
        }
        else if (move instanceof MovePromotion)
        {
            PieceType pieceType = pawnPromotionMenu();
            ((MovePromotion)chessMove).setNewPieceType(pieceType);
        }
        
        Location moveLocation = chessMove.getFromLocation();
        ChessPiece movePiece = currentBoardState.getPiece(moveLocation);
        movePiece.setMostRecentMove(move);
        movePiece.setNumMovesMade(movePiece.getNumMovesMade()+1);
        currentBoardState.setMostRecentMove(move);

        ((ChessMove)move).commitMove(currentBoardState);
        
        togglePlayer();

        persistMove(player,move);
        
        if (checkGameOver())
        {
            if (gameOverWindow())
            {
                gui.dispose();
                System.exit(0);
            }
            System.exit(0);
        }
    }
    
    private boolean gameOverWindow()
    {
        PlayerColor winnerColor = Properties.oppositeColor(currentPlayerColor);
        String winnerColorText = "";
        String gameOverText;
        
        if (winnerColor == WHITE) winnerColorText = "ORANGE";
        if (winnerColor == BLACK) winnerColorText = "BLACK";
        
        gameOverText = "game over: ";
        gameOverText+= winnerColorText;
        gameOverText+=" wins!";
        
        boolean result = gameOverWindow(gameOverText);
        return result;
    }
    
    static private int levelMenu()
    {
        Object[] possibleValues = { "1", "2", "3", "4", "5" };
        Object selectedValue = JOptionPane.showInputDialog(null,"", "AI level",
            JOptionPane.PLAIN_MESSAGE,null,possibleValues, possibleValues[0]);
        
        if (selectedValue == null) System.exit(0);
        
        if (selectedValue.equals("1"))
            return 0;
        if (selectedValue.equals("2"))
            return 1;
        if (selectedValue.equals("3"))
            return 2;
        if (selectedValue.equals("4"))
            return 3;
        if (selectedValue.equals("5"))
            return 4;
        return 1;
    }
    private PieceType pawnPromotionMenu()
    {
        Object[] possibleValues = { "queen", "bishop", "knight", "rook" };
        Object selectedValue = JOptionPane.showInputDialog(null,"", "pawn promotion",
            JOptionPane.PLAIN_MESSAGE,null,possibleValues, possibleValues[0]);
        
        if (selectedValue.equals("queen"))
            return PieceType.QUEEN;
        if (selectedValue.equals("bishop"))
            return PieceType.BISHOP;
        if (selectedValue.equals("knight"))
            return PieceType.KNIGHT;
        if (selectedValue.equals("rook"))
            return PieceType.ROOK;
        return PieceType.QUEEN;
    }
    
    /**
     * After a move is made, this method sends that Move to the opponent's game state 
     * to apply the move and make the disconnected game state consistent
     * @param player player making the move
     * @param move the Move that has been made
     */
    public void persistMove(ChessPlayer player, ChessMove move)
    {
        for (ChessPlayer p : players)
        {
            if (p != player)
                p.persistMove(move);
            else if (p instanceof ChessPlayerHuman)
                ((ChessPlayerHuman) p).repaint();
        }
    }
    
    /**
     * checks if the game is over, an then displays a message if it is<br>
     * end of game established per rules of specific game and piece locations:<br>
     * - Chess ends when a King cannot legally move to another space<br>
     * @return True if the game is over, False otherwise
     */
    public boolean checkGameOver()
    {
        boolean validMovePossible = false;

        ChessBoardState boardState = ChessBoardState.copy(currentBoardState);
        
        for (Location location : Location.allLocations())
        {
            if (!boardState.isEmpty(location))
            {
                ChessPiece piece = boardState.getPiece(location);
                if (piece.getColor()==currentPlayerColor)
                {
                    List<ChessMove> moves = piece.getValidMoves(location, boardState);
                    if (!moves.isEmpty())
                    {
                        validMovePossible = true;
                        break;
                    }
                }
            }
        }

        return !validMovePossible;

    }
        
    /**
     * public access to the official state of the game<br>
     * - if a copy of the board state is made in any class, a Move can 
     *   be evaluated on that board state copy without modifying the official
     *   current board state, that is accessed here
     * 
     * @return the current formal state of the board for the current game
     */
    public ChessBoardState getBoardState()
    {
        return currentBoardState;
    }
    
    public final ChessPlayer getCurrentPlayer()
    {
        return currentPlayer;
    }
    
    protected boolean gameOverWindow(String gameOverText)
    {
        JOptionPane.showMessageDialog(null,
            gameOverText,
            "game over",
            JOptionPane.PLAIN_MESSAGE);
        return true;
    }
    /**
     * A Chess Game for 1 player against a Phantom CPU AI-controlled player<br>
     * - colors are indexed as red or black in the code<br>
     * - the properties class map colors to any java.awt.Color in java canvas graphics
     * @param level Chosen difficulty level for the AI CPU player
     */    
    public static void new2DChessAIGame(int level)
    {
        ChessGame game = new ChessGame();
        
        // order of initialization is important . . .
        game.initializeBoard();
        game.initializeGUI();
        game.initializePlayersAIGame(game.gui,levelMenu());
        game.initializeProperties();
        game.initializePieces();
        game.initializeGame();
    }
    
    public static void new2DChessLocalGame()
    {
        ChessGame game = new ChessGame();
        
        // order of initialization is important . . .
        game.initializeBoard();
        game.initializeGUI();                      // needs board, properties
        game.initializePlayersLocalGame(game.gui); // needs gui
        game.initializeProperties();               // needs players
        game.initializePieces();                   // needs properties,players
        game.initializeGame();                     // needs players
    }
}
