package nlu;

public abstract class TextInterpreter {
    public abstract String sanitizeInputTextForMatching(String inputText);
    public abstract double getMatchMetric(String inputText, String referenceText);
}
