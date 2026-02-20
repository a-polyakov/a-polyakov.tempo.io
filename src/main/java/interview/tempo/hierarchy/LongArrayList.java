package interview.tempo.hierarchy;

public class LongArrayList<E> {

    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final long initialCapacity;
    private final Object[][] matrix;
    private long size;

    public LongArrayList(long initialCapacity) {
        this.initialCapacity = initialCapacity;
        size = 0;
        int rows = (int) (initialCapacity / MAX_ARRAY_SIZE);
        long columns = initialCapacity % MAX_ARRAY_SIZE;
        if (columns > 0) {
            rows++;
        }
        matrix = new Object[rows][];
        long c = initialCapacity;
        for (int i = 0; i < rows; i++) {
            if (c > MAX_ARRAY_SIZE) {
                matrix[i] = new Object[MAX_ARRAY_SIZE];
            } else {
                matrix[i] = new Object[(int) c];
            }
            c -= MAX_ARRAY_SIZE;
        }
    }

    public boolean add(E e) {
        boolean added = size < initialCapacity;
        if (added) {
            int rows = (int) (size / MAX_ARRAY_SIZE);
            int columns = (int) (size % MAX_ARRAY_SIZE);
            matrix[rows][columns] = e;
            size++;
        }
        return added;
    }

    public E get(long index) {
        if (index>=0 && index < size) {
            int rows = (int) (index / MAX_ARRAY_SIZE);
            int columns = (int) (index % MAX_ARRAY_SIZE);
            return (E) matrix[rows][columns];
        }
        throw new IndexOutOfBoundsException();
    }

    public long size() {
        return size;
    }
}
