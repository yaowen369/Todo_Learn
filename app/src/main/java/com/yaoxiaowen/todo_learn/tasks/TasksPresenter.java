package com.yaoxiaowen.todo_learn.tasks;

import android.support.annotation.NonNull;

import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.data.source.TasksDataSource;
import com.yaoxiaowen.todo_learn.data.source.TasksRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * author：yaowen on 18/3/26 21:34
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class TasksPresenter implements TasksContract.Presenter{

    private final TasksRepository mTasksRepository;
    private final TasksContract.View mTasksView;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;
    private boolean mFirstLoad = true;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        this.mTasksRepository = checkNotNull(tasksRepository);
        this.mTasksView = checkNotNull(tasksView);

        mTasksView.setPresenter(this);
    }


    @Override
    public void start() {
        loadTasks(false);
    }


    @Override
    public void result(int requestCode, int resultCode) {
        //todo 这里还没有实现
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI){
        if (showLoadingUI){
            mTasksView.setLoadingIndicator(true);
        }

        if (forceUpdate){
            mTasksRepository.refreshTasks();
        }

        //Todo 这里有一句关于自动化测试的语句，但是暂时还没看懂
//        EspressoIdlingResource.increment();


        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<>();

                //Todo  这里有个自动化测试的句子
                for (Task task : tasks){
                    switch (mCurrentFiltering){
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()){
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLTED_TASK:
                            if (task.isCompleted()){
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }

                if (!mTasksView.isActive()){
                    return;
                }
                if (showLoadingUI){
                    mTasksView.setLoadingIndicator(false);
                }

                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mTasksView.isActive()){
                    return;
                }

                mTasksView.showLoadingTasksError();
            }
        });
    }


    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mTasksView.showTasks(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLTED_TASK:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLTED_TASK:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestTask) {
        checkNotNull(requestTask, "requestTask cannot be null");
        mTasksView.showTaskDetailsUi(requestTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");
        mTasksRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks(false, false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null!");
        mTasksRepository.activeTask(activeTask);
        mTasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }


    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();
        mTasksView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

}
