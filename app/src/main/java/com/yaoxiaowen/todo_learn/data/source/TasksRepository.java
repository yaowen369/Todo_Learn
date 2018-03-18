package com.yaoxiaowen.todo_learn.data.source;

/**
 * author：yaowen on 18/3/17 19:36
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yaoxiaowen.todo_learn.data.Task;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 根据官网demo的注释，本仓库类 在 localDb和RemoteServer之间进行同步，如果LocalDb不存在，则使用RemoteServer。
 * 但是他们之间又是怎么同步的呢。
 */
public class TasksRepository implements TasksDataSource{
    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     * Todo 官方Demo说，包访问权限可供测试，这些究竟是什么意思
     */
    Map<String, Task> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     * Todo 官方Demo说，包访问权限 可供测试，什么意思
     */
    boolean mCacheIsDirty = false;

    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource,
                                              TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destoryInstance(){
        INSTANCE = null;
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallBack callback) {
        checkNotNull(callback);

        if (mCachedTasks!=null && !mCacheIsDirty){
            callback.onTasksLoaded(new ArrayList<Task>(mCachedTasks.values()));
            return;
        }

        if (mCacheIsDirty){
            getTasksFromRemoteDataSource(callback);
        }else {
            mTasksLocalDataSource.getTasks(new LoadTasksCallBack() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(tasks);
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }// getTasks(...


    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        Task compltedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.put(task.getId(), compltedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }


    @Override
    public void activeTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activeTask(task);
        mTasksLocalDataSource.activeTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.put(task.getId(), activeTask);
    }

    @Override
    public void activeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activeTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();

        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }

        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();

        while (it.hasNext()){
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()){
                it.remove();
            }
        }
    }


    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallBack callBack) {
        checkNotNull(taskId);
        checkNotNull(callBack);

        final Task cachedTask = getTaskWithId(taskId);
        if (cachedTask != null){
            callBack.onTaskLoaded(cachedTask);
            return;
        }

        mTasksLocalDataSource.getTask(taskId, new GetTaskCallBack() {
            @Override
            public void onTaskLoaded(Task task) {
                createCacheTasksIfNull();
                mCachedTasks.put(task.getId(), task);
                callBack.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallBack() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        createCacheTasksIfNull();
                        mCachedTasks.put(task.getId(), task);
                        callBack.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callBack.onDataNotAvailable();
                    }
                });
            }
        });
    }// end of "getTask(..."

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();
        
        createCacheTasksIfNull();
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksLocalDataSource.deleteTask(taskId);
        mTasksRemoteDataSource.deleteTask(taskId);

        //Todo 为什么不判空，因为业务逻辑吗？
        mCachedTasks.remove(taskId);
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallBack callback){
        mTasksRemoteDataSource.getTasks(new LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<Task>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks){
        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }

        mCachedTasks.clear();

        for (Task task : tasks){
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks){
        mTasksLocalDataSource.deleteAllTasks();
        for (Task task : tasks){
            mTasksLocalDataSource.saveTask(task);
        }
    }

    @Nullable
    private Task getTaskWithId(@NonNull String id){
        checkNotNull(id);
        if (mCachedTasks==null || mCachedTasks.isEmpty()){
            return null;
        }else {
            return mCachedTasks.get(id);
        }
    }

    private void createCacheTasksIfNull(){
        if (mCachedTasks == null){
            mCachedTasks = new LinkedHashMap<>();
        }
    }
}
