package amazons;

import java.util.Stack;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static amazons.Piece.*;
import static amazons.Move.mv;

/** The state of an Amazons Game.
 *  @author Kenta Sakai
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;
    /** Stuff. */
    private int numMoves;
    /** Stuff. */
    private Piece[][] grid;
    /** Stuff. */
    private Stack<Move> lifostack = new Stack<Move>();

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        this.grid = model.grid;
        this._winner = model._winner;
        this._turn = model._turn;
        this.numMoves = model.numMoves;
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        grid = new Piece[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = EMPTY;
            }
        }
        grid[0][3] = WHITE; grid[0][6] = WHITE;
        grid[3][0] = WHITE; grid[3][9] = WHITE;
        grid[9][3] = BLACK; grid[9][6] = BLACK;
        grid[6][0] = BLACK; grid[6][9] = BLACK;

        _turn = WHITE;
        _winner = EMPTY;
        numMoves = 0;
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return numMoves;
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        if (_winner == EMPTY) {
            return null;
        }
        return _winner;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return grid[s.row()][s.col()];
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return grid[row][col];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        grid[row][col] = p;
        _winner = EMPTY;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (isLegal(from, to)) {
            int coldif = to.col() - from.col();
            int rowdif = to.row() - from.row();
            if (coldif != 0) {
                coldif = coldif / Math.abs(coldif);
            }
            if (rowdif != 0) {
                rowdif = rowdif / Math.abs(rowdif);
            }
            int col = from.col();
            int row = from.row();
            while (!(col == to.col() && row == to.row())) {
                col += coldif;
                row += rowdif;
                if (grid[row][col] != EMPTY && Square.sq(col, row) != asEmpty) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return grid[from.row()][from.col()].toString()
                == turn().toString();
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        if (from.row() == to.row()
                || from.col() == to.col()
                || Math.abs(from.col() - to.col())
                == Math.abs(from.row() - to.row())) {
            return isLegal(from) && grid[to.row()][to.col()] == EMPTY;
        }
        return false;
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        if (isLegal(from, to)
                || isLegal(from, to) && from == spear) {
            return true;
        }
        return false;
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        lifostack.push(Move.mv(from, to, spear));
        grid[to.row()][to.col()] = grid[from.row()][from.col()];
        grid[from.row()][from.col()] = EMPTY;
        grid[spear.row()][spear.col()] = SPEAR;
        turnSwitch();
        numMoves++;
        if (!legalMoves(WHITE).hasNext() || !legalMoves(BLACK).hasNext()) {
            if (legalMoves(WHITE).hasNext()) {
                _winner = WHITE;
            } else {
                _winner = BLACK;
            }
        }
    }

    /** Switch. */
    public void turnSwitch() {
        if (turn() == WHITE) {
            _turn = BLACK;
        } else {
            _turn = WHITE;
        }
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move. Has no effect on the initial board.
     */
    void undo() {
        if (!lifostack.empty()) {
            Move lastReference = lifostack.pop();
            grid[lastReference.spear().row()]
                    [lastReference.spear().col()] = EMPTY;

            grid[lastReference.from().row()][lastReference.from().col()] =
                    grid[lastReference.to().row()][lastReference.to().col()];
            grid[lastReference.to().row()][lastReference.to().col()] = EMPTY;
            numMoves--;
        }
    }

    /** Returns FROM TO ASEMPTY. */
    boolean isUnbiasedMove(Square from, Square to, Square asEmpty) {
        if (isUnbiasedLegal(from, to, asEmpty)) {
            int coldif = to.col() - from.col();
            int rowdif = to.row() - from.row();
            if (coldif != 0) {
                coldif = coldif / Math.abs(coldif);
            }
            if (rowdif != 0) {
                rowdif = rowdif / Math.abs(rowdif);
            }
            int col = from.col();
            int row = from.row();
            while (!(col == to.col() && row == to.row())) {
                col += coldif;
                row += rowdif;
                if (grid[row][col] != EMPTY && Square.sq(col, row) != asEmpty) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** Returns FROM TO. */
    boolean isUnbiasedLegal(Square from, Square to, Square asEmpty) {
        if (from.row() == to.row()
                || from.col() == to.col()
                || Math.abs(from.col() - to.col())
                == Math.abs(from.row() - to.row())) {
            return grid[to.row()][to.col()] == EMPTY
                    || to == asEmpty;
        }
        return false;
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {
        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
            _arraylist = new ArrayList<Square>(0);
            for (int i = 0; i < 100; i++) {
                if (isUnbiasedMove(from, Square.sq(i), asEmpty)) {
                    _arraylist.add(Square.sq(i));
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !_arraylist.isEmpty();
        }

        @Override
        public Square next() {
            Square k = _arraylist.get(_arraylist.size() - 1);
            _arraylist.remove(_arraylist.size() - 1);
            return k;
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
        }

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;

        /** Something. */
        private ArrayList<Square> _arraylist = new ArrayList<Square>(0);
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;

            potentialMoves = new ArrayList<Move>(0);

            for (int sidesearch = 0; sidesearch < 100; sidesearch++) {

                Square potentialFrom = Square.sq(sidesearch);
                Piece pieceatgrid = grid[potentialFrom.row()]
                        [potentialFrom.col()];

                if (pieceatgrid == _fromPiece) {

                    ReachableFromIterator potentialToIterator =
                            new ReachableFromIterator(
                                    potentialFrom, null);

                    while (potentialToIterator.hasNext()) {
                        Square potentialTo = potentialToIterator.next();
                        put(side, potentialTo);
                        put(EMPTY, potentialFrom);

                        Iterator<Square> potentialSpearIterator =
                                new ReachableFromIterator(
                                        potentialTo, potentialFrom);

                        while (potentialSpearIterator.hasNext()) {
                            Square potentialSpear =
                                    potentialSpearIterator.next();
                            Move newMove = mv(potentialFrom,
                                    potentialTo, potentialSpear);
                            potentialMoves.add(newMove);
                        }
                        put(EMPTY, potentialTo);
                        put(side, potentialFrom);
                    }
                }
            }
            _length = potentialMoves.size();
        }

        @Override
        public boolean hasNext() {
            return !potentialMoves.isEmpty();
        }

        @Override
        public Move next() {
            Move result = potentialMoves.get(potentialMoves.size() - 1);
            potentialMoves.remove(potentialMoves.size() - 1);
            return result;
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
        /** Stuff. */
        private List<Move> potentialMoves = new ArrayList<Move>(0);

        /** Number of moves for that color. **/
        private int _length;
    }

    @Override
    public String toString() {
        String result = "";

        for (int row = 0; row < SIZE; row++) {
            String tempstring = "  ";
            for (int col = 0; col < SIZE; col++) {
                tempstring += " " + grid[row][col].toString();
            }
            result = tempstring + "\n" + result;
        }
        return result;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
