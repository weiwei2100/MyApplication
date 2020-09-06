package com.jason.myapp.utils;

import com.meiguan.ipsplayer.base.common.task.Task;

import java.util.Date;

/**
 * Created by qiuzi on 15/5/22.
 */
public class ScheduleUtil implements Runnable {

    private static final ScheduleUtil scheduleUtil = new ScheduleUtil();

    private ScheduleQueue queue = new ScheduleQueue();

    private     ScheduleUtil() {

    }

    public static ScheduleUtil getInstance() {

        return scheduleUtil;
    }

    /**
     * 初始化
     */
    public void init() {
        Thread thread = new Thread(ScheduleUtil.getInstance());
        thread.start();

    }

    /**
     * 添加任务
     *
     * @param taskName
     * @param interval
     */
    public void addTask(String taskName, int interval) {

        queue.push(taskName, interval);
    }

    private void execute() {

        String taskName = queue.pop(new Date());
        if (taskName != null) {
            Task task = TaskUtil.getInstance().getTask(taskName);
            ThreadPoolUtil.getInstance().execute(task);
        }

    }

    public synchronized void end(String taskName) {

        queue.end(taskName);
    }

    @Override
    public void run() {

        while (true) {
            this.execute();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
