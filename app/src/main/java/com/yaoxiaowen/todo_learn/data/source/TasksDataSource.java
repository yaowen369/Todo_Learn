package com.yaoxiaowen.todo_learn.data.source;

import android.support.annotation.NonNull;

import com.yaoxiaowen.todo_learn.data.Task;

import java.util.List;

/**
 * author：yaowen on 18/3/17 18:33
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public interface TasksDataSource {

    //Todo 这两个接口的作用，还暂时不知道
    interface LoadTasksCallBack{
        void onTasksLoaded(List<Task> tasks);
        void onDataNotAvailable();
    }

    interface GetTaskCallBack{
        void onTaskLoaded(Task task);
        void onDataNotAvailable();
    }

    void getTasks(@NonNull LoadTasksCallBack callback);

    void getTask(@NonNull String taskId, @NonNull GetTaskCallBack callBack);

    void saveTask(@NonNull Task task);

    void completeTask(@NonNull Task task);

    void completeTask(@NonNull String taskId);

    void activeTask(@NonNull Task task);

    void activeTask(@NonNull String taskId);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);
}
