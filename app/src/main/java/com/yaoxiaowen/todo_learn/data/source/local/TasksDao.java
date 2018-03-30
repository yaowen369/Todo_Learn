package com.yaoxiaowen.todo_learn.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yaoxiaowen.todo_learn.data.Task;

import java.util.List;

/**
 * author：yaowen on 18/3/18 16:20
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

@Dao
public interface TasksDao {

    @Query("SELECT * FROM tasks")
    List<Task> getTasks();

    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    Task getTaskById(String taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    /**
     * 更新一个Task
     * @param task
     * @return 更新的Task的数量，该值应该永远为1
     */
    @Update
    int updateTask(Task task);


    /**
     * 更新某个Task的完成状态
     * @param taskId
     * @param completed
     */
    @Query("UPDATE tasks SET completed = :completed WHERE entryid = :taskId")
    void updateCompleted(String taskId, boolean completed);

    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
    int deleteTaskById(String taskId);

    @Query("DELETE FROM tasks")
    void deleteTasks();

    /**
     * 从表中删除所有完成状态的tasks
     * @return  删除的tasks的数量
     */
    @Query("DELETE FROM Tasks WHERE completed = 1")
    int deleteCompletedTasks();
}
