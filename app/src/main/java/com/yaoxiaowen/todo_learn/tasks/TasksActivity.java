package com.yaoxiaowen.todo_learn.tasks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.yaoxiaowen.todo_learn.Injection;
import com.yaoxiaowen.todo_learn.R;
import com.yaoxiaowen.todo_learn.util.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    private static final String CURRENT_FILTTERING_KEY = "current_filttering_key";

    private DrawerLayout mDrawerLayout;

    private TasksPresenter mTasksPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);


        //set up the toolbar
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //set up the navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null){
            setupDrawerContent(navigationView);
        }

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (tasksFragment == null){
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        mTasksPresenter = new TasksPresenter(
                Injection.provideTasksRepository(getApplicationContext()), tasksFragment);

        if (savedInstanceState != null){
            TasksFilterType currentFiltering =
                    (TasksFilterType)savedInstanceState.getSerializable(CURRENT_FILTTERING_KEY);
            mTasksPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTTERING_KEY, mTasksPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                break;
                            case R.id.statistics_navigation_menu_item:
                                //Todo 静态统计页面还没有完善
//                                Intent intent =
//                                        new Intent(TasksActivity.this, StatisticsActivity.class);
//                                startActivity(intent);
                                break;
                                default:
                                    break;
                        }
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }


    //Todo 下面还有一个类 for test，但是目前还不懂

}
