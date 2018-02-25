package io.github.makiskaradimas.taskmonitor.web;

import io.github.makiskaradimas.taskmonitor.dao.TaskRedisDao;
import io.github.makiskaradimas.taskmonitor.record.TaskRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class TaskController {

    private TaskRedisDao taskRedisDAO;

    @Autowired
    public TaskController(TaskRedisDao taskRedisDAO) {
        this.taskRedisDAO = taskRedisDAO;
    }

    @PostMapping(value = "tasks")
    public ResponseEntity taskPerformed(@RequestBody Task task) {
        Optional<TaskRecord> taskRecord = taskRedisDAO.find(task.getId());

        if (!taskRecord.isPresent()) {
            taskRecord = Optional.of(new TaskRecord(task.getId(), 1L, task.getMilliseconds()));
            boolean created = taskRedisDAO.create(task.getId(), taskRecord.get());
            if (created) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }

        TaskRecord delta = new TaskRecord(task.getId(), 1L, task.getMilliseconds());
        boolean incremented = taskRedisDAO.increment(task.getId(), delta);
        if (incremented) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        String message = String.format("Modification of value in Redis failed after %d retries", taskRedisDAO.getRetries());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "tasks/{id}/duration")
    public ResponseEntity getAverageTime(@PathVariable("id") String id) {
        Optional<TaskRecord> taskRecord = taskRedisDAO.find(id);
        if (taskRecord.isPresent()) {
            double averageMiliseconds = taskRecord.get().getTotalMilliseconds() / taskRecord.get().getExecutions();
            Response response = new Response();
            response.setId(id);
            response.setAverageDuration(averageMiliseconds);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        String message = String.format("Task with id %s not found", id);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}

class Task {
    private String id;
    private long milliseconds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}

class Response {
    private String id;
    private double averageDuration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }
}

