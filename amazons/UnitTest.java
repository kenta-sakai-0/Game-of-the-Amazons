package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;

/** The suite of all JUnit tests for the amazons package.
 *  @author Kenta Sakai
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {

        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    @Test
    public void testisLegal() {
        Board b = new Board();
        makeSmile(b);
        Square from = Square.sq(3, 2);
        assertTrue(b.isLegal(from, Square.sq(3, 0)));
        assertTrue(b.isLegal(from, Square.sq("d1")));
        assertTrue(b.isLegal(from, Square.sq(4, 3)));
        assertTrue(b.isLegal(from, Square.sq("e4")));
        assertFalse(b.isLegal(from, Square.sq(4, 2)));
        assertFalse(b.isLegal(from, Square.sq("e3")));
        assertFalse(b.isLegal(Square.sq(2, 1), Square.sq(3, 2)));
        assertFalse(b.isLegal(Square.sq("c2"), Square.sq("d3")));

        Board c = new Board();
        assertFalse(c.isLegal(Square.sq(0, 6), Square.sq(9, 6)));
        assertFalse(c.isLegal(Square.sq("a7"), Square.sq("j7")));
        assertFalse(c.isLegal(Square.sq(0, 6), Square.sq(8, 6)));
        assertFalse(c.isLegal(Square.sq("a7"), Square.sq("i7")));
        assertTrue(c.isLegal(Square.sq(0, 3), Square.sq(8, 3)));
        assertTrue(c.isLegal(Square.sq("a4"), Square.sq("i4")));
        assertFalse(c.isLegal(Square.sq(0, 3), Square.sq(9, 3)));
        assertFalse(c.isLegal(Square.sq("a4"), Square.sq("j4")));

        Move mv = Move.mv("d1-d2(d1)");
        assertTrue(c.isLegal(mv));
    }

    @Test
    public void testisUnblockedMove() {
        Board b = new Board();
        makeSmile(b);

        b.put(WHITE, Square.sq(2, 1));
        System.out.println(b.toString());

        assertFalse(b.isUnblockedMove(Square.sq(3, 2),
                Square.sq(1, 0), Square.sq(5, 4)));
        assertTrue(b.isUnblockedMove(Square.sq(3, 2),
                Square.sq(1, 0), Square.sq(2, 1)));
        assertFalse(b.isUnblockedMove(Square.sq(7, 3),
                Square.sq(7, 7), Square.sq(5, 4)));
        assertTrue(b.isUnblockedMove(Square.sq(7, 3),
                Square.sq(7, 7), Square.sq(7, 6)));
        assertTrue(b.isUnblockedMove(Square.sq(7, 3),
                Square.sq(9, 5), Square.sq(8, 4)));
        assertFalse(b.isUnblockedMove(Square.sq(7, 3),
                Square.sq(7, 6), null));
    }

    @Test
    public void testReachableFromIterator() {
        Board b = new Board();
        makeSmile(b);
        b.put(WHITE, 7, 7);
        System.out.println(b.toString());
    }

    @Test
    public void testMakeMove() {
        Board b = new Board();
        b.makeMove(Square.sq(3, 0), Square.sq(5, 0),
                Square.sq(5, 9));
        assertEquals(1, b.numMoves());
        assertEquals(BLACK, b.turn());

        b.makeMove(Square.sq(6, 9), Square.sq(4, 7),
                Square.sq(4, 9));
        assertEquals(2, b.numMoves());
        assertEquals(WHITE, b.turn());
    }

    @Test
    public void testUndo() {
        Board b = new Board();
        System.out.println(b);

        b.makeMove(Square.sq(3, 0), Square.sq(5, 0),
                Square.sq(5, 9));
        System.out.println(b.toString());

        b.makeMove(Square.sq(0, 3), Square.sq(8, 4),
                Square.sq(4, 3));
        System.out.println(b.toString());

        b.makeMove(Square.sq(0, 6), Square.sq(6, 7),
                Square.sq(2, 1));

        b.undo();
        System.out.println(b.toString());

        b.undo();
        System.out.println(b.toString());

        b.undo();
        System.out.println(b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));

    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";


    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLEFROMTESTBOARD =
            {
                    { E, E, E, E, E, E, E, E, E, E },
                    { E, E, E, E, E, E, E, E, W, W },
                    { E, E, E, E, E, E, E, S, E, S },
                    { E, E, E, S, S, S, S, E, E, S },
                    { E, E, E, S, E, E, E, E, B, E },
                    { E, E, E, S, E, W, E, E, B, E },
                    { E, E, E, S, S, S, B, W, B, E },
                    { E, E, E, E, E, E, E, E, E, E },
                    { E, E, E, E, E, E, E, E, E, E },
                    { E, E, E, E, E, E, E, E, E, E },
            };
}
