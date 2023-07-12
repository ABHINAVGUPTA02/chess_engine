package com.chess;

import com.chess.engine.board.Board;
import com.chess.engine.player.WhitePlayer;
import com.chess.gui.Table;

public class Shatranj {
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
//        WhitePlayer whitePlayer = new WhitePlayer(board);
        System.out.println(board);

        Table table = new Table();
    }
}
