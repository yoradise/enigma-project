package enigma;

import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yunsu Ha
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.replace("(", " ");
        cycles = cycles.replace(")", " ");
        _tempPerms = cycles.split(" ");
        _cycles = new HashMap<Character, Character>();
        for (int i = 0; i < _tempPerms.length; i++) {
            addCycle(_tempPerms[i]);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (cycle.equals("")) {
            return;
        }
        char[] charCycle = cycle.toCharArray();
        for (int i = 0; i < charCycle.length - 1; i++) {
            _cycles.put(charCycle[i], charCycle[i + 1]);
        }
        _cycles.put(charCycle[charCycle.length - 1], charCycle[0]);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int modulo = wrap(p);
        char pChar = _alphabet.toChar(modulo);
        pChar = permute(pChar);
        return _alphabet.toInt(pChar);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int modulo = wrap(c);
        char cChar = _alphabet.toChar(modulo);
        cChar = invert(cChar);
        return _alphabet.toInt(cChar);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_cycles.containsKey(p)) {
            return _cycles.get(p);
        } else {
            return p;
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (Character key : _cycles.keySet()) {
            if (_cycles.get(key.charValue()) == c) {
                return key.charValue();
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (_cycles.get(_alphabet.toChar(i)) == null) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** stores the cycles of the permutation. */
    private HashMap<Character, Character> _cycles;

    /** Temporarily stores the sub permutations. */
    private String[] _tempPerms;

}
