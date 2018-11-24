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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Google规范化的属性委托
 */
class CustomCalendarViewDelegate {

    /**
     * 支持转换的最小农历年份
     */
    static final int MIN_YEAR = 1900;
    /**
     * 支持转换的最大农历年份
     */
     static final int MAX_YEAR = 2099;

    /**
     * 各种字体颜色，看名字知道对应的地方
     */
    private int mCurDayTextColor, mWeekTextColor,
            mSchemeTextColor,
            mSchemeLunarTextColor,
            mOtherMonthTextColor,
            mCurrentMonthTextColor,
            mSelectedTextColor,
            mSelectedLunarTextColor,
            mCurMonthLunarTextColor,
            mOtherMonthLunarTextColor;

    /**
     * 标记的主题色和选中的主题色
     */
    private int mSchemeThemeColor, mSelectedThemeColor;

    /**
     * 星期栏的背景
     */
    private int mWeekBackground;

    /**
     * 自定义的日历路径
     */
    private String mCalendarCardViewClass;

    /**
     * 自定义周视图路径
     */
    private String mWeekViewClass;

    /**
     * 自定义周栏路径
     */
    private String mWeekBarClass;

    /**
     * 最小年份和最大年份
     */
    private int mMinYear, mMaxYear;

    /**
     * 最小年份和最大年份对应最小月份和最大月份
     * when you want set 2015-07 to 2017-08
     */
    private int mMinYearMonth, mMaxYearMonth;

    /**
     * 日期和农历文本大小
     */
    private int mDayTextSize, mLunarTextSize;

    /**
     * 日历卡的项高度
     */
    private int mCalendarItemHeight;

    /**
     * 今天的日子
     */
    private Calendar mCurrentDate;

    /**
     * 当前月份和周视图的item位置
     */
    int mCurrentMonthViewItem, mCurrentWeekViewItem;

    /**
     * 标记的日期
     */
    List<Calendar> mSchemeDate;

    /**
     * 日期切换监听
     */
    @SuppressWarnings("all")
    CalendarView.OnDateChangeListener mDateChangeListener;

    /**
     * 日期被选中监听
     */
    CalendarView.OnDateSelectedListener mDateSelectedListener;

    /**
     * 内部日期切换监听，用于内部更新计算
     */
    CalendarView.OnInnerDateSelectedListener mInnerListener;

    /**
     * 快速年份切换
     */
    CalendarView.OnYearChangeListener mYearChangeListener;
    LunarCalendar lunarCalendar;
    /**
     * 保存选中的日期
     */
    Calendar mSelectedCalendar;
    private Context mContext;

    CustomCalendarViewDelegate(Context context, @Nullable AttributeSet attrs) {
        lunarCalendar = new LunarCalendar();
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        mCurDayTextColor = array.getColor(R.styleable.CalendarView_current_day_text_color, Color.RED);
        mSchemeTextColor = array.getColor(R.styleable.CalendarView_scheme_text_color, 0xFFFFFFFF);
        mSchemeLunarTextColor = array.getColor(R.styleable.CalendarView_scheme_lunar_text_color, 0xFFe1e1e1);
        mSchemeThemeColor = array.getColor(R.styleable.CalendarView_scheme_theme_color, 0x50CFCFCF);
        mCalendarCardViewClass = array.getString(R.styleable.CalendarView_calendar_card_view);
        mWeekViewClass = array.getString(R.styleable.CalendarView_week_view);
        mWeekBarClass = array.getString(R.styleable.CalendarView_week_bar_view);
        mWeekBackground = array.getColor(R.styleable.CalendarView_week_background, Color.WHITE);
        mWeekTextColor = array.getColor(R.styleable.CalendarView_week_text_color, Color.BLACK);

        mSelectedThemeColor = array.getColor(R.styleable.CalendarView_selected_theme_color, 0x50CFCFCF);
        mSelectedTextColor = array.getColor(R.styleable.CalendarView_selected_text_color, 0xFF111111);
        mSelectedLunarTextColor = array.getColor(R.styleable.CalendarView_selected_lunar_text_color, 0xFF111111);
        mCurrentMonthTextColor = array.getColor(R.styleable.CalendarView_current_month_text_color, 0xFF111111);
        mOtherMonthTextColor = array.getColor(R.styleable.CalendarView_other_month_text_color, 0xFFe1e1e1);

        mCurMonthLunarTextColor = array.getColor(R.styleable.CalendarView_current_month_lunar_text_color, Color.GRAY);
        mOtherMonthLunarTextColor = array.getColor(R.styleable.CalendarView_other_month_lunar_text_color, Color.GRAY);
        mMinYear = array.getInt(R.styleable.CalendarView_min_year, 1971);
        mMaxYear = array.getInt(R.styleable.CalendarView_max_year, 2055);
        mMinYearMonth = array.getInt(R.styleable.CalendarView_min_year_month, 1);
        mMaxYearMonth = array.getInt(R.styleable.CalendarView_max_year_month, 12);

        mDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_day_text_size, Util.dipToPx(context, 20));
//        if ( context.getResources().getConfiguration().locale.getCountry().equals("UK") || context.getResources().getConfiguration().locale.getCountry().equals("US")) {
//            mLunarTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_lunar_text_size, Util.dipToPx(context, 0));
//        }else {
            //mLunarTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_lunar_text_size, Util.dipToPx(context, 16));
            mLunarTextSize = context.getResources().getDimensionPixelSize(R.dimen.calendar_MonthView_mLunarTextSize_textSize);
//        }
        //mCalendarItemHeight = (int) array.getDimension(R.styleable.CalendarView_calendar_height, Util.dipToPx(context, 65));
        mCalendarItemHeight = context.getResources().getDimensionPixelSize(R.dimen.calendar_MonthViewPager_layout_marginTop);
        if (mMinYear <= MIN_YEAR) mMaxYear = 1971;
        if (mMaxYear >= MAX_YEAR) mMaxYear = 2055;
        array.recycle();
        init();
    }

    private void init() {
        mCurrentDate = new Calendar();
        Date d = new Date();
        mCurrentDate.setYear(Util.getDate("yyyy", d));
        mCurrentDate.setMonth(Util.getDate("MM", d));
        mCurrentDate.setDay(Util.getDate("dd", d));
        mCurrentDate.setWeekend(Util.isWeekend(mCurrentDate));
        mCurrentDate.setWeek(Util.getWeekFormCalendar(mCurrentDate));
        mCurrentDate.setLunar(lunarCalendar.getLunarText(mContext, mCurrentDate));
        mCurrentDate.setCurrentDay(true);
        mCurrentWeekViewItem = Util.getWeekFromCalendarBetweenYearAndYear(mCurrentDate, mMinYear, mMinYearMonth);
    }

    int getCurDayTextColor() {
        return mCurDayTextColor;
    }

    @SuppressWarnings("unused")
    int getWeekTextColor() {
        return mWeekTextColor;
    }

    int getSchemeTextColor() {
        return mSchemeTextColor;
    }

    int getSchemeLunarTextColor() {
        return mSchemeLunarTextColor;
    }

    int getOtherMonthTextColor() {
        return mOtherMonthTextColor;
    }

    int getCurrentMonthTextColor() {
        return mCurrentMonthTextColor;
    }

    int getSelectedTextColor() {
        return mSelectedTextColor;
    }

    int getSelectedLunarTextColor() {
        return mSelectedLunarTextColor;
    }

    int getCurrentMonthLunarTextColor() {
        return mCurMonthLunarTextColor;
    }

    int getOtherMonthLunarTextColor() {
        return mOtherMonthLunarTextColor;
    }

    int getSchemeThemeColor() {
        return mSchemeThemeColor;
    }

    int getSelectedThemeColor() {
        return mSelectedThemeColor;
    }

    int getWeekBackground() {
        return mWeekBackground;
    }

    String getCalendarCardViewClass() {
        return mCalendarCardViewClass;
    }

    String getWeekViewClass() {
        return mWeekViewClass;
    }

    String getWeekBarClass() {
        return mWeekBarClass;
    }

    int getMinYear() {
        return mMinYear;
    }

    int getMaxYear() {
        return mMaxYear;
    }

    int getDayTextSize() {
        return mDayTextSize;
    }

    int getLunarTextSize() {
        return mLunarTextSize;
    }

    int getCalendarItemHeight() {
        return mCalendarItemHeight;
    }

    void setMinYear(int minYear) {
        this.mMinYear = minYear;
    }

    void setMaxYear(int maxYear) {
        this.mMaxYear = maxYear;
    }

    int getMinYearMonth() {
        return mMinYearMonth;
    }

    int getMaxYearMonth() {
        return mMaxYearMonth;
    }

    void setTextColor(int curDayTextColor, int curMonthTextColor, int otherMonthTextColor, int curMonthLunarTextColor, int otherMonthLunarTextColor) {
        mCurDayTextColor = curDayTextColor;
        mOtherMonthTextColor = otherMonthTextColor;
        mCurrentMonthTextColor = curMonthTextColor;
        mCurMonthLunarTextColor = curMonthLunarTextColor;
        mOtherMonthLunarTextColor = otherMonthLunarTextColor;
    }

    void setSchemeColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        this.mSchemeThemeColor = schemeColor;
        this.mSchemeTextColor = schemeTextColor;
        this.mSchemeLunarTextColor = schemeLunarTextColor;
    }

    void setSelectColor(int selectedColor, int selectedTextColor, int selectedLunarTextColor) {
        this.mSelectedThemeColor = selectedColor;
        this.mSelectedTextColor = selectedTextColor;
        this.mSelectedLunarTextColor = selectedLunarTextColor;
    }

    @SuppressWarnings("unused")
    @Deprecated
    void setDayTextSize(float calendarTextSize, float lunarTextSize) {
        mDayTextSize = (int) calendarTextSize;
        mLunarTextSize = (int) lunarTextSize;
    }

    Calendar getCurrentDay() {
        return mCurrentDate;
    }

    Calendar createCurrentDate() {
        Log.d("franck", "createCurrentDate: ");
        Calendar calendar = new Calendar();
        calendar.setYear(mCurrentDate.getYear());
        calendar.setWeek(mCurrentDate.getWeek());
        calendar.setMonth(mCurrentDate.getMonth());
        calendar.setDay(mCurrentDate.getDay());
        calendar.setWeekend(mCurrentDate.isWeekend());
        calendar.setCurrentDay(true);
        calendar.setLunar(mCurrentDate.getLunar());
        return calendar;
    }
}
