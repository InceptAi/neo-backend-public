package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String EMPTY_STRING = "";
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
}