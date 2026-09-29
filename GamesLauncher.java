package launcher;

import chess.ChessGame;



/**
 *
 * @author devang
 */
public class GamesLauncher{

    public static void main(String[] args)
    {
        int level = 0;
        for (String s : args) {
            if ((Integer.parseInt(s) > 0) && (Integer.parseInt(s) < 6))
                level = Integer.parseInt(s);
        }
        ChessGame.new2DChessAIGame(level);
        //new ChessNetworkWindow(level);
    }
}
