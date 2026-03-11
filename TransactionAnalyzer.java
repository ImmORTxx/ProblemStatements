import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    public List<int[]> findTwoSum(List<Transaction> list, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : list) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public List<int[]> findTwoSumWithWindow(List<Transaction> list, int target, long window) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : list) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                Transaction other = map.get(complement);
                if (Math.abs(t.time - other.time) <= window) {
                    result.add(new int[]{other.id, t.id});
                }
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public List<List<Integer>> findKSum(List<Transaction> list, int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(list, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(List<Transaction> list, int k, int target, int start, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == k && target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        if (path.size() >= k) return;

        for (int i = start; i < list.size(); i++) {
            Transaction t = list.get(i);
            path.add(t.id);
            backtrack(list, k, target - t.amount, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    public Map<String, Set<String>> detectDuplicates(List<Transaction> list) {
        Map<String, Set<String>> map = new HashMap<>();

        for (Transaction t : list) {
            String key = t.amount + "-" + t.merchant;
            map.computeIfAbsent(key, x -> new HashSet<>()).add(t.account);
        }

        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : map.entrySet()) {
            if (e.getValue().size() > 1) result.put(e.getKey(), e.getValue());
        }

        return result;
    }

    public static void main(String[] args) {
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1, 500, "StoreA", "acc1", 1000));
        transactions.add(new Transaction(2, 300, "StoreB", "acc2", 1100));
        transactions.add(new Transaction(3, 200, "StoreC", "acc3", 1200));
        transactions.add(new Transaction(4, 500, "StoreA", "acc2", 1300));

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        System.out.println(analyzer.findTwoSum(transactions, 500));
        System.out.println(analyzer.findTwoSumWithWindow(transactions, 500, 500));
        System.out.println(analyzer.findKSum(transactions, 3, 1000));
        System.out.println(analyzer.detectDuplicates(transactions));
    }
}