package com.jason.myapp.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhiqi on 15/5/22.
 */
public class ScheduleQueue {

    private Map<String, Timer> taskTimerMap = new HashMap<String, Timer>();

    private Map<String, Timer> getTaskTimerMap() {
        return taskTimerMap;
    }

    private void setTaskTimerMap(Map<String, Timer> taskTimerMap) {
        this.taskTimerMap = taskTimerMap;
    }

    public synchronized void push(String taskName, int interval) {

        getTaskTimerMap().put(taskName, new Timer(interval));
    }

    public synchronized String pop(Date date) {

        for (Map.Entry<String, Timer> it : taskTimerMap.entrySet()) {
            Timer timer = it.getValue();
            if (timer.canRun(date)) {
                timer.setExecute(date);
                return it.getKey();
            }
        }
        return null;
    }

    public synchronized void end(String taskName) {

        Timer timer = taskTimerMap.get(taskName);
        timer.end();
    }

    class Timer {

        private int interval;

        private Date executeDate;

        private boolean isRun;

        public Timer(int interval) {

            setInterval(interval);

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, this.interval);
            setExecuteDate(calendar.getTime());

            setIsRun(false);
        }

        public void end() {

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, this.interval);
            setExecuteDate(calendar.getTime());

            setIsRun(false);
        }

        public void setExecute(Date date) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, this.interval);
            setExecuteDate(calendar.getTime());

            setIsRun(true);
        }

        public boolean canRun(Date date) {

            if (!isRun && date.after(getExecuteDate())) {
                return true;
            }

            return false;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public Date getExecuteDate() {
            return executeDate;
        }

        public void setExecuteDate(Date executeDate) {
            this.executeDate = executeDate;
        }

        public boolean isRun() {
            return isRun;
        }

        public void setIsRun(boolean isRun) {
            this.isRun = isRun;
        }
    }
}
