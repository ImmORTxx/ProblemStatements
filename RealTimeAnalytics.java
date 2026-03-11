import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalytics {

    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(PageEvent event) {
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);
        uniqueVisitors.computeIfAbsent(event.url, k -> new HashSet<>()).add(event.userId);
        trafficSources.put(event.source, trafficSources.getOrDefault(event.source, 0) + 1);
    }

    public void getDashboard() {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");
        int rank = 1;

        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, new HashSet<>()).size();

            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)");
            rank++;
        }

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> e : trafficSources.entrySet()) {
            double percent = (e.getValue() * 100.0) / total;
            System.out.printf("%s: %.0f%%\n", e.getKey(), percent);
        }
    }

    public static void main(String[] args) throws Exception {
        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent(new PageEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_789", "direct"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_123", "google"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_999", "google"));

        analytics.getDashboard();
    }
}