package com.listen.test_mogu_view.viewpager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.listen.test_mogu_view.R;


public class TransforViewTag2 extends View {

    // /////////////////////////////////////////////////////////////////////////
    // 页面切换时的，白色矩形背景的变化参数
    // /////////////////////////////////////////////////////////////////////////
    /**
     * 第1页->第2页
     */
    public static final float FIRST_HEIGHT = 0.4f;// 第1个页面高度缩放比例，正：放大，负：缩小
    public final int FIRST_TOP1 = -dp2px(30);// 第1个页面top移动距离，正：下移，负：上移
    public final int FIRST_TOP2 = dp2px(60);// 第1个页面top移动距离，正：下移，负：上移
    public static final float FIRST_RATE = 0.5f;// 在偏移50%处，进行下一页的显示
    /**
     * 第2页->第3页
     */
    public static final float SECOND_WIDTH = -0.15f;// 第2个页面宽度缩放比例，正：放大，负：缩小
    public final int SECOND_TOP = -dp2px(20);// 第2个页面top移动距离比例，正：下移，负：上移
    public static final float SECOND_RATE = 0.5f;// 在偏移50%处，进行下一页的显示
    /**
     * 第3页->第4页
     */
    public static final float THIRD_WIDTH = -0.1f;// 第3个页面宽度缩放比例，正：放大，负：缩小
    public static final float THIRD_HEIGHT = -0.1f;// 第3个页面高度缩放比例，正：放大，负：缩小
    public static final int THIRD_DEGREE = -10;// 第3个页面角度调整，正：顺时针，负：逆时针
    public static final float THIRD_RATE = 0.5f;// 在偏移50%处，进行下一页的显示

    // /////////////////////////////////////////////////////////////////////////
    // 白色圆角矩形背景
    // /////////////////////////////////////////////////////////////////////////
    private Paint mRectBgPaint;
    private int mRectBgDefaultCorner = dp2px(5);// 初始圆角5dp
    private float mRectBgCurrentDegree = 0;// 默认不旋转

    /**
     * 第1页初始化
     * default=默认初始宽高，left，top，如果是2，3，4页，则表示变化后的值
     * current=当前宽高，left，top，会根据偏移量变化
     */
    private float mPage1RectBgDefaultWidth = dp2px(260);
    private float mPage1RectBgDefaultHeight = dp2px(230);
    private float mPage1RectBgDefaultLeft = getScreenWidth() / 2 - mPage1RectBgDefaultWidth / 2;
    private float mPage1RectBgDefaultTop = dp2px(80);

    private float mRectBgCurrentWidth = mPage1RectBgDefaultWidth;
    private float mRectBgCurrentHeight = mPage1RectBgDefaultHeight;
    private float mRectBgCurrentLeft = mPage1RectBgDefaultLeft;
    private float mRectBgCurrentTop = mPage1RectBgDefaultTop;

    /**
     * 第1页->第2页
     * 在第1页的基础上进行变化
     * 1.height放大
     * 2.top先上移n，在下移n*2
     */
    private float mPage2RectBgDefaultWidth = mPage1RectBgDefaultWidth;
    private float mPage2RectBgDefaultHeight = mPage1RectBgDefaultHeight * (1 + FIRST_HEIGHT);
    private float mPage2RectBgDefaultLeft = mPage1RectBgDefaultLeft;
    private float mPage2RectBgDefaultTop = mPage1RectBgDefaultTop + FIRST_TOP1 + FIRST_TOP2;

    /**
     * 第2页->第3页
     * 在第2页的基础上进行变化
     * 1.宽度缩小
     * 2.top上移
     */
    private float mPage3RectBgDefaultWidth = mPage2RectBgDefaultWidth * (1 + SECOND_WIDTH);
    private float mPage3RectBgDefaultHeight = mPage2RectBgDefaultHeight;
    private float mPage3RectBgDefaultLeft = getScreenWidth() / 2 - mPage3RectBgDefaultWidth / 2;
    private float mPage3RectBgDefaultTop = mPage2RectBgDefaultTop + SECOND_TOP;

    /**
     * 第3页->第4页
     * 在第3页的基础上进行变化
     * 1.宽度缩小
     * 2.高度缩小
     * 2.逆时针旋转
     */
    private float mPage4RectBgDefaultWidth = mPage3RectBgDefaultWidth * (1 + THIRD_WIDTH);
    private float mPage4RectBgDefaultHeight = mPage3RectBgDefaultHeight * (1 + THIRD_HEIGHT);
    private float mPage4RectBgDefaultLeft = getScreenWidth() / 2 - mPage4RectBgDefaultWidth / 2;
    private float mPage4RectBgDefaultTop = mPage3RectBgDefaultTop;
    private float mPage4ModelDefaultWidth = (mPage4RectBgDefaultWidth - padding() * 4) / 3;

    // /////////////////////////////////////////////////////////////////////////
    // 第1页，view元素
    // /////////////////////////////////////////////////////////////////////////
    private ViewModel mPage1BottomBg;// 底部背景图

    // /////////////////////////////////////////////////////////////////////////
    // 第2页，view元素
    // /////////////////////////////////////////////////////////////////////////
    private ViewModel[] mPage2Split = new ViewModel[2];// 2张裂变背景图

    // /////////////////////////////////////////////////////////////////////////
    // 第4页，view元素
    // /////////////////////////////////////////////////////////////////////////
    private ViewModel[] mPage4Split = new ViewModel[2];// 2张裂变背景图

    /**
     * 当前页数索引
     */
    private int mCurrentPageIndex;

    public TransforViewTag2(Context context) {
        super(context);
        init();
    }

    public TransforViewTag2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransforViewTag2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void transfor(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("ls_debug_log", "p=" + position + ",p_offset=" + positionOffset + ",p_px=" + positionOffsetPixels);
        mCurrentPageIndex = position;
        calculateOffset(position, positionOffset, positionOffsetPixels);
    }

    private void init() {
        {
            /** 第1页，初始化矩形背景 */
            mRectBgPaint = new Paint();
            mRectBgPaint.setAntiAlias(true); // 设置画笔为无锯齿
            mRectBgPaint.setColor(Color.BLACK); // 设置画笔颜色
            mRectBgPaint.setStyle(Paint.Style.FILL); // 空心效果
        }
        /** 第1页，底部背景图 */
        mPage1BottomBg =
            new ViewModel(getContext(), R.drawable.one_bottom_bg).alpha(255)
                .width(mPage1RectBgDefaultWidth - padding() * 2)
                // .height()// 不传则按宽度比例缩放
                .left(mPage1RectBgDefaultLeft + padding())
                // top距离=矩形背景top+height+5dp边距
                .top(mPage1RectBgDefaultTop + mPage1RectBgDefaultHeight + padding())
                .create();

        /** 第2页，裂变背景图 */
        for (int i = 0; i < 2; i++) {
            mPage2Split[i] =
                new ViewModel(getContext(), R.drawable.two_bg).width(mPage2RectBgDefaultWidth)
                    .height(mPage2RectBgDefaultHeight)
                    .left(mPage2RectBgDefaultLeft)
                    .top(mPage2RectBgDefaultTop)
                    .create();
        }
        /** 第4页，2张裂变背景图 */
        for (int i = 0; i < mPage4Split.length; i++) {
            mPage4Split[i] =
                new ViewModel(getContext(), R.drawable.four_bg)
                        .width(mPage4RectBgDefaultWidth)
                    .height(mPage4RectBgDefaultHeight)
                    .left(mPage4RectBgDefaultLeft)
                    .top(mPage4RectBgDefaultTop);

        }
    }

    /**
     * @desc 根据viewPager偏移，修改view的宽，高，left，top
     * @author listen
     */
    private void calculateOffset(int position, float positionOffset, int positionOffsetPixels) {
        if (fromPage1ToPage2(position)) {
            /** 第1页，底部背景图，根据页面向左偏移 */
            mPage1BottomBg.currentLeft = mPage1BottomBg.defaultLeft - positionOffsetPixels;

            if (positionOffset < FIRST_RATE) {
                /** 第1页，在0->50%区间偏移 */
                /** 矩形背景，高度放大40%，向上移动30dp */
                transformRectBgFrom1To2Before(positionOffset);

            } else {
                /** 第1页，在50%->100%区间偏移 */
                /** 矩形背景，上移30dp后，向下偏移60dp */
                transformRectBgFrom1To2After(positionOffset);

                /** 第2页，渐渐裂变背景图 */
                stepByShowPage2Views(positionOffset);
            }
        } else if (fromPage2ToPage3(position)) {
            /** 矩形背景，宽度缩小15%，上移20dp */
            transformRectBgFrom2To3(positionOffset);

            if (positionOffset < SECOND_RATE) {
                /** 第2页，在0->50%区间偏移，渐渐隐藏裂变背景图 */
                stepByHidePage2Views(positionOffset, positionOffsetPixels);
            }
        } else if (fromPage3ToPage4(position)) {

            /** 背景矩形的宽度，高度减少10%，逆时针旋转10度 */
            transformRectBgFrom3To4(positionOffset);

            if (positionOffset < THIRD_RATE) {

            } else {
                /** 渐渐显示第4页，裂变背景图 */
                stepByShowPage4Views(positionOffset);
            }
        }
        postInvalidate();
    }

    private void stepByShowPage4Views(float positionOffset) {
        /** 显示第4页，裂变背景图，并向左右平移 */
        float offset = (mPage4RectBgDefaultWidth + dp2px(40)) * ((positionOffset - THIRD_RATE) * (1 / (1 - THIRD_RATE)));
        for (int i = 0; i < mPage4Split.length; i++) {
            mPage4Split[i].matrix.reset();
            mPage4Split[i].matrix.postScale(mPage4RectBgDefaultWidth / mPage4Split[i].bitmap.getWidth(), mPage4RectBgDefaultHeight / mPage4Split[i].bitmap.getHeight());

            float currentLeft = 0;
            if (i == 0) {
                // 左移
                currentLeft = mPage4RectBgDefaultLeft - offset;
            } else if (i == 1) {
                // 右移
                currentLeft = mPage4RectBgDefaultLeft + offset;
            }

            // 平移
            mPage4Split[i].matrix.postTranslate(currentLeft, mPage4RectBgDefaultTop);
            // 旋转角度
            mPage4Split[i].matrix.postRotate(THIRD_DEGREE, currentLeft + mPage4RectBgDefaultWidth/2,
                    mPage4RectBgDefaultTop + mPage4RectBgDefaultHeight/2);

            mPage4Split[i].alpha((int) (255 * ((positionOffset - THIRD_RATE) * (1 / (1 - THIRD_RATE)))));
        }
    }

    private void transformRectBgFrom3To4(float positionOffset) {
        /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次，保证变化完成 */
        mRectBgCurrentWidth = mPage3RectBgDefaultWidth;
        mRectBgCurrentTop = mPage3RectBgDefaultTop;

        /** 调整第4页，背景矩形的宽高和角度 */
        /** 背景矩形的宽度，在第3页调整宽度的基础上进行缩小 */
        mRectBgCurrentWidth = mPage3RectBgDefaultWidth * (1 + THIRD_WIDTH * positionOffset);
        mRectBgCurrentLeft = getScreenWidth() / 2 - mRectBgCurrentWidth / 2;
        /** 背景矩形的高度，在第2页调整高度的基础上进行缩小 */
        mRectBgCurrentHeight = mPage3RectBgDefaultHeight * (1 + THIRD_HEIGHT * positionOffset);
        /** 背景矩形逆时针旋转 */
        mRectBgCurrentDegree = THIRD_DEGREE * positionOffset;
    }

    private void transformRectBgFrom1To2Before(float positionOffset) {
        /** 矩形背景，高度放大40% */
        /**
         * 偏移到50%的时候height需要放大40%，defaultHeight=400，targetHeight=400*1.4=560
         *
         * offset=0
         * 400 * (1 + 0.4 * 0 * (1 / 0.5)) = 400
         *
         * offset=0.25
         * 400 * (1 + 0.4 * 0.25 * (1 / 0.5)) = 400 * 1.2 = 480
         *
         * offset=0.5
         * 400 * (1 + 0.4 * 0.5 * (1 / 0.5)) = 400 * 1.4 = 560
         *
         */
        mRectBgCurrentHeight =
            (int) (mPage1RectBgDefaultHeight * (1 + FIRST_HEIGHT * positionOffset * (1 / FIRST_RATE)));
        /** 矩形背景，向上移动30dp */
        mRectBgCurrentTop = (int) (mPage1RectBgDefaultTop + (FIRST_TOP1 * positionOffset * (1 / FIRST_RATE)));
    }

    private void transformRectBgFrom1To2After(float positionOffset) {
        /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次，保证变化完成 */
        mRectBgCurrentHeight = mPage2RectBgDefaultHeight;
        mRectBgCurrentTop = mPage1RectBgDefaultTop + FIRST_TOP1;
        /** 第1页，在50%->100%区间偏移 */
        /** 矩形背景，在上上偏移30dp后，向下偏移60dp */
        mRectBgCurrentTop =
            (int) (mPage1RectBgDefaultTop + FIRST_TOP1 + (FIRST_TOP2 * (positionOffset - FIRST_RATE) * 1.0 / (1 - FIRST_RATE)));
    }

    private void stepByShowPage2Views(float positionOffset) {
        /** 第2页，计算裂变背景图的偏移px，并修改透明度渐变显示 */
        float offset =
            (mPage1RectBgDefaultWidth + dp2px(15)) * ((positionOffset - FIRST_RATE) * (1 / (1 - FIRST_RATE)));
        mPage2Split[0].currentLeft = mPage2Split[0].defaultLeft - offset;
        mPage2Split[1].currentLeft = mPage2Split[0].defaultLeft + offset;
        /**
         * 偏移到50%的时候alpha需要为0，偏移到100%，alpha需要为255，不过此时positionOffset的取值=0.5~1
         *
         * offset=0.5
         * 255 * (0.5 - 0.5) * (1 / (1 - 0.5)))=255 * 0 = 0
         *
         * offset=0.75
         * 255 * (0.75 - 0.5) * (1 / (1 - 0.5)))=255 * 0.5 = 127.5
         *
         * offset=1
         * 255 * (1 - 0.5) * (1 / (1 - 0.5)))=255 * 1 = 255
         */
        mPage2Split[0].alpha((int) (255 * (positionOffset - FIRST_RATE) * (1 / (1 - FIRST_RATE))));
        mPage2Split[1].alpha((int) (255 * (positionOffset - FIRST_RATE) * (1 / (1 - FIRST_RATE))));
    }

    private void transformRectBgFrom2To3(float positionOffset) {
        /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次，保证变化完成 */
        mRectBgCurrentHeight = mPage2RectBgDefaultHeight;
        mRectBgCurrentTop = mPage2RectBgDefaultTop;

        /** 矩形背景，宽度缩小15% */
        mRectBgCurrentWidth = (int) (mPage2RectBgDefaultWidth * (1 + SECOND_WIDTH * positionOffset));
        mRectBgCurrentLeft = getScreenWidth() / 2 - mRectBgCurrentWidth / 2;

        /** 矩形背景，上移20dp */
        mRectBgCurrentTop = (int) (mPage2RectBgDefaultTop + (SECOND_TOP * positionOffset));
    }

    private void stepByHidePage2Views(float positionOffset, int positionOffsetPixels) {
        /** 裂变背景图，跟随滑动，向左偏移至消失 */
        mPage2Split[0].currentLeft =
            (mPage2Split[0].defaultLeft - mPage1RectBgDefaultWidth - dp2px(15)) - positionOffsetPixels
                * (1 / SECOND_RATE);
        mPage2Split[1].currentLeft =
            (mPage2Split[1].defaultLeft + mPage1RectBgDefaultWidth + dp2px(15)) - positionOffsetPixels
                * (1 / SECOND_RATE);
        mPage2Split[0].alpha((int) (255 - (255 * positionOffset * (1 / SECOND_RATE))));
        mPage2Split[1].alpha((int) (255 - (255 * positionOffset * (1 / SECOND_RATE))));
    }

    private boolean isPage4(int currentPageIndex) {
        return currentPageIndex == 3;
    }

    private boolean fromPage3ToPage4(int position) {
        return position == 2;
    }

    private boolean fromPage2ToPage3(int position) {
        return position == 1;
    }

    private boolean fromPage1ToPage2(int position) {
        return position == 0;
    }

    public void drawBitmap(Canvas canvas, ViewModel viewModel) {
        canvas.drawBitmap(viewModel.bitmap, viewModel.currentLeft, viewModel.currentTop, viewModel.paint);
    }

    public void drawBitmapMatrix(Canvas canvas, ViewModel viewModel) {
        canvas.drawBitmap(viewModel.bitmap, viewModel.matrix, viewModel.paint);
    }

    /**
     * @desc 根据当前页面，绘制不同的view
     * @author listen
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** 按重叠顺序绘制 */
        if (fromPage1ToPage2(mCurrentPageIndex)) {

            /** 绘制第1页，底部背景图 */
            drawBitmap(canvas, mPage1BottomBg);

            /** 绘制第2页，裂变背景图 */
            drawBitmap(canvas, mPage2Split[0]);
            drawBitmap(canvas, mPage2Split[1]);

            /** 绘制白色矩形背景 */
            drawWhiteRectBackgroud(canvas);

        } else if (fromPage2ToPage3(mCurrentPageIndex)) {

            /** 绘制第2页，裂变背景图 */
            drawBitmap(canvas, mPage2Split[0]);
            drawBitmap(canvas, mPage2Split[1]);

            /** 绘制矩形背景 */
            drawWhiteRectBackgroud(canvas);

        } else if (fromPage3ToPage4(mCurrentPageIndex)) {
            /** 绘制第4页，裂变背景图 */
            drawBitmapMatrix(canvas, mPage4Split[0]);
            drawBitmapMatrix(canvas, mPage4Split[1]);

            /** 绘制矩形背景 */
            drawWhiteRectBackgroud(canvas);

        } else if (isPage4(mCurrentPageIndex)) {

            /** 绘制第4页，裂变背景图 */
            drawBitmapMatrix(canvas, mPage4Split[0]);
            drawBitmapMatrix(canvas, mPage4Split[1]);

            /** 绘制矩形背景 */
            drawWhiteRectBackgroud(canvas);

        }
    }

    @NonNull
    private RectF drawWhiteRectBackgroud(Canvas canvas) {
        RectF rect = new RectF();
        rect.left = mRectBgCurrentLeft;
        rect.top = mRectBgCurrentTop;
        rect.right = rect.left + mRectBgCurrentWidth;
        rect.bottom = rect.top + mRectBgCurrentHeight;

        canvas.rotate(mRectBgCurrentDegree, rect.left + mRectBgCurrentWidth / 2, rect.top + mRectBgCurrentHeight / 2);
        canvas.drawRoundRect(rect, mRectBgDefaultCorner, mRectBgDefaultCorner, mRectBgPaint);
        return rect;
    }

    private int padding() {
        return dp2px(5);
    }

    public int getScreenWidth() {
        if (null != getResources() && null != getResources().getDisplayMetrics()) {
            return getResources().getDisplayMetrics().widthPixels;
        }
        return 0;
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
