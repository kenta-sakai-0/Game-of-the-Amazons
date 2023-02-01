package amazons;


import java.util.Iterator;
import static java.lang.Math.*;
import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Kenta Sakai
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || (board.winner() != null)) {
            return staticScore(board);
        }

        if (sense == 1) {
            Iterator<Move> potentialmove = board.legalMoves(Piece.WHITE);
            while (potentialmove.hasNext()) {
                Move whiteMove = potentialmove.next();
                board.makeMove(whiteMove);
                int compare = findMove(board, depth - 1, false, -1, alpha, beta);
                if (compare >= alpha) {
                    alpha = compare;
                    if (saveMove) {
                        _lastFoundMove = whiteMove;
                    }
                    if (alpha >= beta) {
                        board.undo();
                        break;
                    }
                }
                board.undo();
            }
            return alpha;

        } else {
            Iterator<Move> potentialmove = board.legalMoves(Piece.BLACK);
            while (potentialmove.hasNext()) {
                Move blackMove = potentialmove.next();
                board.makeMove(blackMove);
                int compare = findMove(board, depth - 1, false, 1, alpha, beta);
                if (compare <= beta) {
                    beta = compare;
                    if (saveMove) {
                        _lastFoundMove = blackMove;
                    }
                    if (alpha >= beta) {
                        board.undo();
                        break;
                    }
                }
                board.undo();
            }
            return beta;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N < range1) {
            return 1;
        } else if (N < range2) {
            return 2;
        } else if (N < range3) {
            return 3;
        } else if (N < range4) {
            return 4;
        } else {
            return 5;
        }
    }

    /** Work. */
    private final int range1 = 45;
    /** Work. */
    private final int range2 = 50;
    /** Work. */
    private final int range3 = 60;
    /** Work. */
    private final int range4 = 65;

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        int blacklength = 0, whitelength = 0;


        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Piece item = board.get(col, row); Square square = Square.sq(col, row);
                if (item == WHITE) {
                    Iterator<Square> itera = board.reachableFrom(square, null);
                    while (itera.hasNext()) {
                        itera.next();
                        whitelength++;
                    }
                }
                if (item == BLACK) {
                    Iterator<Square> itera = board.reachableFrom(square, null);
                    while (itera.hasNext()) {
                        itera.next();
                        blacklength++;
                    }
                }
            }
        }
        return whitelength - blacklength;
    }
}
