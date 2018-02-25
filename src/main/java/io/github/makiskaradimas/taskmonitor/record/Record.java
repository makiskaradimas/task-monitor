package io.github.makiskaradimas.taskmonitor.record;

public abstract class Record {
    private String id;

    public Record() {
    }

    public Record(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
