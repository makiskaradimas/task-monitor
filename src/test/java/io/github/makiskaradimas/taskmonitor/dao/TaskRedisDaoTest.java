package io.github.makiskaradimas.taskmonitor.dao;

import io.github.makiskaradimas.taskmonitor.record.TaskRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TaskRedisDaoTest {

    private static final TaskRecord testRecord1 = new TaskRecord("test1", 1L, 1000L);

    @MockBean
    private RedisTemplate<String, TaskRecord> redisTemplate;
    @MockBean
    private ValueOperations<String, TaskRecord> valueOperations;
    @Autowired
    private TaskRedisDao taskRedisDao;

    @Test
    public void findNotExisting() {
        Optional<TaskRecord> retrieved = taskRedisDao.find("test1");
        Mockito.verify(this.redisTemplate, Mockito.times(1)).hasKey("test1");
        Assert.assertEquals(Optional.empty(), retrieved);
    }

    @Test
    public void findExisting() {
        Mockito.when(this.redisTemplate.hasKey("test1")).thenReturn(true);
        Mockito.when(this.valueOperations.get("test1")).thenReturn(testRecord1);
        Mockito.when(this.redisTemplate.opsForValue()).thenReturn(this.valueOperations);

        Optional<TaskRecord> retrieved = taskRedisDao.find("test1");
        Mockito.verify(this.redisTemplate.opsForValue(), Mockito.times(1)).get("test1");
        Assert.assertEquals(testRecord1.getId(), retrieved.get().getId());
        Assert.assertEquals(testRecord1.getExecutions(), retrieved.get().getExecutions());
        Assert.assertEquals(testRecord1.getTotalMilliseconds(), retrieved.get().getTotalMilliseconds());
    }

    @Test
    public void create() {
        Mockito.when(this.redisTemplate.opsForValue()).thenReturn(this.valueOperations);

        taskRedisDao.create("test1", testRecord1);
        Mockito.verify(this.redisTemplate.opsForValue(), Mockito.times(1)).setIfAbsent("test1", testRecord1);
    }

    @Test(expected = MissingMethodInvocationException.class)
    public void incrementExecutesCallback() {
        Mockito.when(taskRedisDao.createCallback("test1", testRecord1)).thenThrow(new MissingMethodInvocationException("test1"));

        taskRedisDao.increment("test1", testRecord1);
    }
}
