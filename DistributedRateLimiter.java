import java.util.*;
import java.util.concurrent.*;

class TokenBucket {
    int tokens;
    int maxTokens;
    long lastRefillTime;

    TokenBucket(int maxTokens) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }
}

public class DistributedRateLimiter {

    private ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private int limit = 1000;
    private long window = 3600_000;

    private void refill(TokenBucket bucket) {
        long now = System.currentTimeMillis();
        if (now - bucket.lastRefillTime >= window) {
            bucket.tokens = bucket.maxTokens;
            bucket.lastRefillTime = now;
        }
    }

    public synchronized String checkRateLimit(String clientId) {
        TokenBucket bucket = clients.computeIfAbsent(clientId, k -> new TokenBucket(limit));
        refill(bucket);

        if (bucket.tokens > 0) {
            bucket.tokens--;
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            long retry = (window - (System.currentTimeMillis() - bucket.lastRefillTime)) / 1000;
            return "Denied (0 requests remaining, retry after " + retry + "s)";
        }
    }

    public Map<String, Long> getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) return Collections.emptyMap();

        long reset = bucket.lastRefillTime + window;
        Map<String, Long> result = new HashMap<>();
        result.put("used", (long) (limit - bucket.tokens));
        result.put("limit", (long) limit);
        result.put("reset", reset);
        return result;
    }

    public static void main(String[] args) {
        DistributedRateLimiter limiter = new DistributedRateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}