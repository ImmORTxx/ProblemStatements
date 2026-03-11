import java.util.*;

public class PlagiarismDetector {

    private Map<String, Set<String>> index = new HashMap<>();
    private int n = 5;

    private List<String> generateNGrams(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> grams = new ArrayList<>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            grams.add(sb.toString().trim());
        }
        return grams;
    }

    public void addDocument(String docId, String text) {
        List<String> grams = generateNGrams(text);
        for (String g : grams) {
            index.computeIfAbsent(g, k -> new HashSet<>()).add(docId);
        }
    }

    public void analyzeDocument(String docId, String text) {
        List<String> grams = generateNGrams(text);
        Map<String, Integer> matchCount = new HashMap<>();

        for (String g : grams) {
            Set<String> docs = index.get(g);
            if (docs != null) {
                for (String d : docs) {
                    matchCount.put(d, matchCount.getOrDefault(d, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + grams.size() + " n-grams");

        for (Map.Entry<String, Integer> e : matchCount.entrySet()) {
            String otherDoc = e.getKey();
            int matches = e.getValue();
            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Found " + matches + " matching n-grams with \"" + otherDoc + "\"");
            System.out.printf("Similarity: %.1f%%", similarity);

            if (similarity > 50) {
                System.out.println(" (PLAGIARISM DETECTED)");
            } else if (similarity > 10) {
                System.out.println(" (suspicious)");
            } else {
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        String doc1 = "data structures and algorithms are important for software development";
        String doc2 = "algorithms and data structures are essential for programming and software development";
        String newDoc = "data structures and algorithms are important for programming";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);

        detector.analyzeDocument("essay_123.txt", newDoc);
    }
}