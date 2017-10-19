package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.*;

public class Utils {
    public static String EMPTY_STRING = "";
    public static String PRINT_MODE = "DEBUG";
    public static ObjectNode createResponse(Object response, boolean ok) {
        ObjectNode result = Json.newObject();
        result.put("isSuccessfull", ok);
        if (response instanceof String)
            result.put("body", (String) response);
        else result.set("body", (JsonNode) response);

        return result;
    }

    public static boolean nullOrEmpty(String target) {
        return target == null || target.isEmpty() || target.equals("null");
    }

    public static List<String> splitSentenceToWords(String sentence) {
        if (nullOrEmpty(sentence)) {
            return new ArrayList<>();
        }
        return Arrays.asList(sentence.split(" "));
    }

    public static void printDebug(String stringToPrint) {
        if (PRINT_MODE.equalsIgnoreCase("DEBUG")) {
            System.out.println(stringToPrint);
        }
    }

    public static boolean containsWord(String sentence, String word) {
        if(nullOrEmpty(sentence) || nullOrEmpty(word)) {
            return false;
        }
        List<String> words = splitSentenceToWords(sentence);
        for (String inputWord: words) {
            if (inputWord.trim().equalsIgnoreCase(word.trim())) {
                return true;
            }
        }
        return false;
    }

    public static String replaceWord(String inputSentence, HashMap<String, String> replacementMap) {
        if (inputSentence == null) {
            return null;
        }

        if (replacementMap == null) {
            return inputSentence;
        }

        List<String> words = Arrays.asList(inputSentence.split(" "));
        StringBuilder replacedSentenceBuilder = new StringBuilder();
        for (String word: words) {
            String replacementWord = replacementMap.get(word.toLowerCase());
            if (replacementWord != null) {
                replacedSentenceBuilder.append(replacementWord);
            } else {
                replacedSentenceBuilder.append(word);
            }
        }
        return replacedSentenceBuilder.toString().toLowerCase();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortHashMapByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}