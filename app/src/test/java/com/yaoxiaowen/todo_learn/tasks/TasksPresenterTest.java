package com.yaoxiaowen.todo_learn.tasks;

import com.google.common.collect.Lists;
import com.yaoxiaowen.todo_learn.data.Task;
import com.yaoxiaowen.todo_learn.data.source.TasksDataSource;
import com.yaoxiaowen.todo_learn.data.source.TasksRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

        verify(mTasksRepository).getTasks(mLoadTasksCallBackCaptor.capture());
        mLoadTasksCallBackCaptor.getValue().onTasksLoaded(TASKS);

        InOrder inOrder = Mockito.inOrder(mTasksView);
        inOrder.verify(mTasksView).setLoadingIndicator(true);
        inOrder.verify(mTasksView).setLoadingIndicator(false);

        ArgumentCaptor<List>  showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void loadActiveTasksFromRepositoryAndLoadIntoView(){
        mTasksPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
        mTasksPresenter.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mTasksRepository).getTasks(mLoadTasksCallBackCaptor.capture());
        mLoadTasksCallBackCaptor.getValue().onTasksLoaded(TASKS);

        // Then progress indicator is hidden and active tasks are shown in UI
        verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 1);
    }


    @Test
    public void loadCompletedTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksPresenter with initialized tasks
        // When loading of Tasks is requested
        mTasksPresenter.setFiltering(TasksFilterType.COMPLTED_TASK);
        mTasksPresenter.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mTasksRepository).getTasks(mLoadTasksCallBackCaptor.capture());
        mLoadTasksCallBackCaptor.getValue().onTasksLoaded(TASKS);

        // Then progress indicator is hidden and completed tasks are shown in UI
        verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 2);
    }



    @Test
    public void clickOnFab_ShowsAddTaskUi() {
        // When adding a new task
        mTasksPresenter.addNewTask();

        // Then add task UI is shown
        verify(mTasksView).showAddTask();
    }

    @Test
    public void clickOnTask_ShowsDetailUi() {
        // Given a stubbed active task
        Task requestedTask = new Task("Details Requested", "For this task");

        // When open task details is requested
        mTasksPresenter.openTaskDetails(requestedTask);

        // Then task detail UI is shown
        verify(mTasksView).showTaskDetailsUi(any(String.class));
    }

    @Test
    public void completeTask_ShowsTaskMarkedComplete() {
        // Given a stubbed task
        Task task = new Task("Details Requested", "For this task");

        // When task is marked as complete
        mTasksPresenter.completeTask(task);

        // Then repository is called and task marked complete UI is shown
        verify(mTasksRepository).completeTask(task);
        verify(mTasksView).showTaskMarkedComplete();
    }


    @Test
    public void activateTask_ShowsTaskMarkedActive() {
        // Given a stubbed completed task
        Task task = new Task("Details Requested", "For this task", true);
        mTasksPresenter.loadTasks(true);

        // When task is marked as activated
        mTasksPresenter.activateTask(task);

        // Then repository is called and task marked active UI is shown
        verify(mTasksRepository).activeTask(task);
        verify(mTasksView).showTaskMarkedActive();
    }

    @Test
    public void unavailableTasks_ShowsError() {
        // When tasks are loaded
        mTasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
        mTasksPresenter.loadTasks(true);

        // And the tasks aren't available in the repository
        verify(mTasksRepository).getTasks(mLoadTasksCallBackCaptor.capture());
        mLoadTasksCallBackCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        verify(mTasksView).showLoadingTasksError();
    }



}
