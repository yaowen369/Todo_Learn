package com.yaoxiaowen.todo_learn;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yaoxiaowen.todo_learn.data.FakeTasksRemoteDataSource;
import com.yaoxiaowen.todo_learn.data.source.TasksRepository;
import com.yaoxiaowen.todo_learn.data.source.local.TasksLocalDataSource;
import com.yaoxiaowen.todo_learn.data.source.local.ToDoDatabase;
import com.yaoxiaowen.todo_learn.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * author：yaowen on 18/3/18 19:46
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class Injection {
    public static TasksRepository provideTasksRepository(@NonNull Context context){
        checkNotNull(context);
        ToDoDatabase database = ToDoDatabase.getInstance(context);
        return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(new AppExecutors(), database.taskDao()));
    }
}
