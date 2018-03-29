package com.yaoxiaowen.todo_learn.statistics;

import android.support.annotation.NonNull;

import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.data.source.TasksDataSource;
import com.yaoxiaowen.todo_learn.data.source.TasksRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * author：yaowen on 18/3/27 12:17
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class StatisticsPresenter implements StatisticsContract.Presenter {
    private final TasksRepository mTasksRepository;

    private final StatisticsContract.View mStatisticsView;

    public StatisticsPresenter(@NonNull TasksRepository tasksRepository,
                               @NonNull StatisticsContract.View statisticsView) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mStatisticsView = checkNotNull(statisticsView, "StatisticsView cannot be null!");

        mStatisticsView.setPresenter(this);
    }


    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics(){
        mStatisticsView.setProgressIndicator(true);

        //Todo 有一句关于自动化测试的类，没看懂，未来要补上的


        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                int activeTasks = 0;
                int completedTasks = 0;

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                //todo 这个自动化测试的类，始终没看懂
//                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
//                    EspressoIdlingResource.decrement(); // Set app as idle.
//                }

                // We calculate number of active and completed tasks
                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        completedTasks += 1;
                    } else {
                        activeTasks += 1;
                    }
                }
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.setProgressIndicator(false);

                mStatisticsView.showStatistics(activeTasks, completedTasks);
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.showLoadingStatisticsError();
            }
        });

    }
}
