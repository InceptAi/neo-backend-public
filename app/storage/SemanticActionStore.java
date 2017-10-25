package storage;

import models.*;
import nlu.TextInterpreter;
import util.Utils;

import java.util.*;

public class SemanticActionStore {
    private static SemanticActionStore instance;
    private Map<String, SemanticAction> semanticActionMap = new HashMap<>();

    public static SemanticActionStore getInstance() {
        if (instance == null) {
            instance = new SemanticActionStore();
        }
        return instance;
    }

    public SemanticAction addSemanticAction(SemanticAction semanticAction) {
        String key = semanticAction.getId();
        semanticActionMap.put(key, semanticAction);
        return semanticAction;
    }

    public SemanticAction getAction(String id) {
        return semanticActionMap.get(id);
    }

    public Set<SemanticAction> getAllActions() {
        return new HashSet<>(semanticActionMap.values());
    }

    public boolean deleteAction(String id) {
        return semanticActionMap.remove(id) != null;
    }

    public HashMap<String, SemanticActionMatchingTextAndScore> returnAllActions() {
        HashMap<String, SemanticActionMatchingTextAndScore> hashMapToReturn = new HashMap<>();
        for (SemanticAction semanticAction : semanticActionMap.values()) {
            hashMapToReturn.put(
                    semanticAction.getId(),
                    new SemanticActionMatchingTextAndScore(semanticAction.getSemanticActionDescription(), 0));
        }
        return hashMapToReturn;
    }

    public HashMap<String, SemanticActionMatchingTextAndScore> searchActions(String inputText, String deviceInfo,
                                                 TextInterpreter textInterpreter, int maxResults) {
        HashMap<String, Double> semanticActionIdToMatchMetric = new HashMap<>();
        HashMap<String, String> semanticActionIdToBestMatchingString = new HashMap<>();
        double minMetricInserted = Double.MAX_VALUE;
        String minIdInserted = Utils.EMPTY_STRING;
        for (SemanticAction semanticAction: semanticActionMap.values()) {
            if (!Utils.nullOrEmpty(deviceInfo) && !semanticAction.getDeviceInfo().equalsIgnoreCase(deviceInfo)) {
                //Match the device info here
                return new HashMap<>();
            }
            List<String> referenceStringList = semanticAction.fetchStringsToMatch();
            double bestMatchMetric = 0;
            String bestMatchingString = Utils.EMPTY_STRING;
            for (String referenceString: referenceStringList) {
                double matchMetric = textInterpreter.getMatchMetric(inputText, referenceString);
                if (matchMetric > bestMatchMetric) {
                    bestMatchMetric = matchMetric;
                    bestMatchingString = referenceString;
                }
            }
            if (bestMatchMetric > 0) {
                semanticActionIdToBestMatchingString.put(semanticAction.getId(), bestMatchingString);
                semanticActionIdToMatchMetric.put(semanticAction.getId(), bestMatchMetric);
            }
        }

        //Sort the hash maps and return
        LinkedHashMap<String, SemanticActionMatchingTextAndScore> sortedSemanticActionIdToDescriptionAndScore = new LinkedHashMap<>();
        Map<String, Double> sortedMetricMap = Utils.sortHashMapByValueDescending(semanticActionIdToMatchMetric);
        int numActions = 0;
        for (HashMap.Entry<String, Double> entry : sortedMetricMap.entrySet()) {
            if (numActions >= maxResults) {
                break;
            }
            String bestMatchingStringForId = semanticActionIdToBestMatchingString.get(entry.getKey());
            sortedSemanticActionIdToDescriptionAndScore.put(
                    entry.getKey(),
                    new SemanticActionMatchingTextAndScore(bestMatchingStringForId, entry.getValue()));
            numActions++;
        }
        return sortedSemanticActionIdToDescriptionAndScore;
    }

    public class SemanticActionMatchingTextAndScore {
        String matchingDescription;
        double confidenceScore;
        SemanticActionMatchingTextAndScore(String matchingDescription, double confidenceScore) {
            this.matchingDescription = matchingDescription;
            this.confidenceScore = confidenceScore;
        }

        public String getMatchingDescription() {
            return matchingDescription;
        }

        public void setMatchingDescription(String matchingDescription) {
            this.matchingDescription = matchingDescription;
        }

        public double getConfidenceScore() {
            return confidenceScore;
        }

        public void setConfidenceScore(double confidenceScore) {
            this.confidenceScore = confidenceScore;
        }
    }

}
