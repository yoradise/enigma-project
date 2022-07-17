package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;


import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Yunsu Ha
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();

    private static final Alphabet ALPH = new Alphabet();
    private static final Permutation PERM = new Permutation("(ABCDEFGH)", ALPH);
    private static final ArrayList<Rotor> ROTORLIST = new ArrayList<>();


    static {
        ROTORLIST.add(new Reflector("B", PERM));
        ROTORLIST.add(new FixedRotor("Beta", PERM));
        ROTORLIST.add(new MovingRotor("III", PERM, "B"));
        ROTORLIST.add(new MovingRotor("IV", PERM, "B"));
        ROTORLIST.add(new MovingRotor("I", PERM, "B"));
    }

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
        ROTORS.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "J"));
        ROTORS.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
    }

    private static final String[] ROTORS1 = { "B", "Beta", "III", "IV", "I" };
    private static final String SETTING1 = "AXLE";

    private Machine mach1() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        return mach;
    }


    @Test
    public void testInsertRotors() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(5, mach.numRotors());
        assertEquals(3, mach.numPawls());
        assertEquals(AZ, mach.alphabet());
        assertEquals(ROTORS.get("B"), mach.getRotor(0));
        assertEquals(ROTORS.get("Beta"), mach.getRotor(1));
        assertEquals(ROTORS.get("III"), mach.getRotor(2));
        assertEquals(ROTORS.get("IV"), mach.getRotor(3));
        assertEquals(ROTORS.get("I"), mach.getRotor(4));
    }

    @Test
    public void testConvertChar() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(YF) (HZ)", AZ));
        assertEquals(25,
                mach.convert(24));
    }

    @Test
    public void testConvertMsg() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMHISSHOULDERHIAWATHA"));
    }

    @Test
    public void testSetRotors() {
        Machine testMachine = new Machine(ALPH, 5, 3, ROTORS.values());
        testMachine.insertRotors(ROTORS1);
        String rotorName1 = testMachine.rotorSlots()[0].name();
        assertEquals(ROTORS.get("B").name(), rotorName1);
        assertEquals(ROTORS.get("Beta").name(),
                testMachine.rotorSlots()[1].name());

        testMachine.setRotors("AXLE");
        System.out.println(testMachine.rotorSlots()[0].name());
        System.out.println(testMachine.rotorSlots()[1].name());
        assertEquals(0, testMachine.rotorSlots()[1].setting());
        assertEquals(23, testMachine.rotorSlots()[2].setting());
        assertEquals(11, testMachine.rotorSlots()[3].setting());
        assertEquals(4, testMachine.rotorSlots()[4].setting());
    }
}
