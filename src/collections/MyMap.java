package collections;
import java.util.Arrays;
import java.util.Objects;

public class MyMap<K, V> extends AbstractMyCollection {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int modCount = 0;

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
        modCount++;
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
                modCount++;
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
        modCount++;
    }

    public Iterable<V> values() {
        final java.util.Iterator<java.util.Map.Entry<K, V>> ei = entryIterator();
        return new Iterable<V>() {
            @Override
            public java.util.Iterator<V> iterator() {
                return new java.util.Iterator<V>() {
                    @Override
                    public boolean hasNext() { return ei.hasNext(); }
                    @Override
                    public V next() { return ei.next().getValue(); }
                    @Override
                    public void remove() { ei.remove(); }
                };
            }
        };
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

    
    private static class Entry<K, V> implements java.util.Map.Entry<K, V> {
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
        
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V old = value;
            value = newValue;
            return old;
        }
    }
    
    public java.util.Iterator<java.util.Map.Entry<K, V>> entryIterator() {
        return new EntryItr();
    }

    public java.util.Iterator<K> keyIterator() {
        final java.util.Iterator<java.util.Map.Entry<K, V>> ei = entryIterator();
        return new java.util.Iterator<K>() {
            @Override
            public boolean hasNext() { return ei.hasNext(); }
            @Override
            public K next() { return ei.next().getKey(); }
            @Override
            public void remove() { ei.remove(); }
        };
    }

    public java.util.Iterator<V> valueIterator() {
        final java.util.Iterator<java.util.Map.Entry<K, V>> ei = entryIterator();
        return new java.util.Iterator<V>() {
            @Override
            public boolean hasNext() { return ei.hasNext(); }
            @Override
            public V next() { return ei.next().getValue(); }
            @Override
            public void remove() { ei.remove(); }
        };
    }

    private class EntryItr implements java.util.Iterator<java.util.Map.Entry<K, V>> {
        private int bucketIndex = 0;
        private Entry<K, V> next;
        private Entry<K, V> lastReturned;
        private int expectedModCount = modCount;

        EntryItr() {
            Entry<K, V>[] t = table;
            int len = t.length;
            Entry<K, V> e = null;
            for (int i = 0; i < len; i++) {
                if (t[i] != null) {
                    bucketIndex = i;
                    e = t[i];
                    break;
                }
            }
            next = e;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public java.util.Map.Entry<K, V> next() {
            checkForComodification();
            if (next == null) {
                throw new java.util.NoSuchElementException();
            }
            lastReturned = next;

            if (next.next != null) {
                next = next.next;
            } else {
                Entry<K, V>[] t = table;
                int len = t.length;
                Entry<K, V> e = null;
                for (int i = bucketIndex + 1; i < len; i++) {
                    if (t[i] != null) {
                        bucketIndex = i;
                        e = t[i];
                        break;
                    }
                }
                next = e;
            }

            return lastReturned;
        }

        @Override
        public void remove() {
            checkForComodification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            MyMap.this.remove(lastReturned.key);
            lastReturned = null;
            expectedModCount = modCount;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new java.util.ConcurrentModificationException();
        }
    }
}