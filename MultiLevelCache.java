import java.util.*;

class Video {
    String id;
    String data;

    Video(String id, String data) {
        this.id = id;
        this.data = data;
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V> {
    int capacity;

    LRUCache(int capacity) {
        super(capacity,0.75f,true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> e) {
        return size() > capacity;
    }
}

public class MultiLevelCache {

    private LRUCache<String,Video> L1 = new LRUCache<>(10000);
    private LRUCache<String,Video> L2 = new LRUCache<>(100000);
    private Map<String,Video> L3 = new HashMap<>();
    private Map<String,Integer> accessCount = new HashMap<>();

    private int l1Hits;
    private int l2Hits;
    private int l3Hits;

    public MultiLevelCache() {
        for(int i=1;i<=200000;i++){
            String id="video_"+i;
            L3.put(id,new Video(id,"data_"+i));
        }
    }

    public Video getVideo(String id) {

        if(L1.containsKey(id)){
            l1Hits++;
            accessCount.put(id,accessCount.getOrDefault(id,0)+1);
            return L1.get(id);
        }

        if(L2.containsKey(id)){
            l2Hits++;
            Video v=L2.get(id);
            accessCount.put(id,accessCount.getOrDefault(id,0)+1);
            if(accessCount.get(id)>3){
                L1.put(id,v);
            }
            return v;
        }

        if(L3.containsKey(id)){
            l3Hits++;
            Video v=L3.get(id);
            accessCount.put(id,accessCount.getOrDefault(id,0)+1);
            L2.put(id,v);
            return v;
        }

        return null;
    }

    public void invalidate(String id){
        L1.remove(id);
        L2.remove(id);
        L3.remove(id);
        accessCount.remove(id);
    }

    public void printStats(){
        int total=l1Hits+l2Hits+l3Hits;
        double l1Rate=total==0?0:(l1Hits*100.0)/total;
        double l2Rate=total==0?0:(l2Hits*100.0)/total;
        double l3Rate=total==0?0:(l3Hits*100.0)/total;

        System.out.println("L1 Hit Rate "+String.format("%.2f",l1Rate)+"%");
        System.out.println("L2 Hit Rate "+String.format("%.2f",l2Rate)+"%");
        System.out.println("L3 Hit Rate "+String.format("%.2f",l3Rate)+"%");
    }

    public static void main(String[] args) {

        MultiLevelCache cache=new MultiLevelCache();

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");
        cache.getVideo("video_123");

        cache.printStats();
    }
}