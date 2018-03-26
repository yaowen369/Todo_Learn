package com.yaoxiaowen.todo_learn.tasks;

import android.support.annotation.NonNull;

import com.yaoxiaowen.todo_learn.BaseView;
import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.util.BasePresenter;

import java.util.List;

/**
 * author：yaowen on 18/3/26 12:50
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class TasksContract {

    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter{
        void result(int requestCode, int resultCode);
        void loadTasks(boolean forceUpdate);
        void addNewTask();
        void openTaskDetails(@NonNull Task requestTask);

        void completeTask(@NonNull Task completedTask);

        void activateTask(@NonNull Task activeTask);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();
    }
}
