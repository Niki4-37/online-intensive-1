package hashmap;

import java.util.Objects;

public class SimpleHashMap<K,V> {

    static final int DEFAULT_CAPACITY = 1 << 4;
    static final int MAX_CAPACITY = 1 << 30;
    
    int capacity;
    int size;
    final float loadFactor;
    MapPair<K,V>[] buckets;

    public SimpleHashMap() {
        buckets = (MapPair<K,V>[]) new MapPair[DEFAULT_CAPACITY];
        capacity = DEFAULT_CAPACITY;
        loadFactor = 0.75f;
        size = 0;
    }

    int hash(Object o) {
        int h;
        return o == null ? 0 : (h = o.hashCode()) ^ (h >>> 16); 
    }

    int indexByHash(int hash) {
        return (capacity - 1) & hash;
    }

    public void put(K key, V value) {
        if (key == null) return;
        
        int hash = hash(key);
        int bucketIndex = indexByHash(hash);

        for (
            MapPair<K,V> bucketPair = buckets[bucketIndex]; 
            bucketPair != null; 
            bucketPair = bucketPair.nextPair
            ) {
            if (
                bucketPair.hash == hash
                && Objects.equals(key, bucketPair.key)
                ) {
                bucketPair.value = value;
                return;
            }
            if (
                bucketPair.nextPair == null
                ) {
                bucketPair.nextPair = new MapPair<K,V>(hash, key, value, null);
                ++size;
                return;
            }
                    
        }

        buckets[bucketIndex] = new MapPair<K,V>(hash, key, value, null);
        ++size;
        if (size >= capacity*loadFactor) {
            resize();
        }
    }

    public int size() { return size; }

    public V get(Object key) {
        if (key == null) return null;
        
        int hash = hash(key);
        int bucketIndex = indexByHash(hash);
        
        for (
            MapPair<K,V> bucketPair = buckets[bucketIndex]; 
            bucketPair != null; 
            bucketPair = bucketPair.nextPair
            ) {
                if (bucketPair.hash == hash
                    && Objects.equals(key, bucketPair.key))
                    return bucketPair.value;
        }
        return null;
    }

    public V remove(Object key) {
        if (key == null) return null;
        
        int hash = hash(key);
        int bucketIndex = indexByHash(hash);
        
        MapPair<K,V> prevPair = null, currentPair = buckets[bucketIndex];
        while (currentPair != null) {
            if (
                hash == currentPair.hash
                && Objects.equals(key, currentPair.key)
                ) {
                if (
                    prevPair == null
                ) { 
                    buckets[bucketIndex] = currentPair.nextPair;
                } else {
                    prevPair.nextPair = currentPair.nextPair;
                }
                --size;
                return currentPair.value;
            }
            prevPair = currentPair;
            currentPair = currentPair.nextPair;
        }
        return null;
    }

    void resize() {
        int oldCap = capacity;
        if (oldCap >= MAX_CAPACITY) return;
        capacity = oldCap << 1;
        MapPair<K,V>[] oldBuckets = buckets;
        buckets = (MapPair<K,V>[]) new MapPair[capacity];
        size = 0;
        for (var oldBucket : oldBuckets) {
            MapPair<K,V> linkedPair = oldBucket;
            while (linkedPair != null) {
                put(linkedPair.key, linkedPair.value);
                linkedPair = linkedPair.nextPair;
            }
        }
    }

    static class MapPair<K,V> {
        final int hash;
        final K key;
        V value;
        MapPair<K,V> nextPair;

        MapPair(int hash, K key, V value, MapPair<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.nextPair = next;
        }

        @Override
        public String toString() {
            String keyString = key == null ? "null" : key.toString();
            String valueString = value == null ? "null" : value.toString();
            return "[key: " + keyString + ", value: " + valueString + "]\n";
        }
    }

    @Override
    public String toString() {
        if (size <= 0) return "Empty map";
        StringBuilder stringVault = new StringBuilder();
        stringVault.append("Map size: " + size + '\n');
        MapPair<K,V> bucketPair;
        for (int i = 0; i < buckets.length; ++i) {
            bucketPair = buckets[i];
            while (bucketPair != null) {
                stringVault.append(bucketPair);
                bucketPair = bucketPair.nextPair;
            }
        }
        return stringVault.toString();
    }
}
