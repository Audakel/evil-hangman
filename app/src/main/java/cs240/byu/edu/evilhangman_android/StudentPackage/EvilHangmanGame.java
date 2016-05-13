package cs240.byu.edu.evilhangman_android.StudentPackage;



import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by audakel on 5/4/16.
 */
public class EvilHangmanGame implements StudentEvilHangmanGameController {
    private static final String TAG = "debug";
    private Set<Character> previouslyChosenLetters;
    private HashSet<String> currentWordList;
    private Scanner scanner;
    private int numberOfGuessesLeft;
    private char[] currentWord;


    @Override
    public GAME_STATUS getGameStatus() {
        if (numberOfGuessesLeft > 0) return GAME_STATUS.NORMAL;

        if (numberOfGuessesLeft == 0) return GAME_STATUS.PLAYER_LOST;

        return null;
    }

    @Override
    public int getNumberOfGuessesLeft() {
        return numberOfGuessesLeft;
    }

    @Override
    public String getCurrentWord() {
        return String.valueOf(currentWord);
    }

    @Override
    public Set<Character> getUsedLetters() {
        return previouslyChosenLetters;
    }

    @Override
    public void setNumberOfGuesses(int numberOfGuessesToStart) {
        numberOfGuessesLeft = numberOfGuessesToStart;
    }

    @Override
    public void startGame(InputStreamReader dictionary, int wordLength) {
        

        previouslyChosenLetters = new HashSet<Character>();
        scanner = new Scanner(dictionary);
        currentWordList = new HashSet<String>();
        currentWord = new char[wordLength];

        for (int i = 0; i < wordLength; i++) {
            currentWord[i] = '_';
        }


        while (scanner.hasNext()) {
            String word = scanner.next();
            if (wordLength != word.length()) continue;

            currentWordList.add(word);
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        HashMap<String, HashSet<String>> wordHashMap;

        if (!Character.isLetter(guess)) {
            throw new GuessAlreadyMadeException();
        }

        if (getUsedLetters().contains(guess)) {
            throw new GuessAlreadyMadeException();
        }

        numberOfGuessesLeft--;
        addUsedLetter(guess);
        wordHashMap = updateCurrentWordList(guess, currentWordList);

        int largestList = 0;
        char[] tempKey = new char[0];

        for (String potentialKey : wordHashMap.keySet()) {
            if (wordHashMap.get(potentialKey).size() < largestList) {
                continue;
            }

            if (wordHashMap.get(potentialKey).size() == largestList) {
               // Log.d(TAG, "makeGuess: " + " currentKey: " + String.valueOf(tempKey) + " potentialKey : " + potentialKey);

                if (keepCurrentKey(guess, tempKey, potentialKey)) {
                    //Log.d(TAG, "makeGuess: we decided to keep the current key");
                    continue;
                }
                //Log.d(TAG, "makeGuess: new word chossen: " + potentialKey);
            }

            largestList = wordHashMap.get(potentialKey).size();
            currentWordList = wordHashMap.get(potentialKey);
            tempKey = potentialKey.toCharArray();
        }

        currentWord = combineKeys(tempKey, currentWord);

        return currentWordList;
    }


    private HashMap<String, HashSet<String>> updateCurrentWordList(char guess, HashSet<String> currentWords) {
        HashMap<String, HashSet<String>> wordHashMap = new HashMap<>();
        char[] tempKey = new char[0];

        for (String word : currentWords) {
            tempKey = getBlankKey(word.length());
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    tempKey[i] = guess;
                }
            }

//            Log.d(TAG, "makeGuess: key loop " + String.valueOf(key));

            try {
                wordHashMap.get(String.valueOf(tempKey)).add(word);
            } catch (Exception e) {
                HashSet<String> newWordSet = new HashSet<String>();
                newWordSet.add(word);
                wordHashMap.put(String.valueOf(tempKey), newWordSet);
            }

        }
        return wordHashMap;
    }

    
    private Boolean keepCurrentKey(char guess, char[] curKey, String potentialKey) {
        if (potentialKey.replaceAll("_", "").length() > String.valueOf(curKey).replaceAll("_", "").length()) {
            return true;
        }
        if (potentialKey.replaceAll("_", "").length() < String.valueOf(curKey).replaceAll("_", "").length()){
            return false;
        }
        if (potentialKey.replaceAll(String.valueOf(guess), "").length() <
                String.valueOf(curKey).replaceAll(String.valueOf(guess), "").length()) {
            return true;
        }
        if (potentialKey.replaceAll(String.valueOf(guess), "").length() >
                String.valueOf(curKey).replaceAll(String.valueOf(guess), "").length()) {
            return false;
        }

        else {
            for (int i = potentialKey.length() - 1; i >= 0; i--) {
                if (curKey[i] != potentialKey.charAt(i)) {
                    if (curKey[i] != "_".charAt(0)) {
                        // Current key wins
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }

        }

        return null;
    }

    private char[] combineKeys(char[] chars, char[] currentWord) {
        char[] newChar = new char[chars.length];

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != '_') {
                newChar[i] = chars[i];
            } else if (currentWord[i] != '_') {
                newChar[i] = currentWord[i];
            } else {
                newChar[i] = '_';
            }

        }
        return newChar;
    }

    private void addUsedLetter(char guess) {
        previouslyChosenLetters.add((String.valueOf(guess).toUpperCase()).charAt(0));
    }

    private char[] getBlankKey(int wordLength) {
        char[] key = new char[wordLength];

        for (int i = 0; i < wordLength; i++) {
            key[i] = '_';
        }

        return key;
    }


}
