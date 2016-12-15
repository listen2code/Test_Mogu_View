package com.listen.test_mogu_view.viewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;

public class ViewModel {
    Bitmap bitmap;
    Paint paint;
    Matrix matrix;

    /**
     * 初始，固定的宽高left，top
     */
    float defaultWidth;
    float defaultHeight;
    float defaultLeft;
    float defaultTop;

    /**
     * 当前的宽高left，top，会根据偏移量进行变化
     */
    float currentWidth;
    float currentHeight;
    float currentLeft;
    float currentTop;

    public ViewModel(Context context, int resId) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        paint = new Paint();
        paint.setAlpha(0);
        matrix = new Matrix();
    }

    public ViewModel alpha(int alpha) {
        paint.setAlpha(alpha);
        return this;
    }

    public ViewModel width(float width) {
        this.currentWidth = this.defaultWidth = width;
        return this;
    }

    public ViewModel height(float height) {
        this.currentHeight = this.defaultHeight = height;
        return this;
    }

    public ViewModel left(float left) {
        this.currentLeft = this.defaultLeft = left;
        return this;
    }

    public ViewModel top(float top) {
        this.currentTop = this.defaultTop = top;
        return this;
    }

    public ViewModel create() {
        // 如果没传高度
        if (defaultHeight <= 0) {
            if (defaultWidth > 0) {
                // 有宽度，则按宽度比例缩放
                currentHeight = defaultHeight = (defaultWidth / bitmap.getWidth()) * bitmap.getHeight();
            } else {
                // 没有宽度，则去图片默认高度
                currentHeight = defaultHeight = bitmap.getHeight();
            }
        }

        // 如果没传宽度
        if (defaultWidth <= 0) {
            if (defaultHeight > 0) {
                // 有宽度，则按宽度比例缩放
                currentWidth = defaultWidth = (defaultHeight / bitmap.getHeight()) * bitmap.getWidth();
            } else {
                // 没有高度，则去图片默认宽度
                currentWidth = defaultWidth = bitmap.getWidth();
            }
        }

        /** 缩放图片尺寸到合适的比例 */
        matrix.postScale(currentWidth / bitmap.getWidth(), currentHeight / bitmap.getHeight());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return this;
    }

    /**
     * @desc 当前宽度变化比例
     */
    public float widthRate() {
        return currentWidth / defaultWidth;
    }

    /**
     * @desc 当前高度变化比例
     */
    public float heightRate() {
        return currentHeight / defaultHeight;
    }
}
