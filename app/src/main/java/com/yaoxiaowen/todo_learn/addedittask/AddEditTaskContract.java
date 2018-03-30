package com.yaoxiaowen.todo_learn.addedittask;

import com.yaoxiaowen.todo_learn.BaseView;
import com.yaoxiaowen.todo_learn.util.BasePresenter;

/**
 * author：yaowen on 18/3/30 15:48
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter>{
        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter{
        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();
    }
}
