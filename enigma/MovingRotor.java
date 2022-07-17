package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Yunsu Ha
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    int type() {
        return 1;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (alphabet().toInt(_notches.charAt(i)) == setting()) {
                return true;
            }
        }
        return false;
    }

    /** the letters where notches are located. */
    private String _notches;
}
