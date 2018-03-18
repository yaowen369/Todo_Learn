package com.yaoxiaowen.todo_learn.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.yaoxiaowen.todo_learn.data.Task;

/**
 * author：yaowen on 18/3/18 16:42
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class ToDoDatabase extends RoomDatabase{

    private static  ToDoDatabase ourInstance;

    public abstract TasksDao taskDao();


    /**
     * Todo 该类的写法没看懂，尤其是没搞明白为什么要锁个Object
     */
    private static final Object sLock = new Object();


    public static ToDoDatabase getInstance(Context context) {
        synchronized (sLock){
            if (ourInstance == null){
                ourInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ToDoDatabase.class, "Tasks.db")
                        .build();
            }
            return ourInstance;
        }
    }

}
