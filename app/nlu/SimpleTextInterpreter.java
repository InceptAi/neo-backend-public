package nlu;

import util.Utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleTextInterpreter extends TextInterpreter {
    private static final double MIN_MATCH_PERCENTAGE = 0.8;

    @Override
    public String sanitizeInputTextForMatching(String inputText) {
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

        List<String> referenceWords = Arrays.asList(referenceText.split(" "));
        final Set<String> referenceSet = new HashSet<String>(referenceWords);

        int numMatches = 0;
        List<String> inputWords = Arrays.asList(inputText.split(" "));
        for (String inputWord: inputWords) {
            if (referenceSet.contains(inputWord)) {
                numMatches++;
            }
        }

        double inputMatch = (double)numMatches / inputWords.size();
        if (inputMatch < MIN_MATCH_PERCENTAGE) {
            return 0;
        }

        double overallMatch = (double)numMatches / (inputWords.size() + referenceSet.size());
        //Normalize -- max value can be 0.5, min is 0
        return overallMatch * 2.0;
    }
}
