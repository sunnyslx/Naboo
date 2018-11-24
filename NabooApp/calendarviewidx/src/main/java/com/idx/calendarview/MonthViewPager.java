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
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;


/**
 * 这是一个自适应高度的View
 */
@SuppressWarnings("deprecation")
public class MonthViewPager extends ViewPager {
    private static final String TAG = "MonthViewPager";
    private CustomCalendarViewDelegate mDelegate;
    CalendarLayout mParentLayout;
    WeekViewPager mWeekPager;
    Context context;
    LunarCalendar lunarCalendar;
    private Context mContext;
    public MonthViewPager(Context context) {
        this(context, null);
    }

    public MonthViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        lunarCalendar = new LunarCalendar();
        mContext = context;

    }

    /**
     * 初始化
     *
     * @param delegate delegate
     */
    void setup(CustomCalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        init();
    }

    //滑动时
    private void init() {
        setAdapter(new MonthViewPagerAdapter());

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: 444444" +position);
                Calendar calendar = new Calendar();
                calendar.setYear((position + mDelegate.getMinYearMonth() -1) / 12 + mDelegate.getMinYear());
                calendar.setMonth((position + mDelegate.getMinYearMonth() -1 ) % 12 + 1);
                //当月的天数
                int getDaysByYearMonth_day = getDaysByYearMonth((position + mDelegate.getMinYearMonth() -1) / 12 + mDelegate.getMinYear(),(position + mDelegate.getMinYearMonth() -1 ) % 12 + 1);
                calendar.setDay(mDelegate.mSelectedCalendar.getDay());
                if (mDelegate.mSelectedCalendar.getDay() > getDaysByYearMonth_day) {
                    calendar.setDay(getDaysByYearMonth_day);
                }

                calendar.setCurrentMonth(calendar.getYear() == mDelegate.getCurrentDay().getYear() &&
                        calendar.getMonth() == mDelegate.getCurrentDay().getMonth());
                try {
                    calendar.setLunar(lunarCalendar.getLunarText(mContext, calendar));

                }catch (Exception e){
                    e.printStackTrace();
                }

                Log.d("Franck", "onPageSelected: year" + (position + mDelegate.getMinYearMonth() -1) / 12 + mDelegate.getMinYear());
                Log.d("Franck", "onPageSelected: month" + ((position + mDelegate.getMinYearMonth() -1 ) % 12 + 1));

                if (mParentLayout == null || getVisibility() == INVISIBLE || mWeekPager.getVisibility() == VISIBLE) {
                    if (mDelegate.mDateChangeListener != null) {
                        mDelegate.mDateChangeListener.onDateChange(calendar);
                    }
                    return;
                }

                if (!calendar.isCurrentMonth()) {
                    mDelegate.mSelectedCalendar = calendar;
                } else {
                    mDelegate.mSelectedCalendar = mDelegate.createCurrentDate();
                }
                if (mDelegate.mDateChangeListener != null) {
                    Log.d("Franck", "onDateChange: ");
                    mDelegate.mDateChangeListener.onDateChange(mDelegate.mSelectedCalendar);
                }

                if (mDelegate.mDateSelectedListener != null) {
                    Log.d("Franck", "calendarning");

                    mDelegate.mDateSelectedListener.onDateSelected(mDelegate.mSelectedCalendar);
                }

                BaseCalendarCardView view = (BaseCalendarCardView) findViewWithTag(position);
                if (view != null) {
                    int index = view.getSelectedIndex(mDelegate.mSelectedCalendar);
                    view.mCurrentItem = index;
                    if (index >= 0) {
                        mParentLayout.setSelectPosition(index);
                    }
                    view.invalidate();
                }
                mWeekPager.updateSelected(mDelegate.mSelectedCalendar);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 滚动到指定日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    void scrollToCalendar(int year, int month, int day) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setCurrentDay(calendar.equals(mDelegate.getCurrentDay()));
        mDelegate.mSelectedCalendar = calendar;

        int y = calendar.getYear() - mDelegate.getMinYear();
        int position = 12 * y + calendar.getMonth() - 1;
        setCurrentItem(position);

        if (mParentLayout != null) {
            int i = Util.getWeekFromDayInMonth(calendar);
            mParentLayout.setSelectWeek(i);
        }

        if (mDelegate.mInnerListener != null) {
            Log.d(TAG, "scrollToCalendar11: ");
            mDelegate.mInnerListener.onDateSelected(calendar);
        }

        if (mDelegate.mDateSelectedListener != null) {
            Log.d(TAG, "calendar55: ");
            mDelegate.mDateSelectedListener.onDateSelected(calendar);
        }
        if (mDelegate.mDateChangeListener != null) {
            Log.d(TAG, "scrollToCalendar22: ");
            mDelegate.mDateChangeListener.onDateChange(calendar);
        }

        updateSelected();
    }

    /**
     * 滚动到当前日期
     */
    void scrollToCurrent() {
        int position = 12 * (mDelegate.getCurrentDay().getYear() - mDelegate.getMinYear()) +
                mDelegate.getCurrentDay().getMonth() - mDelegate.getMinYearMonth();
        setCurrentItem(position);
        BaseCalendarCardView view = (BaseCalendarCardView) findViewWithTag(position);
        if (view != null) {
            view.setSelectedCalendar(mDelegate.getCurrentDay());
            view.invalidate();
            if (mParentLayout != null) {
                mParentLayout.setSelectPosition(view.getSelectedIndex(mDelegate.getCurrentDay()));
            }
        }
    }


    /**
     * 更新选择效果
     */
    void updateSelected() {
        for (int i = 0; i < getChildCount(); i++) {
            BaseCalendarCardView view = (BaseCalendarCardView) getChildAt(i);
            view.setSelectedCalendar(mDelegate.mSelectedCalendar);
            view.invalidate();
        }
    }

    /**
     * 更新标记日期
     */
    void updateScheme() {
        for (int i = 0; i < getChildCount(); i++) {
            BaseCalendarCardView view = (BaseCalendarCardView) getChildAt(i);
            view.update();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getCardHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 日历卡月份Adapter
     */
    private class MonthViewPagerAdapter extends PagerAdapter {

        private int count;

        private MonthViewPagerAdapter() {
            count = 12 * (mDelegate.getMaxYear() - mDelegate.getMinYear())
                    - mDelegate.getMinYearMonth() + 1 +
                    mDelegate.getMaxYearMonth();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int year = (position + mDelegate.getMinYearMonth() -1) / 12 + mDelegate.getMinYear();
            int month = (position + mDelegate.getMinYearMonth() -1) % 12 + 1;
            BaseCalendarCardView view;
            if (TextUtils.isEmpty(mDelegate.getCalendarCardViewClass())) {
                view = new DefaultCalendarCardView(getContext());
            } else {
                try {
                    Class cls = Class.forName(mDelegate.getCalendarCardViewClass());
                    @SuppressWarnings("unchecked")
                    Constructor constructor = cls.getConstructor(Context.class);
                    view = (BaseCalendarCardView) constructor.newInstance(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            view.mParentLayout = mParentLayout;

            view.setup(mDelegate);
            view.setTag(position);
            view.setCurrentDate(year, month);
            view.setSelectedCalendar(mDelegate.mSelectedCalendar);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 获取日历卡高度
     *
     * @return 获取日历卡高度
     */
    private int getCardHeight() {
        return 6 * mDelegate.getCalendarItemHeight();
    }

    /**
     * 根据年　月　获取当月的天数
     * */
    public static int getDaysByYearMonth(int year, int month) {

        java.util.Calendar a = java.util.Calendar.getInstance();
        a.set(java.util.Calendar.YEAR, year);
        a.set(java.util.Calendar.MONTH, month - 1);
        a.set(java.util.Calendar.DATE, 1);
        a.roll(java.util.Calendar.DATE, -1);
        int maxDate = a.get(java.util.Calendar.DATE);
        return maxDate;
    }

}
