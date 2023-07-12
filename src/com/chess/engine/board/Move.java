package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new nullMove();

    private Move(final Board board,final Piece movedPiece,final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board,final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode(){
        final int prime = 31;

        int result = 1;

        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }

        if(!(other instanceof Move)) return false;
        final Move otherMove = (Move) other;
        return (getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece().equals(otherMove.getMovedPiece()));
    }

    public int getCurrentCoordinate(){
        return this.movedPiece.getPiecePosition();
    }

    public int getDestinationCoordinate(){
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute() {
        final Builder builder = new Builder();

        for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }

        for(final Piece piece: this.board.getCurrentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        //move the moved piece
        builder.setPiece(this.movedPiece.movedPiece(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static final class MajorMove extends Move{

        public MajorMove(final Board board,final Piece piece,final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return (this == other || other instanceof MajorMove && super.equals(other));
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionCoordinate(this.destinationCoordinate);
        }

    }

    public static class AttackMove extends Move{

        final Piece attackedPiece;

        public AttackMove(final Board board,final Piece piece,final int destinationCoordinate,final Piece attackedPiece) {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(Object other){
            if(this == other) return true;
            if(!(other instanceof AttackMove)) return false;

            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }
    }


    public static final class pawnMove extends Move{

        public pawnMove(final Board board,final Piece piece,final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

    }


    public static class pawnAttackMove extends AttackMove{

        public pawnAttackMove(final Board board,final Piece piece,final int destinationCoordinate,final Piece attackedPiece) {
            super(board, piece, destinationCoordinate,attackedPiece);
        }

    }

    public static final class pawnEnPassantMove extends pawnAttackMove{

        public pawnEnPassantMove(final Board board,final Piece piece,final int destinationCoordinate,final Piece attackedPiece) {
            super(board, piece, destinationCoordinate,attackedPiece);
        }

    }


    public static final class pawnJump extends Move{

        public pawnJump(final Board board,final Piece piece,final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movedPiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }


    public static class castleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public castleMove(final Board board,final Piece piece,final int destinationCoordinate,final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, piece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()){
                if(!(this.movedPiece.equals(piece)) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            //TODO look into the isFirstMove
            builder.setPiece(this.movedPiece.movedPiece(this));
            builder.setPiece(new Rook(this.castleRookDestination,this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.getCurrentPlayer().getAlliance());
            return builder.build();
        }

    }

    public static final class kingSideCastleMove extends castleMove{

        public kingSideCastleMove(final Board board,final Piece piece,final int destinationCoordinate,final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }
    }

    public static final class queenSideCastleMove extends castleMove{

        public queenSideCastleMove(final Board board,final Piece piece,final int destinationCoordinate,final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }
    }

    public static final class nullMove extends Move{

        public nullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute a null move1");
        }

    }

    public static class MoveFactory{
        private MoveFactory() {
            throw new RuntimeException("Not Instantiable!");
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate){
            for(final Move move :  board.getAllLegalMoves()){
                if(move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate){
                    return move;
                }
            }

            return null;
        }
    }

}
