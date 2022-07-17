package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Yunsu Ha
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        assert chars.length() != 0 : "chars empty";
        boolean duplicate = false;
        char[] charArr = chars.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            for (int j = 0; j < charArr.length; j++) {
                if (i != j && charArr[i] == charArr[j]) {
                    duplicate = true;
                }
            }
        }
        assert !duplicate : "There is a duplicate";
        _chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _chars.indexOf(ch) != -1;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _chars.indexOf(ch);
    }

    /** Returns _chars. */
    String returnChars() {
        return _chars;
    }

    /** String containing the characters of the Alphabet. */
    private String _chars;
}
