/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.idx.calendarview;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class MonthRecyclerView extends RecyclerView {
    private static final String TAG = "MonthRecyclerView";
    private CustomCalendarViewDelegate mDelegate;
    private MonthAdapter mAdapter;
    private OnMonthSelectedListener mListener;
    private String[] montList;

    public MonthRecyclerView(Context context) {
        this(context, null);
    }

    public MonthRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mAdapter = new MonthAdapter(context);
        montList = context.getResources().getStringArray(R.array.month_list);
        // 判断Android当前的屏幕是横屏还是竖屏。横竖屏判断
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            setLayoutManager(new GridLayoutManager(context, 3));
        } else {
            //横屏
            setLayoutManager(new GridLayoutManager(context, 4));
        }

        setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                Log.d(TAG, "onItemClick11: ");
                if (mListener != null && mDelegate != null) {
                    Log.d(TAG, "onItemClick22: ");
                    Month month = mAdapter.getItem(position);
                    if (!Util.isMonthInRange(month.getYear(), month.getMonth(),
                            mDelegate.getMinYear(), mDelegate.getMinYearMonth(),
                            mDelegate.getMaxYear(), mDelegate.getMaxYearMonth())) {
                        Log.d(TAG, "onItemClick33: ");
                        return;
                    }
                    mListener.onMonthSelected(month.getYear(), month.getMonth());
                }
            }
        });
    }

    void setup(CustomCalendarViewDelegate delegate) {
        this.mDelegate = delegate;
    }

    void init(int year) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        for (int i = 1; i <= 12; i++) {
            date.set(year, i - 1, 1);
            int firstDayOfWeek = date.get(java.util.Calendar.DAY_OF_WEEK) - 1;//月第一天为星期几,星期天 == 0
            int mDaysCount = Util.getMonthDaysCount(year, i);
            Month month = new Month();
            month.setDiff(firstDayOfWeek);
            month.setCount(mDaysCount);
            month.setMonths(montList[i-1]);
            month.setMonth(i);
            month.setYear(year);
            mAdapter.addItem(month);
        }
    }

    void setSchemes(List<Calendar> mSchemes) {
        mAdapter.setSchemes(mSchemes);
    }

    void setSchemeColor(int schemeColor) {
        mAdapter.setSchemeColor(schemeColor);
    }

    void setOnMonthSelectedListener(OnMonthSelectedListener listener) {
        this.mListener = listener;
    }

    interface OnMonthSelectedListener {
        void onMonthSelected(int year, int month);
    }
}
