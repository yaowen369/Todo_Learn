package com.yaoxiaowen.todo_learn.taskdetail;

import com.yaoxiaowen.todo_learn.BaseView;
import com.yaoxiaowen.todo_learn.util.BasePresenter;

/**
 * author：yaowen on 18/3/30 09:35
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public interface TaskDetailContract {

    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);

        void showMissingTask();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showCompletionStatus(boolean complete);

        void showEditTask(String taskId);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();
    }


    interface Presenter extends BasePresenter{

        void editTask();

        void deleteTask();

        void completeTask();

        void activateTask();
    }

}
