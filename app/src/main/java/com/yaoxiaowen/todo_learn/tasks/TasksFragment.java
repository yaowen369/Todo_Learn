package com.yaoxiaowen.todo_learn.tasks;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.yaoxiaowen.todo_learn.R;
import com.yaoxiaowen.todo_learn.data.Task;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.AEADBadTagException;

import static com.google.common.base.Preconditions.checkNotNull;


public class TasksFragment extends Fragment implements TasksContract.View{

    private TasksContract.Presenter mPresenter;
    private TasksAdapter mListAdapter;
    private View mNoTasksView;
    private ImageView mNoTaskIcon;
    private TextView mNoTaskMainView;
    private TextView mNoTaskAddView;
    private LinearLayout mTasksView;
    private TextView mFilteringLabelView;



    public TasksFragment() {
        // Required empty public constructor
    }


    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TasksAdapter(new ArrayList<Task>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.tasks_frag, container, false);

        // Set up tasks view
        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mTasksView = (LinearLayout) root.findViewById(R.id.tasksLL);

        // Set up  no tasks view
        mNoTasksView = root.findViewById(R.id.noTasks);
        mNoTaskIcon = (ImageView) root.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = (TextView) root.findViewById(R.id.noTasksMain);
        mNoTaskAddView = (TextView) root.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo 这个屏蔽语句要去掉，应该还要添加 数据
//                showAddTask();
            }
        });

        //set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton)getActivity().findViewById(R.id.fab_add_task);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mPresenter.addNewTask();
            }
        });

       //set p progress indicator
        final ScrollChildSwipRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );


        //set the scrolling view in the custom SwipRefreshLayout
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTasks(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear:
                mPresenter.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }


    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.active:
                        mPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltering(TasksFilterType.COMPLTED_TASK);
                        break;
                    default:
                        mPresenter.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                mPresenter.loadTasks(false);
                return true;
            }
        });
        popup.show();
    }

    TaskItemListener mItemListener = new TaskItemListener() {
        @Override
        public void onTaskCilck(Task clickedTask) {
            mPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            mPresenter.activateTask(activatedTask);
        }
    };

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null){
            return;
        }

        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }


    @Override
    public void showNoActiveTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false
        );
    }


    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showAddTask() {
//        AddEditTaskActivity 还没有写，Todo 待实现之后，这里要放开屏蔽

//        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
//        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }


    @Override
    public void showTaskDetailsUi(String taskId) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        //Todo 等待完成之后，这块要放开屏蔽
//        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
//        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
//        startActivity(intent);
    }


    @Override
    public void showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete));
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active));
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared));
    }

    @Override
    public void showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    private void showMessage(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    private static class TasksAdapter extends BaseAdapter{

        private List<Task> mTasks;
        private TaskItemListener mItemListener;


        public TasksAdapter(List<Task> tasks, TaskItemListener mItemListener) {
            this.mTasks = tasks;
            this.mItemListener = mItemListener;
        }

        public void replaceData(List<Task> tasks){
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> tasks){
            mTasks = checkNotNull(tasks);
        }


        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.task_item, parent, false);
            }

            final Task task = getItem(position);
            TextView titleTv = rowView.findViewById(R.id.title);
            titleTv.setText(task.getTitleForList());

            CheckBox completeCB = rowView.findViewById(R.id.complete);
            completeCB.setChecked(task.isCompleted());
            if (task.isCompleted()){
                rowView.setBackgroundDrawable(parent.getContext()
                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            }else {
                rowView.setBackgroundDrawable(parent.getContext()
                        .getResources().getDrawable(R.drawable.touch_feedback));
            }

            completeCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!task.isCompleted()){
                        mItemListener.onCompleteTaskClick(task);
                    }else {
                        mItemListener.onActivateTaskClick(task);
                    }
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onTaskCilck(task);
                }
            });


            return rowView;
        }
    }


    public interface TaskItemListener{
        void onTaskCilck(Task clickedTask);
        void onCompleteTaskClick(Task completedTask);
        void onActivateTaskClick(Task activatedTask);
    }


}
