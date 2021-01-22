package gui;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: gq
 * @Date: 2018/12/26 13:21
 */
public class SyncedSwapBuffer<DataType> {

    private final List<DataType> buffer = new ArrayList<>(2);
    private int readBufferIndex = 0;
    private boolean dirty = false;

    public SyncedSwapBuffer(DataType initialValue) {
        buffer.add(initialValue);
        buffer.add(initialValue);
    }

    public DataType get() {
        return buffer.get(readBufferIndex);
    }

    public void update(DataType updateValue){
        synchronized (buffer) {
            buffer.set(1 - readBufferIndex, updateValue);
            dirty = true;
        }
    }

    public void swap() {
        synchronized (buffer) {
            if (dirty) {
                readBufferIndex = 1 - readBufferIndex;
                dirty = false;
            }
        }
    }


}
