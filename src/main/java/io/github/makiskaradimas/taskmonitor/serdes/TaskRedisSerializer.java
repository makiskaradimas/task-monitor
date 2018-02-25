package io.github.makiskaradimas.taskmonitor.serdes;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.makiskaradimas.taskmonitor.record.TaskRecord;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class TaskRedisSerializer implements RedisSerializer<TaskRecord> {

    private final ObjectMapper om;

    public TaskRedisSerializer() {
        this.om = new ObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public byte[] serialize(TaskRecord t) throws SerializationException {
        try {
            return om.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public TaskRecord deserialize(byte[] bytes) throws SerializationException {

        if (bytes == null) {
            return null;
        }

        try {
            return om.readValue(bytes, TaskRecord.class);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}