package io.github.makiskaradimas.taskmonitor.record;

public class TaskRecord extends Record implements Incrementable<TaskRecord> {
    private long executions;
    private long totalMilliseconds;

    public TaskRecord() {
    }

    public TaskRecord(String id, long executions, long totalMilliseconds) {
        super(id);
        this.executions = executions;
        this.totalMilliseconds = totalMilliseconds;
    }

    public void increment(TaskRecord delta) {
        this.executions += delta.getExecutions();
        this.totalMilliseconds += delta.getTotalMilliseconds();
    }

    public long getExecutions() {
        return executions;
    }

    public long getTotalMilliseconds() {
        return totalMilliseconds;
    }
}
