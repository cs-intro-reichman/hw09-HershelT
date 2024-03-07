import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	private Random randomGenerator;

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
    
        // Reads just enough characters to form the first window
        for (int i = 0; i < windowLength; i++) {
            if (!in.isEmpty()) {
                window += in.readChar();
            }
        }
    
        // Processes the entire text, one character at a time
        while (!in.isEmpty()) {
            // Gets the next character
            c = in.readChar();
    
            // Checks if the window is already in the map
            List probs = CharDataMap.get(window);
    
            // If the window was not found in the map
            if (probs == null) {
                // Creates a new empty list, and adds (window,list) to the map
                probs = new List();
                CharDataMap.put(window, probs);
            }
    
            // Calculates the counts of the current character.
            probs.update(c);
    
            // Advances the window: adds c to the window’s end, and deletes the window's first character.
            window = window.substring(1) + c;
        }
    
        // The entire file has been processed, and all the characters have been counted.
        // Proceeds to compute and set the p and cp fields of all the CharData objects in each linked list in the map.
        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        int totalCharacters = probs.getSize();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < totalCharacters; i++) {
            CharData charData = probs.get(i);

            charData.p = (double) charData.count / totalCharacters;
            charData.p = Math.floor(charData.p * 10) / 10;

            // charData.p = 0.0 + charData.p * 100 / 100;
            // make a tester to Round up the 0.8999999 to 9 and 0.99999 to 1.0

            cumulativeProbability += charData.p;
            charData.cp = cumulativeProbability;
        }

	}

    // Returns a random character from the given probabilities list.
	public  char getRandomChar(List probs) {
		// Your code goes here
        if (probs.getSize() == 0) {
            return ' '; // Return space character if the list is empty
        }
        // randomGenerator = new Random();
        double random = randomGenerator.nextDouble();
        for (int i = 0; i < probs.getSize(); i++) {
            CharData charData = probs.get(i);
            if (random < charData.cp) {
                return charData.chr;
            }
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
        if (initialText.length() < windowLength) {
            return initialText; // Return initial text if it's shorter than window length
        }

        StringBuilder generatedText = new StringBuilder(initialText);
        String window = initialText.substring(initialText.length() - windowLength);

        while (generatedText.length() < textLength) {
            List probs = CharDataMap.get(window);

            if (probs == null) {
                break; // Stop if window is not found in the map
            }

            char nextChar = getRandomChar(probs);
            generatedText.append(nextChar);
            window = generatedText.substring(generatedText.length() - windowLength);
        }

        return generatedText.toString();
    }

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    // public static void main(String[] args) {
	// 	// Your code goes here
    //     //create a test of calculate probabilities function
    //     List list = new List();
    //     //add the word "committe_" to the list
    //     String word = "committee_";
    //     for (int i = word.length()-1; i >=0 ; i--) {
    //         list.addFirst(word.charAt(i));
    //     }
    //     System.out.println(list.toString());
    //     calculateProbabilities(list);
    //     System.out.println(list.toString());

    //     // Stress test the getRandomChar method
    //     System.out.println("Stress testing getRandomChar method...");

    //     int seed = 498353;
    //     // Call getRandomChar method multiple times
    //     for (int i = 0; i < 100; i++) {
    //         System.out.print(getRandomChar(list));
    //     }

    //     System.out.println("\nStress test complete.");

    //     //test the train method on the Galileo corpus
    //     LanguageModel model = new LanguageModel(4, seed);
    //     // model.train("originofspecies.txt");
    //     // System.out.println(model.toString());

    // }
    public static void main(String[] args) {
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        Boolean randomGeneration = args[3].equals("random");
        String fileName = args[4];
        // Create the LanguageModel object
        LanguageModel lm;
        if (randomGeneration)
        lm = new LanguageModel(windowLength);
        else
        lm = new LanguageModel(windowLength, 20);
        // Trains the model, creating the map.
        lm.train(fileName);
        // Generates text, and prints it.
        System.out.println(lm.generate(initialText, generatedTextLength));
        }
}
