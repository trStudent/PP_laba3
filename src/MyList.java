import java.util.Arrays;
import java.util.Objects;

public class MyList<E> extends AbstractMyCollection {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;

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
    }

    public void add(int index, E e) {
        rangeCheckForAdd(index);
        ensureCapacity(size + 1);

        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = e;
        increaseSize();
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
        return old;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
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
}

