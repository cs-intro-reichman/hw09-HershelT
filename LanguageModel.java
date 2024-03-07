// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.math.BigDecimal;
// import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    public int seed;
    
    // The random number generator used by this model. 
	private Random randomGenerator = new Random();

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        this.seed = seed;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        String window = "";
        char c;
        In in = new In(fileName);
        List probs;

        // Reads just enough characters to form the first window
        for (int i = 0; i < this.windowLength; i++) {
            // if (!in.isEmpty()) {
                c = in.readChar();
                window += c;
            // }
        }
    
        // Processes the entire text, one character at a time
        while (!in.isEmpty()) {
            // Gets the next character
            c = in.readChar();
    

            if (this.CharDataMap.containsKey(window)) {
                // If the window was found in the map, gets the list of character data objects
                probs = this.CharDataMap.get(window);
            } 
            else {
                // If the window was not found in the map, creates a new list of character data objects
                probs = new List();
                
            }
            // Checks if the window is already in the map
            probs.update(c);
            // Adds the character to the list of character data objects
            this.CharDataMap.put(window, probs);
    
            // Calculates the counts of the current character.
    
            // Advances the window: adds c to the window’s end, and deletes the window's first character.
            window += c;
            window = window.substring(1);
            
        }
    
        // The entire file has been processed, and all the characters have been counted.
        // Proceeds to compute and set the p and cp fields of all the CharData objects in each linked list in the map.
        for (List prob : CharDataMap.values()) {
            calculateProbabilities(prob);
        }
    }
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public static void calculateProbabilities(List probs) {				
		// Your code goes here
        double totalCharacters = 0.0;
        double p, cumulativeProbability = 0.0;
        Node current = probs.getFirstNode();
        while (current != null) {
            totalCharacters += current.cp.count;
            current = current.next;
        }

        current = probs.getFirstNode();
        while (current != null) {
            // CharData charData = current.cp;
            p = current.cp.count / totalCharacters;
            cumulativeProbability += p;
            current.cp.p = p;
            current.cp.cp = cumulativeProbability;
            current = current.next;
        }
        // double cumulativeProbability = 0.0;



        // for (int i = 0; i < totalCharacters; i++) {
        //     CharData charData = probs.get(i);

        //     // BigDecimal charDataCount = new BigDecimal(charData.count);
        //     // BigDecimal tC = new BigDecimal(totalCharacters);
        //     // charData.p = charDataCount.divide(tC, 1, RoundingMode.UP);
            
        //     int intCount = charData.count * 100; // Multiply by 100 to work with integers
        //     int intTotalChars = totalCharacters * 100;
        //     charData.p = (double) Math.ceil((double) intCount / intTotalChars) / 100; // Round and cast back to double

        //     // charData.p = Math.floor((double) charData.count / totalCharacters);
        //     // charData.p = (double) Math.round(charData.p * 10) / 10;
        //     //chops off the decimal places to 1


        //     // charData.p = 0.0 + charData.p * 100 / 100;
        //     // make a tester to Round up the 0.8999999 to 9 and 0.99999 to 1.0

        //     cumulativeProbability += charData.p;
        //     charData.cp = cumulativeProbability;
        // }

	}

    // Returns a random character from the given probabilities list.
	public  char getRandomChar(List probs) {
		// Your code goes here
        // randomGenerator = new Random();
        Node current = probs.getFirstNode();
        double r = this.randomGenerator.nextDouble();

        while (current != null) {
            if (r < current.cp.cp) {
                return current.cp.chr;
            }
            current = current.next;
        }
        return ' '; // Return space character if no character is found
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
    public String generate(String initialText, int textLength) {
        if ((initialText.length() < windowLength) || (!this.CharDataMap.containsKey(initialText.substring(initialText.length() - this.windowLength)))) {
            return initialText; // Return initial text if it's shorter than window length
        }

        String generatedText = initialText;
        String window = initialText;
        char context;
        for (int i = 0; i < textLength; i++) {
            context = this.getRandomChar(this.CharDataMap.get(window));
            generatedText += context;
            window += context;
            window = window.substring(1);
        }
        return generatedText;
    }

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : this.CharDataMap.keySet()) {
			List keyProbs = this.CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    
    public static void main(String[] args) {
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        Boolean randomGeneration = args[3].equals("random");
        String fileName = args[4];
        // Create the LanguageModel object
        LanguageModel lm;
        if (randomGeneration) {

            lm = new LanguageModel(windowLength);
        }
        else{
            lm = new LanguageModel(windowLength, 20);
        }
        // Trains the model, creating the map.
        lm.train(fileName);
        // Generates text, and prints it.
        System.out.println(lm.generate(initialText, generatedTextLength));
        }
}
