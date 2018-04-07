package com.yaoxiaowen.todo_learn.tasks;

import com.google.common.collect.Lists;
import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.data.source.TasksDataSource;
import com.yaoxiaowen.todo_learn.data.source.TasksRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * author：yaowen on 18/4/7 17:45
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 *
 * 学习基本的测试case
 */

public class TasksPresenterTest {

    private static List<Task> TASKS;

    @Mock
    private TasksRepository mTasksRepository;

    @Mock
    private TasksContract.View mTasksView;

    @Captor
    private ArgumentCaptor<TasksDataSource.LoadTasksCallBack> mLoadTasksCallBackCaptor;


    private TasksPresenter mTasksPresenter;


    @Before
    public void setUpTaskPresenter(){
        MockitoAnnotations.initMocks(this);

        mTasksPresenter = new TasksPresenter(mTasksRepository, mTasksView);

        when(mTasksView.isActive()).thenReturn(true);

        TASKS = Lists.newArrayList(new Task("Title1", "Description1"),
                new Task("Title2", "Description2", true),
                new Task("Title3", "Description3", true));

    }

    @Test
    public void createPresenter_setsThePresenterToView(){
        mTasksPresenter = new TasksPresenter(mTasksRepository, mTasksView);

        verify(mTasksView).setPresenter(mTasksPresenter);
    }

    @Test
    public void loadAllTasksFromRepositoryAndLoadIntoView(){
        mTasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
        mTasksPresenter.loadTasks(true);

        verify(mTasksRepository).getTasks(mLoadTasksCallBackCaptor.capture());

        //Todo 剩下的东西太复杂了
    }
}
