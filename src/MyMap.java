import java.util.Arrays;
import java.util.Objects;

public class MyMap<K, V> extends AbstractMyCollection {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] table = new Entry[DEFAULT_INITIAL_CAPACITY];
    
    private int threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    private final float loadFactor;

    public MyMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0 || loadFactor <= 0)
            throw new IllegalArgumentException("Capacity and loadFactor must be > 0");
        
        int cap = 1;
        while (cap < initialCapacity) cap <<= 1;
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.loadFactor = loadFactor;
        this.threshold = (int) (cap * loadFactor);
    }

    private int hash(Object key) {
        int h = (key == null ? 0 : key.hashCode());
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    public V put(K key, V value) {
        int h = hash(key);
        int idx = indexFor(h, table.length);
        for (Entry<K, V> e = table[idx]; e != null; e = e.next) {
            if (e.hash == h && Objects.equals(e.key, key)) {
                V old = e.value;
                e.value = value;
                return old;
            }
        }
        
        Entry<K, V> e = new Entry<>(h, key, value, table[idx]);
        table[idx] = e;
        increaseSize();
        if (size > threshold) {
            resize(2 * table.length);
        }
        return null;
    }

    public V get(Object key) {
        int h = hash(key);
        int idx = indexFor(h, table.length);
        for (Entry<K, V> e = table[idx]; e != null; e = e.next) {
            if (e.hash == h && Objects.equals(e.key, key)) {
                return e.value;
            }
        }
        return null;
    }

    public V remove(Object key) {
        int h = hash(key);
        int idx = indexFor(h, table.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = table[idx]; e != null; prev = e, e = e.next) {
            if (e.hash == h && Objects.equals(e.key, key)) {
                if (prev == null) {
                    table[idx] = e.next;
                } else {
                    prev.next = e.next;
                }
                V old = e.value;
                e.value = null;
                decreaseSize();
                return old;
            }
        }
        return null;
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    public boolean containsValue(Object value) {
        for (Entry<K, V> bucket : table) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                if (Objects.equals(e.value, value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] oldTable = table;
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];
        
        for (Entry<K, V> bucket : oldTable) {
            for (Entry<K, V> e = bucket; e != null; ) {
                Entry<K, V> next = e.next;
                int idx = indexFor(e.hash, newCapacity);
                e.next = newTable[idx];
                newTable[idx] = e;
                e = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Entry<K, V> bucket : table) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                if (!first) sb.append(", ");
                sb.append(e.key).append("=").append(e.value);
                first = false;
            }
        }
        sb.append("}");
        return sb.toString();
    }

    
    private static class Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Entry<K, V> next;

        Entry(int hash, K key, V value, Entry<K, V> next) {
            this.hash = hash;
            this.key  = key;
            this.value = value;
            this.next = next;
        }
    }
}