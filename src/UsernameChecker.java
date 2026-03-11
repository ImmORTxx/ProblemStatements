import java.util.*;

public class UsernameChecker {
    private Map<String, Integer> usernameMap = new HashMap<>();
    private Map<String, Integer> attemptFrequency = new HashMap<>();
    private int userIdCounter = 1;

    public boolean checkAvailability(String username) {
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        return !usernameMap.containsKey(username);
    }

    public void registerUser(String username) {
        if (!usernameMap.containsKey(username)) {
            usernameMap.put(username, userIdCounter++);
        }
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int i = 1;
        while (suggestions.size() < 3) {
            String candidate = username + i;
            if (!usernameMap.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            i++;
        }
        String alt = username.replace("_", ".");
        if (!usernameMap.containsKey(alt) && suggestions.size() < 5) {
            suggestions.add(alt);
        }
        return suggestions;
    }

    public String getMostAttempted() {
        String result = null;
        int max = 0;
        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();
        checker.registerUser("john_doe");

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));
        System.out.println(checker.suggestAlternatives("john_doe"));
        System.out.println(checker.getMostAttempted());
    }
}