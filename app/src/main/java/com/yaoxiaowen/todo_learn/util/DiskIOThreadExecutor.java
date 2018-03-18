package com.yaoxiaowen.todo_learn.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * author：yaowen on 18/3/18 16:05
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 */

public class DiskIOThreadExecutor implements Executor{

    private final Executor mDiskIO;

    public DiskIOThreadExecutor() {
        this.mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIO.execute(command);
    }
}
