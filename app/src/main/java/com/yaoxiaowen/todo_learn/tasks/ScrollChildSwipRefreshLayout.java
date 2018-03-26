package com.yaoxiaowen.todo_learn.tasks;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * author：yaowen on 18/3/26 12:31
 * email：yaoxiaowen88@gmail.com
 * www.yaoxiaowen.com
 *
 *  SwipeRefreshLayout 默认只能有一个child，这里尽心扩展，支持 不能直接刷新的View。
 */

public class ScrollChildSwipRefreshLayout extends SwipeRefreshLayout{

    private View mScrollUpchild;

    public ScrollChildSwipRefreshLayout(Context context) {
        super(context);
    }

    public ScrollChildSwipRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpchild != null){
            return ViewCompat.canScrollVertically(mScrollUpchild, -1);
        }

        return super.canChildScrollUp();
    }


    public void setScrollUpChild(View view){
        mScrollUpchild = view;
    }
}
