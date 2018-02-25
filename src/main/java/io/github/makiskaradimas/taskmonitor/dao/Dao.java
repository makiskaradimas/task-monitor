package io.github.makiskaradimas.taskmonitor.dao;

import io.github.makiskaradimas.taskmonitor.record.Record;

import java.util.Optional;

/**
 * Interface that specifies a basic set of DAO operations for a key-value store
 * where key is of type {@link String} and value of type {@link T},
 * implemented by {@link TaskRedisDao}.
 *
 * @author Makis Karadimas
 */
public interface Dao<T extends Record> {

    /**
     * Given the {@code key}, it retrieves the record from a key-value store.
     *
     * @param key the key to look for in the store. Must not be {@literal null}.
     * @return object found in the store or Optional.empty()
     */
    Optional<T> find(String key);

    /**
     * Create and insert a new key-value pair in the store only if the {@code key}
     * is not present.
     *
     * @param key   the key to be stored. Must not be {@literal null}.
     * @param value the value to be stored under key
     * @return true if the new key-value pair is stored successfully, false otherwise
     */
    boolean create(String key, T value);

    /**
     * Increment the value under {@code key} by {@code delta}.
     *
     * @param key   must not be {@literal null}.
     * @param delta the delta to be applied to the value under key
     * @return true if the delta is applied successfully, false otherwise
     */
    boolean increment(String key, T delta);
}
