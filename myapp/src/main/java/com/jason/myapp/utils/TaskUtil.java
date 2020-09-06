package com.jason.myapp.utils;

import com.meiguan.ipsplayer.base.common.task.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiuzi on 15/5/22.
 */
public class TaskUtil {

    private static TaskUtil taskUtil = new TaskUtil();

    private Map<String, Task> taskMap = new HashMap<String, Task>();

    private TaskUtil() {
    }

    public static TaskUtil getInstance() {
        return taskUtil;
    }

    public void register(Task task, int interval) {
        taskMap.put(task.getTaskName(), task);
        ScheduleUtil.getInstance().addTask(task.getTaskName(), interval);
    }

    public Task getTask(String taskName) {
        return taskMap.get(taskName);
    }
}
