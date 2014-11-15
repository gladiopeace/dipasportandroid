package com.dipacommerce.dipasport.data;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ImageGridView extends GridView {

    public ImageGridView(Context context) {
        super(context);
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = heightMeasureSpec;
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT)
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }

}
