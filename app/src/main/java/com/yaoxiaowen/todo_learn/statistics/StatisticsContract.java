package com.yaoxiaowen.todo_learn.statistics;

import android.widget.BaseAdapter;

import com.yaoxiaowen.todo_learn.BaseView;
import com.yaoxiaowen.todo_learn.util.BasePresenter;

/**
 * author：yaowen on 18/3/27 12:14
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public interface StatisticsContract {

    interface View extends BaseView<Presenter>{
        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter{

    }
}
