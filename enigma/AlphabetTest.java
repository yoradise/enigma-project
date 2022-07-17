package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Alphabet class.
 *  @author Yunsu Ha
 */

public class AlphabetTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Alphabet Test utilities */
    private Alphabet testAlpha0 = new Alphabet();
    private Alphabet testAlpha1 = new Alphabet("ZYXWVUTSRQPONMLKJIHGFEDCBA");
    private Alphabet testAlpha2 = new Alphabet("BSC");

    @Test
    public void checkSize() {
        assertEquals(26, testAlpha0.size());
        assertEquals(26, testAlpha1.size());
        assertEquals(3, testAlpha2.size());
    }

    @Test
    public void checkContains() {
        assertEquals(true, testAlpha0.contains('A'));
        assertEquals(true, testAlpha0.contains('Z'));
        assertEquals(true, testAlpha1.contains('A'));
        assertEquals(false, testAlpha2.contains('A'));
        assertEquals(true, testAlpha2.contains('C'));
    }

    @Test
    public void checkToCharToInt() {
        assertEquals(0, testAlpha0.toInt('A'));
        assertEquals(25, testAlpha0.toInt('Z'));
        assertEquals(2, testAlpha2.toInt('C'));

        assertEquals('A', testAlpha0.toChar(0));
        assertEquals('V', testAlpha1.toChar(4));
    }
}
