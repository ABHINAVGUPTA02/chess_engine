package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import static com.chess.engine.board.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {7, 8, 9, 16};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN,piecePosition, pieceAlliance,true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance,final boolean isFirstMove) {
        super(PieceType.PAWN,piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Pawn movedPiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance());
    }


    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> LegalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset);

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                //TODO more work here (deal with promotions)!!!
                LegalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
            } else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                      ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                      (BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite()))) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.getPieceAlliance().getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                    !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    LegalMoves.add(new pawnJump(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.getPieceAlliance().isBlack()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        //TODO more work to do here
                        LegalMoves.add(new pawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
            } else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.getPieceAlliance().isWhite()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        //TODO more work to do here
                        LegalMoves.add(new pawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
            }
        }

        return LegalMoves;
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
}

