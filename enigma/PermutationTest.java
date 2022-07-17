package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Yunsu Ha
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;
    private Alphabet defaultAlpha = new Alphabet();
    private Alphabet alph = new Alphabet("ABCXYZ");

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testPermute() {
        perm = new Permutation("(BACD) (WXYZ)", defaultAlpha);
        assertEquals('E', perm.permute('E'));
        assertEquals(4, perm.permute(4));
        assertEquals('C', perm.permute('A'));
        assertEquals('B', perm.permute('D'));
        assertEquals('W', perm.permute('Z'));
        assertEquals(0, perm.permute(1));
        assertEquals(1, perm.permute(3));
        assertEquals(22, perm.permute(25));
    }

    @Test
    public void testInvert() {
        perm = new Permutation("(BACD) (WXYZ)", defaultAlpha);
        assertEquals('E', perm.invert('E'));
        assertEquals(4, perm.invert(4));
        assertEquals('B', perm.invert('A'));
        assertEquals('D', perm.invert('B'));
        assertEquals('Z', perm.invert('W'));
        assertEquals(0, perm.invert(2));
        assertEquals(25, perm.invert(22));
        assertEquals(3, perm.invert(1));
    }

    @Test
    public void testDerangement() {
        perm = new Permutation("(WXYZ)", defaultAlpha);
        Permutation perm2 = new Permutation("(ABCZYX)", alph);
        assertFalse(perm.derangement());
        assertTrue(perm2.derangement());
    }
}
