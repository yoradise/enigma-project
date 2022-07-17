package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Yunsu Ha
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine mach = readConfig();
        _temp = _input.nextLine();
        if (!_temp.contains("*")) {
            throw new EnigmaException("Setting format incorrect");
        }
        while (_input.hasNext()) {
            String outVal = "";
            setUp(mach, _temp);
            _temp = _input.nextLine();
            if (Pattern.matches("^$", _temp)) {
                _output.println();
            }
            while (!_temp.contains("*") && _input.hasNext()) {
                String tempString = _temp.replaceAll("\\s+", "");
                outVal = mach.convert(tempString);
                printMessageLine(outVal);
                _temp = _input.nextLine();
                if (Pattern.matches("^$", _temp)) {
                    _output.println();
                }
            }
            if (!_input.hasNext() && !_temp.contains("*")) {
                String tempString = _temp.replaceAll("\\s+", "");
                outVal = mach.convert(tempString);
                printMessageLine(outVal);
            }
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] rotorNames = new String[M.numRotors()];
        String[] splitSettings = settings.split(" ");

        Iterator<String> iterSettings = Arrays.stream(splitSettings).iterator();
        _temp = iterSettings.next();
        String plugboard = "";
        for (int i = 0; i < M.numRotors(); i++) {
            rotorNames[i] = iterSettings.next();
        }
        for (int i = 0; i < rotorNames.length; i++) {
            boolean same = false;
            for (int j = 0; j < M.allRotors().size(); j++) {
                if (M.allRotors().get(j).name().equals(rotorNames[i])) {
                    same = true;
                }
            }
            if (!same) {
                throw new EnigmaException("Invalid rotor name");
            }
        }
        M.insertRotors(rotorNames);
        if (M.rotorSlots()[0].type() != 3) {
            throw new EnigmaException("First rotor is not reflector");
        }
        int tempCounter = 0;
        for (int i = 0; i < M.numRotors(); i++) {
            if (M.rotorSlots()[i].type() == 1) {
                tempCounter += 1;
            }
        }
        if (tempCounter > M.numPawls()) {
            throw new EnigmaException("Too many moving rotors");
        }
        M.setRotors(iterSettings.next());
        if (iterSettings.hasNext()) {
            _temp = iterSettings.next();
            if (!_temp.contains("(") && !_temp.contains(")")) {
                char[] ringVals = _temp.toCharArray();
                for (int i = 1; i < M.numRotors(); i++) {
                    M.getRotor(i).setRings(ringVals[i - 1]);
                }
                if (iterSettings.hasNext()) {
                    _temp = iterSettings.next();
                }
            }
            if (_temp.contains("(")) {
                while (_temp.contains("(")) {
                    plugboard = plugboard + _temp + " ";
                    if (iterSettings.hasNext()) {
                        _temp = iterSettings.next();
                    } else {
                        break;
                    }
                }
            }
        }
        M.setPlugboard(new Permutation(plugboard, _alphabet));
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alph = _config.next();
            _alphabet = new Alphabet(alph);
            int numRotors;
            int numPawls;
            try {
                numRotors = Integer.valueOf(_config.next());
                numPawls = Integer.valueOf(_config.next());
            } catch (NumberFormatException excp) {
                throw error("Has to be an integer for numRotor / numPawls");
            }
            _name = _config.next();
            while (_config.hasNext()) {
                _rotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, _rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String typeAndNotch = _config.next();
            String perm = "";
            String subPerm = _config.next();
            String name = _name;
            while (subPerm.contains(")")
                    && subPerm.contains("(") && _config.hasNext()) {
                perm = perm + subPerm + " ";
                subPerm = _config.next();
            }
            if (!_config.hasNext()) {
                perm = perm + subPerm;
            } else {
                _name = subPerm;
                if (_name.contains("(") || _name.contains(")")) {
                    throw new EnigmaException("Name incorrect");
                }
            }
            Permutation returnPerm = new Permutation(perm, _alphabet);
            if (typeAndNotch.charAt(0) == 'M') {
                return new MovingRotor(name, returnPerm,
                        typeAndNotch.substring(1));
            } else if (typeAndNotch.charAt(0) == 'N') {
                return new FixedRotor(name, returnPerm);
            } else {
                return new Reflector(name, returnPerm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 5) {
            int maximum = msg.length() - i;
            if (maximum < 5) {
                _output.println(msg.substring(i));
            } else {
                _output.print(msg.substring(i, i + 5) + " ");
                if (i == msg.length() - 5) {
                    _output.println();
                }
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** stores the rotors from the conf file. */
    private ArrayList<Rotor> _rotors = new ArrayList<>();

    /** Temporarily stores the names of each rotor. */
    private String _name;

    /** Temporarily used to store nextLine(). */
    private String _temp;
}
