package com.yaoxiaowen.todo_learn.data.source.local;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.data.source.TasksDataSource;
import com.yaoxiaowen.todo_learn.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * author：yaowen on 18/3/17 18:50
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class TasksLocalDataSource implements TasksDataSource{

    private static volatile TasksLocalDataSource INSTANCE;

    private TasksDao mTasksDao;

    private AppExecutors mAppExecutors;


    private TasksLocalDataSource(@NonNull AppExecutors mAppExecutors,
                                 @NonNull TasksDao mTasksDao) {
        this.mTasksDao = mTasksDao;
        this.mAppExecutors = mAppExecutors;
    }

    public static TasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TasksDao tasksDao){
        if (INSTANCE == null){
            synchronized (TasksLocalDataSource.class){
                if (INSTANCE == null){
                    INSTANCE = new TasksLocalDataSource(appExecutors, tasksDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = mTasksDao.getTasks();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tasks.isEmpty()){
                            callback.onDataNotAvailable();
                        }else {
                            callback.onTasksLoaded(tasks);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = mTasksDao.getTaskById(taskId);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task == null){
                            callBack.onDataNotAvailable();
                        }else {
                            callBack.onTaskLoaded(task);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveTask(@NonNull final Task task) {
        checkNotNull(task);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.insertTask(task);
            }
        };

        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void completeTask(@NonNull final Task task) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.updateCompleted(task.getId(), true);
            }
        };

        mAppExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        //无需实现，在 TaskRepository 中根据taskid，转化为了对应的Task来实现。
    }

    @Override
    public void activeTask(@NonNull final Task task) {
        Runnable actiateRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.updateCompleted(task.getId(), false);
            }
        };

        mAppExecutors.diskIO().execute(actiateRunnable);
    }

    @Override
    public void activeTask(@NonNull String taskId) {
        //无需实现，在 TaskRepository 中根据taskid，转化为了对应的Task来实现。
    }

    @Override
    public void clearCompletedTasks() {
        Runnable clearTasksRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.deleteCompletedTasks();
            }
        };

        mAppExecutors.diskIO().execute(clearTasksRunnable);
    }

    @Override
    public void refreshTasks() {
        // TasksRepository 中 实现了对应的逻辑
    }

    @Override
    public void deleteAllTasks() {
        Runnable deleteAllRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.deleteTasks();
            }
        };

        mAppExecutors.diskIO().execute(deleteAllRunnable);
    }

    @Override
    public void deleteTask(@NonNull final String taskId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.deleteTaskById(taskId);
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }


    @VisibleForTesting
    static void clearInstance(){
        INSTANCE = null;
    }
}
