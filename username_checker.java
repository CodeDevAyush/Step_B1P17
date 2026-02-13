import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class username_checker {
    private final ConcurrentHashMap<String, Long> userRegistry = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Integer> popularityTracker = new ConcurrentHashMap<>();

    public boolean checkAvailability(String username) {

        popularityTracker.merge(username, 1, Integer::sum);

        return !userRegistry.containsKey(username.toLowerCase());
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        String base = username.toLowerCase();

        for (int i = 1; suggestions.size() < 3; i++) {
            String candidate = base + i;
            if (!userRegistry.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        String dotCandidate = base + ".official";
        if (!userRegistry.containsKey(dotCandidate)) {
            suggestions.add(dotCandidate);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        return popularityTracker.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No attempts recorded");
    }

    public void registerUser(String username, Long id) {
        userRegistry.put(username.toLowerCase(), id);
    }

    public static void main(String[] args) {
        username_checker system = new username_checker();
        system.registerUser("john_doe", 101L);

        String testName = "john_doe";
        if (!system.checkAvailability(testName)) {
            System.out.println(testName + " is taken.");
            System.out.println("Suggestions: " + system.suggestAlternatives(testName));
        }

        System.out.println("Most Attempted: " + system.getMostAttempted());
    }
}