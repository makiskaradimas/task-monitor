package io.github.makiskaradimas.taskmonitor.dao;

import io.github.makiskaradimas.taskmonitor.record.TaskRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRedisDao implements Dao<TaskRecord> {

    private final RedisOperations<String, TaskRecord> redisOperations;

    @Value("${redis.operations.retries}")
    private int retries;

    @Autowired
    public TaskRedisDao(RedisOperations<String, TaskRecord> redisOperations) {
        this.redisOperations = redisOperations;
    }

    @Override
    public Optional<TaskRecord> find(String id) {
        if (this.redisOperations.hasKey(id)) {
            return Optional.of(this.redisOperations.opsForValue().get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean create(String id, TaskRecord taskRecord) {
        return this.redisOperations.opsForValue().setIfAbsent(id, taskRecord);
    }

    @Override
    public boolean increment(String id, TaskRecord delta) {
        SessionCallback<Boolean> callback = createCallback(id, delta);
        return this.redisOperations.execute(callback);
    }

    public int getRetries() {
        return retries;
    }

    @SuppressWarnings("unchecked")
    SessionCallback<Boolean> createCallback(String id, TaskRecord delta) {
        return new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {
                int retryLimit = retries;
                long delay = 10;
                Optional<List<Object>> results;
                int i = 0;
                do {
                    // start watching key for modifications
                    operations.watch((K) id);

                    // retrieve value for id
                    TaskRecord retrieved = (TaskRecord) operations.opsForValue().get(delta.getId());

                    // increment executions and add milliseconds
                    retrieved.increment(delta);

                    // add statements to be queued and then executed
                    operations.multi();
                    operations.opsForValue().getAndSet((K) id, (V) retrieved);

                    // execute and get results (the output of GETSET must be in the results)
                    // will fail if the watched key has been modified (optimistic locking)
                    results = Optional.of(operations.exec());

                    // in case of failure wait and retry
                    if (results.get().size() == 0) {
                        try {
                            Thread.sleep(delay << i);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } while (results.get().size() == 0 && i++ < retryLimit);

                return (results.get().size() > 0);
            }
        };
    }
}
