import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequency = new HashMap<>();

    public void addQuery(String query) {
        frequency.put(query, frequency.getOrDefault(query, 0) + 1);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    private void collect(TrieNode node, String prefix, List<String> results) {
        if (node.isEnd) results.add(prefix);

        for (Map.Entry<Character, TrieNode> e : node.children.entrySet()) {
            collect(e.getValue(), prefix + e.getKey(), results);
        }
    }

    public List<String> search(String prefix) {
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }

        List<String> all = new ArrayList<>();
        collect(node, prefix, all);

        PriorityQueue<String> pq = new PriorityQueue<>(
                (a, b) -> frequency.get(a) - frequency.get(b)
        );

        for (String s : all) {
            pq.offer(s);
            if (pq.size() > 10) pq.poll();
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) result.add(pq.poll());
        Collections.reverse(result);
        return result;
    }

    public int updateFrequency(String query) {
        addQuery(query);
        return frequency.get(query);
    }

    public static void main(String[] args) {
        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");

        System.out.println(system.search("jav"));
        System.out.println(system.updateFrequency("java 21 features"));
        System.out.println(system.updateFrequency("java 21 features"));
        System.out.println(system.updateFrequency("java 21 features"));
    }
}