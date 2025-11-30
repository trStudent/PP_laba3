package collections;
import java.util.Arrays;
import java.util.Objects;

public class MyList<E> extends AbstractMyCollection {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int modCount = 0;

    public MyList() {
        this(DEFAULT_CAPACITY);
    }

    public MyList(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be > 0");
        }
        elements = new Object[initialCapacity];
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length<<1; // x2
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    public void add(E e) {
        ensureCapacity(size + 1);
        elements[size] = e;
        increaseSize();
        modCount++;
    }

    public void add(int index, E e) {
        rangeCheckForAdd(index);
        ensureCapacity(size + 1);

        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = e;
        increaseSize();
        modCount++;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        rangeCheck(index);
        return (E) elements[index];
    }

    @SuppressWarnings("unchecked")
    public E set(int index, E e) {
        rangeCheck(index);
        E old = (E) elements[index];
        elements[index] = e;
        return old;
    }

    @SuppressWarnings("unchecked")
    public E remove(int index) {
        rangeCheck(index);
        E old = (E) elements[index];
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null;
        modCount++;
        return old;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
        modCount++;
    }

    public boolean contains(E e) {
        return indexOf(e) >= 0;
    }

    public int indexOf(E e) {
        if (e == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (e.equals(elements[i])) return i;
            }
        }
        return -1;
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(indexOutOfBoundsMsg(index));
        }
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(indexOutOfBoundsMsg(index));
        }
    }

    private String indexOutOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }
    
    public java.util.Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements java.util.Iterator<E> {
        private int cursor = 0;       // index of next element to return
        private int lastRet = -1;     // index of last element returned; -1 if none
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();
            if (cursor >= size) {
                throw new java.util.NoSuchElementException();
            }
            lastRet = cursor;
            E e = (E) elements[cursor];
            cursor++;
            return e;
        }

        @Override
        public void remove() {
            checkForComodification();
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            MyList.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = modCount;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new java.util.ConcurrentModificationException();
        }
    }
}

