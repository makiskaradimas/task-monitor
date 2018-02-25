package io.github.makiskaradimas.taskmonitor.record;

public interface Incrementable<T> {
    void increment(T delta);
}
