package com.example.demo.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Service
public class TaskManager {
    private static final Map<String, Future<?>> asyncTasks = new HashMap<>();

    public boolean isTaskRunning(String taskName) {
        return asyncTasks.containsKey(taskName) &&
                !asyncTasks.get(taskName).isDone() &&
                !asyncTasks.get(taskName).isCancelled();
    }

    public void startTask(String taskName, Future<?> taskExecution) {
        if (isTaskRunning(taskName)) {
            log.info(taskName + " is already running");

            return;
        }

        asyncTasks.remove(taskName);
        asyncTasks.put(taskName, taskExecution);
    }

    public void cancelTask(String taskName) {
        if (!isTaskRunning(taskName)) {
            log.info(taskName + " is not running");

            return;
        }

        log.info("Interrupting task " + taskName);
        asyncTasks.get(taskName).cancel(true);
    }
}
