package com.idx.naboo.dish;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import java.util.jar.Attributes;

/**
 * Created by sunny on 18-5-8.
 */

public class DishGridView extends GridView {

    public DishGridView(Context context) {
        super(context);
    }

    public DishGridView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public DishGridView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //因为32位的数据中的前两位是代表的模式，那么Integer.MAX_VALUE >> 2就代表能获取到的最大值（不含模式下的值）
        //MeasureSpec.AT_MOST这个模式下面高度会在listView、gridView的item集高度和Integer.MAX_VALUE >> 2
        //之间取最小值,也就是包裹内容
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
