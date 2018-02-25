package io.github.makiskaradimas;

import io.github.makiskaradimas.taskmonitor.record.TaskRecord;
import io.github.makiskaradimas.taskmonitor.serdes.TaskRedisSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class Application {

    @Bean
    @Primary
    RedisTemplate<String, TaskRecord> redisTemplate(RedisConnectionFactory rcf) {

        RedisTemplate<String, TaskRecord> template = new RedisTemplate<>();
        template.setConnectionFactory(rcf);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new TaskRedisSerializer());

        return template;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
