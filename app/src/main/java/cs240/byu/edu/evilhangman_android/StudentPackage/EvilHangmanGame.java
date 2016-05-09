package cs240.byu.edu.evilhangman_android.StudentPackage;

import android.util.Log;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

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
        currentWordList =  new HashSet<String>();
        currentWord = new char[wordLength];

        for (int i = 0; i < wordLength; i++) {
            currentWord[i] = '_';
        }


        while (scanner.hasNext()){
            String word = scanner.next();
            if (wordLength != word.length()) continue;

            currentWordList.add(word);
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        HashMap<String, HashSet<String>> wordHashMap = new HashMap<>();
        char[] tempKey = new char[0];

        if (!Character.isLetter(guess)){
            throw new GuessAlreadyMadeException();
        }

        if (getUsedLetters().contains(guess)) {
            throw new GuessAlreadyMadeException();
        }

        numberOfGuessesLeft--;

        addUsedLetter(guess);

        for (String word : currentWordList) {
            tempKey = getBlankKey(word.length());
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess){
                    tempKey[i] = guess;
                }
            }

//            Log.d(TAG, "makeGuess: key loop " + String.valueOf(key));

            try{
                wordHashMap.get(String.valueOf(tempKey)).add(word);
            }
            catch (Exception e){
                HashSet<String> newWordSet = new HashSet<String>();
                newWordSet.add(word);
                wordHashMap.put(String.valueOf(tempKey), newWordSet);
            }

        }

        int largestList = 0;
        for (String key : wordHashMap.keySet()) {
//            Log.d(TAG, "makeGuess: for loop key size " + wordHashMap.get(key).size());
//            Log.d(TAG, "makeGuess: for loop key " + key);
            if (wordHashMap.get(key).size() > largestList){
                largestList = wordHashMap.get(key).size();
                currentWordList = wordHashMap.get(key);
                tempKey = key.toCharArray();

            }
        }

        currentWord = combineKeys(tempKey, currentWord);

        Log.d(TAG, "makeGuess: word key " + String.valueOf(currentWord));
        Log.d(TAG, "makeGuess: currentWOrdList Size " + currentWordList.size());



        return currentWordList;
    }

    private char[] combineKeys(char[] chars, char[] currentWord) {
        char[] newChar = new char[chars.length];

        for (int i = 0; i < chars.length; i++){
            if (chars[i] != '_'){
                newChar[i] = chars[i];
            }
            else if (currentWord[i] != '_'){
                newChar[i] = currentWord[i];
            }
            else{
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
