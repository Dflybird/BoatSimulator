package gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:35
 */
public class SyncedCacheBuffer<DataType> {

    private final LinkedList<DataType> buffer = new LinkedList<DataType>();
    private final int capacity;
    private long checkpoint = 0L;

    public SyncedCacheBuffer(int capacity, DataType initialData) {
        this.capacity = capacity;
        for (int i = 0; i < capacity; i++) {
            buffer.add(initialData);
        }
    }

    public SyncedCacheBuffer(DataType initialData) {
        this(2, initialData);
    }

    public DataType get(int index) {
        return buffer.get(index);
    }

    public DataType get() {
        return buffer.get(0);
    }

    public List<DataType> getLatest(int num) {
        List<DataType> list = new ArrayList<>(num);
        for (int i = 0; i < num; i++){
            list.add(null);
        }
        long check;
        while (true) {
            check = checkpoint;
            if (checkAndGet(list, check)) {
                return list;
            }
        }
    }

    public List<DataType> getLatest() {
        return getLatest(2);
    }

    public void update(DataType updateValue){
        synchronized (buffer) {
            buffer.removeLast();
            buffer.addFirst(updateValue);
            checkpoint++;
        }
    }

    /**
     * MVCC 解决读写冲突
     * @param list
     * @param check
     * @return
     */
    private boolean checkAndGet(List<DataType> list, long check) {
        for (int i = 0; i < capacity; i++) {
            list.set(i, buffer.get(i));
            if (checkpoint != check) {
                return false;
            }
        }
        return true;
    }
}
