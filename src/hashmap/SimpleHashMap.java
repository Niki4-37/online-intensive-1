package hashmap;
public class SimpleHashMap<K,V> {

    static final int DEFAULT_CAPACITY = 16;
    
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

    public void put(K key, V value) {
        putToBucket(hash(key), key, value);
    }

    public int size() { return size; }

    void putToBucket(int hash, K key, V value) {
        int bucketIndex, bucketCapacity;
        MapPair<K,V> bucketPair;
        bucketCapacity = buckets.length;
        bucketIndex = (bucketCapacity - 1) & hash;

        if ((bucketPair = buckets[bucketIndex]) == null) {
            buckets[bucketIndex] = new MapPair<>(hash, key, value, null);
        } else {
            MapPair<K,V> tempPair;
            K bucketPairKey;
            if (bucketPair.hash == hash 
                && ((bucketPairKey = bucketPair.key) == key || (key != null && key.equals(bucketPairKey)))) {
                tempPair = bucketPair;
            } else {
                while (true) {
                    tempPair = bucketPair.nextPair;
                    if (tempPair == null) {
                        bucketPair.nextPair = new MapPair<>(hash, key, value, null);
                        break;
                    }
                    if (tempPair.hash == hash 
                        && ((bucketPairKey = tempPair.key) == key || (key != null && key.equals(bucketPairKey)))) {
                            break;
                        }
                    bucketPair = tempPair;
                }
            }
            if (tempPair != null) {
                tempPair.value = value;
                return;
            }
        }
        ++size;
    }

    public V get(Object key) {
        if (key == null) return null;
        MapPair<K,V> bucketPair;
        int hash = hash(key);
        for (int i = 0; i < buckets.length; ++i) {
            if ((bucketPair = buckets[i]) == null) continue; 
            if (hash == bucketPair.hash
                && bucketPair.key == key || key.equals(bucketPair.key)) return bucketPair.value;
            
            MapPair<K,V> nextPair = bucketPair.nextPair;
            K bucketPairKey;
            while (nextPair != null) {
                if (hash == nextPair.hash
                    && ((bucketPairKey = nextPair.key) == key || key.equals(bucketPairKey))) {
                    return nextPair.value;
                }
                nextPair = nextPair.nextPair; 
            }
        }
        return null;
    }

    public V remove(Object key) {
        if (key == null) return null;
        MapPair<K,V> bucketPair;
        int hash = hash(key);
        for (int i = 0; i < buckets.length; ++i) {
            if ((bucketPair = buckets[i]) == null) continue; 
            if (hash == bucketPair.hash
                && bucketPair.key == key || key.equals(bucketPair.key)) {
                    if (bucketPair.nextPair != null) buckets[i] = bucketPair.nextPair;
                    --size;
                    return bucketPair.value;
                }
            
            MapPair<K,V> nextPair = bucketPair.nextPair;
            MapPair<K,V> currenyPair = bucketPair; 
            K bucketPairKey;
            while (nextPair != null) {
                if (hash == nextPair.hash
                    && ((bucketPairKey = nextPair.key) == key || key.equals(bucketPairKey))) {
                        currenyPair.nextPair = nextPair.nextPair;
                        --size;
                        return nextPair.value;
                }
                currenyPair = nextPair;
                nextPair = nextPair.nextPair; 
            }
        }
        return null;
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
        MapPair<K,V> bucketPair;
        MapPair<K,V> nextPair;
        for (int i = 0; i < buckets.length; ++i) {
            if ((bucketPair = buckets[i]) == null) {
                continue;
            }
            if (bucketPair != null) {
                stringVault.append(bucketPair);
            }
            nextPair = bucketPair.nextPair;
            while (nextPair != null) {
                stringVault.append(nextPair);
                nextPair = nextPair.nextPair;
            }
        }
        return stringVault.toString();
    }
}
