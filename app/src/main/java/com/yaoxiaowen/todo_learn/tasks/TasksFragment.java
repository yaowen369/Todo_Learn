package com.yaoxiaowen.todo_learn.tasks;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaoxiaowen.todo_learn.R;
import com.yaoxiaowen.todo_learn.data.Task;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class TasksFragment extends Fragment {

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tasks_frag, container, false);
    }


    TaskItemListener mItemListener = new TaskItemListener() {
        //Todo 这个东西 需要更进一步的 完善
        @Override
        public void onTaskCilck(Task clickedTask) {

        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {

        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {

        }
    };


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


        //Todo 这个adapter的下面几个方法，还没有具体实现呢。
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }


    public interface TaskItemListener{
        void onTaskCilck(Task clickedTask);
        void onCompleteTaskClick(Task completedTask);
        void onActivateTaskClick(Task activatedTask);
    }


}
