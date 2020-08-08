
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class AutoCorrect {
    public static void main(String[] args) {
        String FileName = "your vocabulary file path here";

        HashMap<String, Float> frequencyMap = buildMap(FileName);
        Set<String> vocabulary = frequencyMap.keySet();
        Scanner keyedinput = new Scanner(System.in);


        for (int i = 0; i < 1000; i++) {
            System.out.println("Please Enter A Word To Correct:");
            String inputword = keyedinput.nextLine();
            if (inputword.trim().equals("")) {
                continue;
            }
            if (vocabulary.contains(inputword)) {
                System.out.println("The Word You Inputed Is Correct.");
                System.out.println("----------------------------------------");
                System.out.println("");
                continue;
            }

            Set<String> candidates = getCandidateTwoEdit(inputword);

            candidates.retainAll(vocabulary);

            // get top ten frequency candidate
            Set<String> topFrequencyCandidate = getTopFrequencyWords(candidates, frequencyMap);


            String bestWord = " ";
            int bestDistance = Integer.MAX_VALUE;
            float bestFrequency = 0f;
            for (String word: topFrequencyCandidate) {
                int d = StringUtils.getLevenshteinDistance(inputword, word);
                if (d < bestDistance) {
                    bestDistance = d;
                    bestFrequency = frequencyMap.get(word);
                    bestWord = word;
                }
                else if (bestDistance == d) {
                    if (bestFrequency < frequencyMap.get(word)) {
                        bestFrequency = frequencyMap.get(word);
                        bestWord = word;
                    }
                }
            }
            if (bestWord.equals(" ")) {
                System.out.println("We could not correct this word: " + inputword);
                System.out.println("----------------------------------------");
                System.out.println("");
                continue;
            }

            System.out.println("The best corrected word is: " + bestWord);
            System.out.println("The frequency of the best corrected word is: " + bestFrequency);
            System.out.println("The distance of the best corrected word and your input is: " + bestDistance);
            System.out.println("----------------------------------------");
            System.out.println("");
        }

    }

    public static Set<String> getTopFrequencyWords(Set<String> words, HashMap<String, Float> wordMap) {
        Set<String> result = new HashSet<>();
        Set<Map.Entry<String, Float>> entries = wordMap.entrySet();
        Set<Map.Entry<String, Float>> goodEntries = new HashSet<>();
        for (Map.Entry<String, Float> entry: entries) {
            if (words.contains(entry.getKey())) {
                goodEntries.add(entry);
            }
        }

        List<Map.Entry<String, Float>> entryList = new ArrayList<>(goodEntries);
        Collections.sort(entryList, Comparator.comparing(en -> en.getValue()));
        for (int i = entryList.size() - 1; i > entryList.size() - 10 - 1 && i > -1; i--) {
            result.add(entryList.get(i).getKey());
        }
        return result;
    }

    //Simulate spelling and get candidate words
    public static Set<String> getCandidatesOneEdit(String inputword) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> deletedWord = getDeleteList(inputword);
        ArrayList<String> insertedWord = getInsertList(inputword);
        ArrayList<String> changedWords = getChangedList(inputword);
        ArrayList<String> switchedWords = getSwitchedList(inputword);
        ArrayList<String> totalNewWord = new ArrayList<>();
        totalNewWord.addAll(deletedWord);
        totalNewWord.addAll(insertedWord);
        totalNewWord.addAll(changedWords);
        totalNewWord.addAll(switchedWords);

        Set<String> newSet = new HashSet<>(totalNewWord);


        return newSet;
    }

    public static Set<String> getCandidateTwoEdit(String inputword) {
        Set<String> candidates = getCandidatesOneEdit(inputword);
        Set<String> candidates2 = new HashSet<>();
        for (String word : candidates) {
            candidates2.addAll(getCandidatesOneEdit(word));
        }
        return candidates2;
    }

    public static ArrayList<String> getSimilarList(String inputword, Set<String> vocab) {
        ArrayList<String> similarWords = new ArrayList<>();
        return similarWords;
    }

    public static ArrayList<String> getDeleteList(String inputWord) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < inputWord.length(); i++) {
            String newWord = inputWord.substring(0, i) + inputWord.substring(i + 1, inputWord.length());
            result.add(newWord);
        }
        return result;
    }


    public static ArrayList<String> getInsertList(String inputWord) {
        ArrayList<String> result = new ArrayList<>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < alphabet.length(); i++) {
            String aLetter = String.valueOf(alphabet.charAt(i));

            for (int j = 0; j < inputWord.length() + 1; j++) {
                String newWord;
                if (j < inputWord.length()) {
                    newWord = inputWord.substring(0, j) + aLetter + inputWord.substring(j, inputWord.length());
                } else {
                    newWord = inputWord.substring(0, j) + aLetter;
                }
                result.add(newWord);
            }
        }
        return result;
    }


    public static ArrayList<String> getSwitchedList(String inputWord) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; i < inputWord.length(); i++) {
            String newWord;
            if (i == 1) {
                newWord = inputWord.charAt(i) + inputWord.charAt(i - 1) + inputWord.substring(i + 1, inputWord.length());
            }
            if (i == inputWord.length() - 1) {
                newWord = inputWord.substring(0, i - 1) + inputWord.charAt(i) + inputWord.charAt(i - 1);
            } else {
                newWord = inputWord.substring(0, i - 1) + inputWord.charAt(i) + inputWord.charAt(i - 1) + inputWord.substring(i + 1, inputWord.length());
            }
            result.add(newWord);
        }
        return result;
    }

    public static ArrayList<String> getChangedList(String inputWord) {
        ArrayList<String> result = new ArrayList<>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < alphabet.length(); i++) {
            String aLetter = String.valueOf(alphabet.charAt(i));

            for (int j = 0; j < inputWord.length() + 1; j++) {
                String newWord;
                if (j < inputWord.length()) {
                    newWord = inputWord.substring(0, j) + aLetter + inputWord.substring(j + 1, inputWord.length());
                } else {
                    if (j < 1) {
                        newWord = aLetter;
                    }
                    else {
                        newWord = inputWord.substring(0, j - 1) + aLetter;
                    }
                }
                result.add(newWord);
            }
        }
        return result;
    }


    //Reead from file and build frequency map
    public static HashMap<String, Float> buildMap(String FileName) {
        String article = readFile(FileName);
        article = article.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
        String[] words = article.split(" ");
        int totalCount = 0;
        ArrayList<String> allWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 1) {
                allWords.add(words[i]);
            }
        }


        totalCount = allWords.size();

        HashMap<String, Integer> countMap = new HashMap<>();
        for (int i = 0; i < allWords.size(); i++) {
            int currentFrequency = 1;
            if (countMap.containsKey(allWords.get(i))) {
                currentFrequency = countMap.get(allWords.get(i)) + 1;
            }
            countMap.put(allWords.get(i), currentFrequency);
        }

        HashMap<String, Float> frequencyMap = new HashMap<>();
        Set<String> Keys = countMap.keySet();
        for (String key : Keys) {
            float frequency = 1.0f * countMap.get(key) / totalCount;
            frequencyMap.put(key, frequency);
        }
        return frequencyMap;
    }

    public static String readFile(String FileName) {
        String fileContent = " ";
        try {
            File myObj = new File(FileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                fileContent = fileContent + " " + line;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return fileContent;
    }
}
