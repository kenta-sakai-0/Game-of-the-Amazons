package amazons;
import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

/** Junit tests for our Board iterators.
 *  @author Kenta Sakai
 */

public class IteratorTests {

    /**
     * Run the JUnit tests in this package.
     */
    public static void main(String[] ignored) {
        textui.runClasses(IteratorTests.class);
    }

    /**
     * Tests reachableFromIterator to make sure it returns all reachable
     * Squares. This method may need to be changed based on
     * your implementation.
     */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        assertFalse(b.isUnblockedMove(Square.sq(5, 4), Square.sq(1, 0), null));
        assertTrue(b.isUnblockedMove(Square.sq(5, 4), Square.sq(6, 4), null));
        assertFalse(b.isUnblockedMove(Square.sq(5, 4), Square.sq(9, 7), null));
        assertFalse(b.isUnblockedMove(Square.sq(5, 4), Square.sq(5, 6), null));
        assertTrue(b.isUnblockedMove(Square.sq(5, 4), Square.sq(8, 7), null));
        assertFalse(b.isUnblockedMove(Square.sq(5, 4), Square.sq(5, 7), null));
        assertTrue(b.isUnblockedMove(Square.sq(5, 4), Square.sq(4, 4), null));
        assertFalse(b.isUnblockedMove(Square.sq(5, 4), Square.sq(0, 9), null));

        System.out.println(b.toString());
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTESTSQUARES.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTESTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMTESTSQUARES.size(), squares.size());

    }

    /**
     * Tests legalMovesIterator to make sure it returns all legal Moves.
     * This method needs to be finished and may need to be changed
     * based on your implementation.
     */

    @Test
    public void testLegalMoves() {
        final ArrayList<Move> checkLegal =
                new ArrayList<>(Arrays.asList(
                        Move.mv("f5-e5(e6)"), Move.mv("f5-e5(f6)"),
                        Move.mv("f5-e5(f5)"), Move.mv("f5-e6(e5)"),
                        Move.mv("f5-e6(f6)"), Move.mv("f5-e6(f5)"),
                        Move.mv("f5-f6(e6)"), Move.mv("f5-f6(e5)"),
                        Move.mv("f5-e6(g6)"), Move.mv("f5-g6(e6)"),
                        Move.mv("f5-g6(f6)"), Move.mv("f5-g6(f5)"),
                        Move.mv("f5-f6(f5)"), Move.mv("f5-f6(g6)"),
                        Move.mv("h4-i3(j4)"), Move.mv("h4-i3(j3)"),
                        Move.mv("h4-i3(i1)"), Move.mv("h4-i3(i2)"),
                        Move.mv("h4-i3(h4)"), Move.mv("i9-i10(i9)"),
                        Move.mv("i9-i10(h9)"), Move.mv("i9-i10(j10)"),
                        Move.mv("i9-h9(i9)"), Move.mv("i9-h9(i10)"),
                        Move.mv("i9-h9(g10)"), Move.mv("i9-j10(i9)"),
                        Move.mv("i9-j10(i10)"), Move.mv("j9-j10(j9)"),
                        Move.mv("j9-j10(i10)"), Move.mv("j9-i10(j10)"),
                        Move.mv("j9-i10(j9)"), Move.mv("j9-i10(h9)")
                ));

        Board b = new Board();
        buildBoard(b, REACHABLEFROMLEGALBOARD);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(checkLegal.contains(Move.mv(m.toString())));
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(checkLegal.size(), numMoves);
        assertEquals(checkLegal.size(), moves.size());

    }

    @Test
    public void testLegalMoves2() {

        Board b = new Board();
        buildBoard(b, REACHABLEFROM);
        System.out.println(b.toString());
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        assertFalse(legalMoves.hasNext());
    }

    @Test
    public void testLegalMoves3() {

        Board b = new Board();
        buildBoard(b, ERRORSHIT);
        System.out.println(b.toString());
        Iterator<Move> legalMoves = b.legalMoves(Piece.BLACK);

        while (legalMoves.hasNext()) {
            System.out.println(legalMoves.next());
        }
    }

    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;
    static final Piece[][] ERRORSHIT = {
            {E, E, E, E, S, E, E, E, S, E},
            {E, E, B, E, E, S, S, W, B, E},
            {E, E, E, S, E, S, S, S, S, E},
            {E, S, S, S, S, S, E, S, S, E},
            {S, E, E, E, E, S, S, E, E, S},
            {E, S, S, B, E, S, S, E, S, E,},
            {E, W, S, E, S, S, W, E, S, E,},
            {S, E, S, E, S, E, S, S, E, E,},
            {E, S, E, S, S, E, B, E, S, W},
            {E, E, E, S, E, E, S, E, E, E,}
    };

    static final Piece[][] REACHABLEFROMTESTBOARD =
        {
            {E, E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, W, W},
            {E, E, E, E, E, E, E, S, E, S},
            {E, E, E, S, S, S, S, E, E, S},
            {E, E, E, S, E, E, E, E, B, E},
            {E, E, E, S, E, W, E, E, B, E},
            {E, E, E, S, S, S, B, W, B, E},
            {E, E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E, E},
        };


    static final Piece[][] REACHABLEFROMLEGALBOARD =
        {
            {E, E, E, E, E, E, E, S, E, E},
            {E, E, E, E, E, E, S, E, W, W},
            {E, E, E, E, E, E, S, S, S, S},
            {E, E, E, S, S, S, S, S, S, S},
            {E, E, E, S, E, E, E, S, B, E},
            {E, E, E, S, E, W, S, S, B, E},
            {E, E, E, S, S, S, B, W, B, E},
            {E, E, E, E, E, E, S, S, E, E},
            {E, E, E, E, E, E, S, S, E, S},
            {E, E, E, E, E, E, S, E, E, E},
        };

    static final Piece[][] REACHABLEFROM =
        {
            {E, E, E, E, E, E, S, S, S, W},
            {E, E, E, E, E, E, S, W, S, S},
            {E, E, E, E, E, E, S, S, S, S},
            {E, E, E, S, S, S, S, S, S, S},
            {E, E, E, S, S, S, B, S, B, E},
            {E, E, E, S, S, W, S, S, B, E},
            {E, E, E, S, S, S, B, S, B, E},
            {E, E, E, E, E, E, S, S, E, E},
            {E, E, E, E, E, E, S, S, E, S},
            {E, E, E, E, E, E, S, E, E, E},
        };


    static final Set<Square> REACHABLEFROMTESTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));

}
