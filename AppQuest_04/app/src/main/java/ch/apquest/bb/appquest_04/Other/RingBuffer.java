package ch.apquest.bb.appquest_04.Other;

import java.util.ArrayList;
import java.util.List;

public class RingBuffer {

    //region Fields

    private float[] buffer;
    private int capacity;
    private int current = 0;
    private int count = 0;

    //endregion

    //region Constructor

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        buffer = new float[capacity];
    }

    //endregion

    //region General Methods

    public int getCount() {
        return count;
    }

    public void put(float f) {
        buffer[current] = f;
        current++;
        count++;
        current = current % capacity;
    }

    public float getAverage() {
        float avg = 0;
        for (float f : buffer) {
            avg += f;
        }
        if (count > capacity) {
            return avg / capacity;
        } else {
            return avg / count;
        }
    }

    public List<Float> getValues() {
        List<Float> values = new ArrayList<>();

        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0f) {
                values.add(buffer[i]);
            }
        }

        return values;
    }

    //endregion

}