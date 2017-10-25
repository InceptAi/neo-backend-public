package nlu;

import util.Utils;

import java.lang.reflect.Array;
import java.util.*;

public class SimpleTextInterpreter extends TextInterpreter {
    private static final double MIN_MATCH_PERCENTAGE = 0.5;
    private double minMatchPercentage;

    public SimpleTextInterpreter(double minMatchPercentage) {
        this.minMatchPercentage = minMatchPercentage;
    }

    public SimpleTextInterpreter() {
        this.minMatchPercentage = MIN_MATCH_PERCENTAGE;
    }

    @Override
    public String sanitizeInputTextForMatching(String inputText) {
        //TODO: Remove special characters, also remove plurals
        if (Utils.nullOrEmpty(inputText)) {
            return inputText;
        }
        return inputText.toLowerCase();
    }

    @Override
    public double getMatchMetric(String inputText, String referenceText) {
        //Break input text into words, and see how many words occur in reference text
        if (Utils.nullOrEmpty(inputText) || Utils.nullOrEmpty(referenceText)) {
            return 0;
        }

        //Sanitize the text
        inputText = sanitizeInputTextForMatching(inputText);
        referenceText = sanitizeInputTextForMatching(referenceText);

        List<String> referenceWords = Arrays.asList(referenceText.split(" "));
        final Set<String> referenceSet = new HashSet<String>(referenceWords);

        int numMatches = 0;
        List<String> inputWords = Arrays.asList(inputText.split(" "));
        for (String inputWord: inputWords) {
            if (referenceSet.contains(getSingularForm(inputWord)) || referenceSet.contains(getPluralForm(inputWord))) {
                numMatches++;
            }
        }

        double inputMatch = (double)numMatches / inputWords.size();
        if (inputMatch < minMatchPercentage) {
            return 0;
        }

        double overallMatch = (double)numMatches / (inputWords.size() + referenceSet.size());
        //Normalize -- max value can be 0.5, min is 0
        return overallMatch * 2.0;
    }

    private String getPluralForm(String word) {
        if (Utils.nullOrEmpty(word)) {
            return Utils.EMPTY_STRING;
        }

        if (word.endsWith("s")) {
            return word;
        } else {
            return word + "s";
        }
    }

    private String getSingularForm(String word) {
        if (Utils.nullOrEmpty(word)) {
            return Utils.EMPTY_STRING;
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        } else {
            return word;
        }
    }
}
