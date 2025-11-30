package collections;

import java.util.Iterator;

public interface MyIterable<T> {
    Iterator<T> iterator();
}