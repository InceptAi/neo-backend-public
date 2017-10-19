package storage;

import models.*;
import nlu.SimpleTextInterpreter;
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


    public HashMap<String, String> searchActions(String inputText, String deviceInfo,
                                                 TextInterpreter textInterpreter, int maxResults) {
        HashMap<String, Double> semanticActionIdToMatchMetric = new HashMap<>();
        HashMap<String, String> semanticActionIdToBestMatchingString = new HashMap<>();
        double minMetricInserted = -1;
        String minIdInserted = Utils.EMPTY_STRING;

        for (SemanticAction semanticAction: semanticActionMap.values()) {
            List<String> referenceStringList = semanticAction.getStringsToMatch();
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
                if (semanticActionIdToMatchMetric.size() >= maxResults && bestMatchMetric > minMetricInserted) {
                    semanticActionIdToMatchMetric.remove(minIdInserted);
                    semanticActionIdToBestMatchingString.remove(minIdInserted);
                }
                if (semanticActionIdToMatchMetric.size() < maxResults) {
                    semanticActionIdToBestMatchingString.put(semanticAction.getId(), bestMatchingString);
                    semanticActionIdToMatchMetric.put(semanticAction.getId(), bestMatchMetric);
                    if (bestMatchMetric < minMetricInserted) {
                        minMetricInserted = bestMatchMetric;
                        minIdInserted = semanticAction.getId();
                    }
                }
            }
        }

        //Sort the hash maps and return
        HashMap<String, String> sortedSemanticActionIdToDescription = new HashMap<>();
        Map<String, Double> sortedMetricMap = Utils.sortHashMapByValueDescending(semanticActionIdToMatchMetric);
        for (HashMap.Entry<String, Double> entry : sortedMetricMap.entrySet()) {
            String bestMatchingStringForId = semanticActionIdToBestMatchingString.get(entry.getKey());
            sortedSemanticActionIdToDescription.put(entry.getKey(), bestMatchingStringForId);
        }
        return sortedSemanticActionIdToDescription;
    }

}
