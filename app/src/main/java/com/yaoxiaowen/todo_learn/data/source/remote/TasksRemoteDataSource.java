package com.yaoxiaowen.todo_learn.data.source.remote;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.data.source.TasksDataSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * author：yaowen on 18/3/17 18:51
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class TasksRemoteDataSource implements TasksDataSource{
    private static final TasksRemoteDataSource ourInstance = new TasksRemoteDataSource();

    public static TasksRemoteDataSource getInstance() {
        return ourInstance;
    }

    private TasksRemoteDataSource() {
    }


    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERICE_DATA;

    static {
        TASKS_SERICE_DATA = new LinkedHashMap<>(2);
        addTask("北京建塔", "我们要在伟大的帝都开发建设");
        addTask("驻马店铺路修桥", "杀人放火金腰带，修桥铺路无尸骸");
    }

    private static void addTask(String title, String description){
        Task newTask = new Task(title, description);
        TASKS_SERICE_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public void getTasks(final @NonNull LoadTasksCallBack callback) {
        //Todo 为什么没有looper

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTasksLoaded(Lists.newArrayList(TASKS_SERICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetTaskCallBack callBack) {
        final Task task = TASKS_SERICE_DATA.get(taskId);

        Handler  handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callBack.onTaskLoaded(task);
            }
        }, SERVICE_LATENCY_IN_MILLIS);

    }

    @Override
    public void saveTask(@NonNull Task task){
        TASKS_SERICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        //这里不用实现， 因为 {@link TaskRepository} 将taskId转化为了 task。换句话说，重载了Task
    }

    @Override
    public void activeTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASKS_SERICE_DATA.put(task.getId(), activeTask);
    }

    @Override
    public void activeTask(@NonNull String taskId) {
        //不用实现，在 {@link TaskRepository} 中，进行了重载，将taskid转化为了对应的Task
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERICE_DATA.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()){
                it.remove();
            }
        }
    }


    @Override
    public void refreshTasks() {
        //{@link TasksRepository} 中已经做了相应的处理了
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERICE_DATA.remove(taskId);
    }
}
