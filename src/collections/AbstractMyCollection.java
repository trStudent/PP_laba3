package collections;
public abstract class AbstractMyCollection {
    protected int size;
    protected AbstractMyCollection() {
        this.size = 0;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    protected void increaseSize() {
        size++;
    }
    protected void decreaseSize() {
        size--;
    }
    public abstract void clear();
}