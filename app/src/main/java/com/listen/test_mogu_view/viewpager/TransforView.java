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


public class TransforView extends View {

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
    private ViewModel mPage1Top;// 顶部模特图
    private ViewModel mPage1Bottom;// 底部文案图
    private ViewModel mPage1BottomBg;// 底部背景图

    // /////////////////////////////////////////////////////////////////////////
    // 第2页，view元素
    // /////////////////////////////////////////////////////////////////////////
    private int[] mPage2ModelResources = new int[] {R.drawable.two_1, R.drawable.two_2, R.drawable.two_3};
    private ViewModel mPage2Top;// 顶部文案图
    private ViewModel[] mPage2Center = new ViewModel[mPage2ModelResources.length];// 中间3张模特图
    private ViewModel mPage2Bottom;// 底部文案图
    private ViewModel[] mPage2Split = new ViewModel[2];// 2张裂变背景图

    // /////////////////////////////////////////////////////////////////////////
    // 第3页，view元素
    // /////////////////////////////////////////////////////////////////////////
    private int[] mPage3ModelResources = new int[] {R.drawable.three_1, R.drawable.three_2, R.drawable.three_3,
        R.drawable.three_4, R.drawable.three_5, R.drawable.three_6};
    private ViewModel[] mPage3Model = new ViewModel[mPage3ModelResources.length];// 6张模特图
    private float mPage3ModelDefaultWidth = (mPage3RectBgDefaultWidth - padding() * 4) / 3;// 每张模特图的默认宽度
    private float mPage3ModelDefaultHeight = (mPage3RectBgDefaultHeight - padding() * 3) / 2;// 每张模特图的默认高度

    // /////////////////////////////////////////////////////////////////////////
    // 第4页，view元素
    // /////////////////////////////////////////////////////////////////////////
    private int[] mPage4ModelResources = new int[] {R.drawable.four_bottom_1, R.drawable.four_bottom_2,
        R.drawable.four_bottom_3};
    private ViewModel mPage4Top;// 顶部模特图
    private ViewModel[] mPage4Model = new ViewModel[mPage4ModelResources.length];// 底部3张模特图
    private ViewModel[] mPage4Split = new ViewModel[2];// 2张裂变背景图
    // private float mPage4SplitTargeOffset =

    /**
     * 当前页数索引
     */
    private int mCurrentPageIndex;

    public TransforView(Context context) {
        super(context);
        init();
    }

    public TransforView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransforView(Context context, AttributeSet attrs, int defStyle) {
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
        {
            /** 第1页，顶部模特图 */
            mPage1Top = new ViewModel(getContext(), R.drawable.one_top).alpha(255)
            // 宽度为矩形背景宽度，减去左，右，各5dp边距
                .width(mPage1RectBgDefaultWidth - padding() * 2)
                // 高度为矩形背景的高度，减去上，中，下各5边距后剩余的高度的2/3
                .height((mPage1RectBgDefaultHeight - padding() * 3) / 3 * 2)
                .left(mPage1RectBgDefaultLeft + padding())
                .top(mPage1RectBgDefaultTop + padding())
                .create();

            /** 第1页，底部文案图 */
            mPage1Bottom = new ViewModel(getContext(), R.drawable.one_bottom).alpha(255)
            // 宽度为矩形背景宽度，减去左，右，各5dp边距
                .width(mPage1RectBgDefaultWidth - padding() * 2)
                // 高度为矩形背景的高度，减去上，中，下各5dp边距后剩余的高度的1/3
                .height((mPage1RectBgDefaultHeight - padding() * 3) / 3)
                .left(mPage1RectBgDefaultLeft + padding())
                // top距离=顶部模特图top+height+5dp边距
                .top(mPage1Top.defaultTop + mPage1Top.defaultHeight + padding())
                .create();

            /** 第1页，底部背景图 */
            mPage1BottomBg =
                new ViewModel(getContext(), R.drawable.one_bottom_bg).alpha(255)
                    .width(mPage1RectBgDefaultWidth - padding() * 2)
                    // .height()// 不传则按宽度比例缩放
                    .left(mPage1RectBgDefaultLeft + padding())
                    // top距离=矩形背景top+height+5dp边距
                    .top(mPage1RectBgDefaultTop + mPage1RectBgDefaultHeight + padding())
                    .create();
        }

        {

            /** 第2页，顶部文案图 */
            mPage2Top = new ViewModel(getContext(), R.drawable.two_top).width(mPage2RectBgDefaultWidth - padding() * 2)
            // .height()// 不传则按宽度比例缩放
                .left(mPage2RectBgDefaultLeft + padding())
                .top(mPage2RectBgDefaultTop + padding())
                .create();

            /** 第2页，中间3张模特图 */
            for (int i = 0; i < mPage2ModelResources.length; i++) {
                mPage2Center[i] = new ViewModel(getContext(), mPage2ModelResources[i]);
                if (i == 0) {
                    /** 第1张模特图 */
                    mPage2Center[i].width((mPage2RectBgDefaultWidth - dp2px(15)) / 3 * 2)
                        .height(mPage2Center[i].defaultWidth + dp2px(5))
                        .left(mPage2RectBgDefaultLeft + padding())
                        .top(mPage2Top.defaultTop + mPage2Top.defaultHeight + padding())
                        .create();
                } else if (i == 1) {
                    /** 第2张模特图 */
                    mPage2Center[i].width((mPage2RectBgDefaultWidth - dp2px(15)) / 3)
                        .height(mPage2Center[i].defaultWidth)
                        .left(mPage2Center[0].defaultLeft + mPage2Center[0].defaultWidth + padding())
                        .top(mPage2Top.defaultTop + mPage2Top.defaultHeight + padding())
                        .create();
                } else if (i == 2) {
                    /** 第3张模特图 */
                    mPage2Center[i].width((mPage2RectBgDefaultWidth - dp2px(15)) / 3)
                        .height(mPage2Center[i].defaultWidth)
                        .left(mPage2Center[0].defaultLeft + mPage2Center[0].defaultWidth + padding())
                        .top(mPage2Center[1].defaultTop + mPage2Center[1].defaultHeight + padding())
                        .create();
                }
            }

            /** 第2页，底部文案图 */
            mPage2Bottom =
                new ViewModel(getContext(), R.drawable.two_bottom).width(mPage2RectBgDefaultWidth - padding() * 2)
                    .height(
                        mPage2RectBgDefaultHeight - mPage2Top.defaultHeight - mPage2Center[0].defaultHeight - padding()
                            * 4)
                    .left(mPage2RectBgDefaultLeft + padding())
                    .top(mPage2Center[0].defaultTop + mPage2Center[0].defaultHeight + padding())
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
        }

        {
            /** 第3页，6张模特图 */
            for (int i = 0; i < mPage3Model.length; i++) {
                if (i < mPage3Model.length / 2) {
                    /** 第3页，第1排3张模特图 */
                    mPage3Model[i] =
                        new ViewModel(getContext(), mPage3ModelResources[i]).width(mPage3ModelDefaultWidth)
                            .height(mPage3ModelDefaultHeight)
                            .left(mPage3RectBgDefaultLeft + mPage3ModelDefaultWidth * i + padding() * (i + 1))
                            .top(mPage3RectBgDefaultTop + padding())
                            .create();
                } else {
                    /** 第3页，第2排3张模特图 */
                    mPage3Model[i] =
                        new ViewModel(getContext(), mPage3ModelResources[i]).width(mPage3ModelDefaultWidth)
                            .height(mPage3ModelDefaultHeight)
                            .left(
                                mPage3RectBgDefaultLeft + mPage3ModelDefaultWidth * (i - mPage3Model.length / 2)
                                    + padding() * ((i - mPage3Model.length / 2) + 1))
                            // 与第1排第区别在于top需要加上第1排的height
                            .top(mPage3RectBgDefaultTop + mPage3Model[i - 1].defaultHeight + padding() * 2)
                            .create();
                }
                mPage3Model[i].paint.setAntiAlias(true);

            }
        }

        {
            /** 第4页，顶部模特图 */
            mPage4Top =
                new ViewModel(getContext(), R.drawable.four_top).width(mPage4RectBgDefaultWidth - padding() * 2)
                    .left(mPage4RectBgDefaultLeft + padding())
                    .top(mPage4RectBgDefaultTop + padding())
                    .create();
            mPage4Top.paint.setAntiAlias(true);

            /** 第4页，底部3张模特图 */
            for (int i = 0; i < mPage4ModelResources.length; i++) {
                mPage4Model[i] =
                    new ViewModel(getContext(), mPage4ModelResources[i]).width(mPage4ModelDefaultWidth)
                        .height(mPage4RectBgDefaultHeight - padding() * 3 - mPage4Top.defaultHeight)
                        .left(mPage4RectBgDefaultLeft + mPage4ModelDefaultWidth * i + padding() * (i + 1))
                        .top(mPage4Top.defaultTop + mPage4Top.defaultHeight + padding());
                mPage4Model[i].paint.setAntiAlias(true);
            }

            /** 第4页，2张裂变背景图 */
            for (int i = 0; i < mPage4Split.length; i++) {
                mPage4Split[i] = new ViewModel(getContext(), R.drawable.four_bg)
                        .width(mPage4RectBgDefaultWidth)
                        .height(mPage4RectBgDefaultHeight)
                        .left(mPage4RectBgDefaultLeft)
                        .top(mPage4RectBgDefaultTop);

            }
        }
    }

    /**
     * @desc 根据viewPager偏移，修改view的宽，高，left，top
     * @author listen
     */
    private void calculateOffset(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset <= 0) {
            return;
        }
        if (fromPage1ToPage2(position)) {
            /** 第1页，底部背景图，根据页面向左偏移 */
            mPage1BottomBg.currentLeft = mPage1BottomBg.defaultLeft - positionOffsetPixels;

            if (positionOffset < FIRST_RATE) {
                /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里重新设置一次，保证第2页的view不可见 */
                hidePage2Views();

                /** 第1页，在0->50%区间偏移 */
                /** 矩形背景，高度放大40%，向上移动30dp */
                transformRectBgFrom1To2Before(positionOffset);

                /** 第1页，渐渐隐顶部图，底部图；透明度渐变消失，偏移到50%时完全消失 */
                stepByHidePage1Views(positionOffset);

            } else {
                /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次。保证第1页的view不可见 */
                hidePage1Views();

                /** 第1页，在50%->100%区间偏移 */
                /** 矩形背景，上移30dp后，向下偏移60dp */
                transformRectBgFrom1To2After(positionOffset);

                /** 第2页，渐渐显示顶部，3张模特图，底部图 */
                stepByShowPage2Views(positionOffset);

            }
        } else if (fromPage2ToPage3(position)) {
            /** 矩形背景，宽度缩小15%，上移20dp */
            transformRectBgFrom2To3(positionOffset);

            if (positionOffset < SECOND_RATE) {
                /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次。保证第3页滑回到第2页时，第3页的view不可见 */
                hidePage3Views();
                /** 第2页，在0->50%区间偏移，渐渐隐藏顶部，中间，底部，裂变背景图 */
                stepByHidePage2Views(positionOffset, positionOffsetPixels);
            } else {
                /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次，保证第2页的view不可见 */
                hidePage2Views();
                /** 第2页，在50->100%区间偏移，渐渐显示第3页，6张模特图 */
                stepByShowPage3Views(positionOffset);
            }
        } else if (fromPage3ToPage4(position)) {

            /** 背景矩形的宽度，高度减少10%，逆时针旋转10度 */
            transformRectBgFrom3To4(positionOffset);

            if (positionOffset < THIRD_RATE) {

                /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次，保证第4页的view不可见 */
                hidePage4Views();

                /** 渐渐缩放，隐藏第3页，6张模特图 */
                stepByHidePage3Views(positionOffset);

            } else {
                /** 快速滑动的时候，可能丢失最后一次绘制，所以需要在这里调重新设置一次，保证第3页的view缩放完成 */
                scaleHidePage3Views();

                /** 渐渐显示第4页，顶部图，底部3张模特图，分裂背景图 */
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

        /** 显示第4页，顶部模特图 */
        mPage4Top.alpha((int) (255 * ((positionOffset - THIRD_RATE) * (1 / (1 - THIRD_RATE)))));

        /** 显示第4页，底部3张模特图 */
        for (int i = 0; i < mPage4Model.length; i++) {
            if (i == 0) {
                mPage4Model[i].currentWidth =
                    mPage4Model[i].defaultWidth * ((positionOffset - THIRD_RATE) * (1 / (1 - THIRD_RATE)));
                mPage4Model[i].currentHeight =
                    mPage4Model[i].defaultHeight * ((positionOffset - THIRD_RATE) * (1 / (1 - THIRD_RATE)));
                mPage4Model[i].alpha((int) (255 * (positionOffset - THIRD_RATE) * (1 / (1 - THIRD_RATE))));
            } else {
                if (mPage4Model[i - 1].currentWidth >= mPage4ModelDefaultWidth / 2) {
                    mPage4Model[i].currentWidth =
                        mPage4Model[i].defaultWidth * ((mPage4Model[i - 1].widthRate() - 0.5f) * 2);
                    mPage4Model[i].currentHeight =
                        mPage4Model[i].defaultHeight * ((mPage4Model[i - 1].widthRate() - 0.5f) * 2);
                    mPage4Model[i].currentLeft =
                        mPage4Model[i - 1].currentLeft + mPage4Model[i - 1].currentWidth + padding();
                    mPage4Model[i].alpha((int) (255 * (mPage4Model[i - 1].widthRate() - 0.5f) * 2));
                }
            }
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

    private void stepByHidePage3Views(float positionOffset) {
        /** 隐藏第3页6张模特图 */
        /** 从第1排，第3-1张开始，依次缩放 */
        for (int i = mPage3ModelResources.length / 2 - 1; i >= 0; i--) {
            if (i == mPage3ModelResources.length / 2 - 1) {
                /** 如果是第1排，第3张，则开始缩放 */
                mPage3Model[i].currentHeight =
                    mPage3Model[i].defaultHeight * (1 - positionOffset * (1 / (1 - THIRD_RATE)));
                mPage3Model[i].currentWidth =
                    mPage3Model[i].defaultWidth * (1 - positionOffset * (1 / (1 - THIRD_RATE)));
            } else {
                /** 如果是第1排，第1/2张，则判断后1张缩放到一半的时候开始自己的缩放 */
                if (mPage3Model[i + 1].currentHeight <= mPage3Model[i + 1].defaultHeight / 2) {
                    mPage3Model[i].currentHeight = mPage3Model[i].defaultHeight * mPage3Model[i + 1].heightRate() * 2;
                    mPage3Model[i].currentWidth = mPage3Model[i].defaultWidth * mPage3Model[i + 1].heightRate() * 2;
                } else {
                    mPage3Model[i].currentHeight = mPage3Model[i].defaultHeight;
                    mPage3Model[i].currentWidth = mPage3Model[i].defaultWidth;
                }
            }

            /** 跳转left，top，实现居中缩放 */
            mPage3Model[i].currentLeft =
                mPage3Model[i].defaultLeft + mPage3Model[i].defaultWidth / 2 - mPage3Model[i].currentWidth / 2;
            mPage3Model[i].currentTop =
                mPage3Model[i].defaultTop + mPage3Model[i].defaultHeight / 2 - mPage3Model[i].currentHeight / 2;
        }

        /** 从第1排，第4-6张开始，依次缩放 */
        for (int i = mPage3ModelResources.length / 2; i < mPage3ModelResources.length; i++) {
            if (i == mPage3ModelResources.length / 2) {
                /** 如果是第2排，第1张，则开始缩放 */
                mPage3Model[i].currentHeight =
                    mPage3Model[i].defaultHeight * (1 - positionOffset * (1 / (1 - THIRD_RATE)));
                mPage3Model[i].currentWidth =
                    mPage3Model[i].defaultWidth * (1 - positionOffset * (1 / (1 - THIRD_RATE)));
            } else {
                /** 如果是第2排，第5/6张，则判断前1张缩放到一半的时候开始自己的缩放 */
                if (mPage3Model[i - 1].currentHeight <= mPage3Model[i - 1].defaultHeight / 2) {
                    mPage3Model[i].currentHeight = mPage3Model[i].defaultHeight * mPage3Model[i - 1].heightRate() * 2;
                    mPage3Model[i].currentWidth = mPage3Model[i].defaultWidth * mPage3Model[i - 1].heightRate() * 2;
                } else {
                    mPage3Model[i].currentHeight = mPage3Model[i].defaultHeight;
                    mPage3Model[i].currentWidth = mPage3Model[i].defaultWidth;
                }
            }

            /** 跳转left，top，实现居中缩放 */
            mPage3Model[i].currentLeft =
                mPage3Model[i].defaultLeft + mPage3Model[i].defaultWidth / 2 - mPage3Model[i].currentWidth / 2;
            mPage3Model[i].currentTop =
                mPage3Model[i].defaultTop + mPage3Model[i].defaultHeight / 2 - mPage3Model[i].currentHeight / 2;
        }
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

    private void stepByHidePage1Views(float positionOffset) {
        /**
         * 偏移到50%的时候alpha需要为0，view不可见
         *
         * offset=0
         * 255-(255*0.0*(1/0.5)) = 0
         *
         * offset=0.25
         * 255-(255*0.25*(1/0.5)) = 127
         *
         * offset=0.5
         * 255-(255*0.5*(1/0.5)) = 255
         */
        mPage1Top.alpha((int) (255 - (255 * positionOffset * (1 / FIRST_RATE))));
        mPage1Bottom.alpha((int) (255 - (255 * positionOffset * (1 / FIRST_RATE))));

        /** 第1页，顶部图向下移动 */
        mPage1Top.currentTop = mPage1Top.defaultTop + (FIRST_TOP2 + FIRST_TOP1) * positionOffset * (1 / FIRST_RATE);

        /** 第1页，底部图跟随顶部图向下移动 */
        mPage1Bottom.currentTop = mPage1Top.currentTop + mPage1Top.defaultHeight + padding();
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
        /** 第2页，顶部图，跟随矩形背景下移 */
        mPage2Top.currentTop = mRectBgCurrentTop + padding();

        /** 第2页，底部图，跟随矩形背景下移 */
        mPage2Bottom.currentTop = mPage2Center[0].currentTop + mPage2Center[0].defaultHeight + padding();

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

        /** 第2页，顶部，底部图，透明度渐变显示，偏移量达到100%，完成显示 */
        mPage2Top.alpha((int) (255 * (positionOffset - FIRST_RATE) * (1 / (1 - FIRST_RATE))));
        mPage2Bottom.alpha((int) (255 * (positionOffset - FIRST_RATE) * (1 / (1 - FIRST_RATE))));

        /** 第2页，显示中间3张模特图 */
        for (int i = 0; i < mPage2Center.length; i++) {
            if (i == 0) {
                /** 第2页，显示第1张模特图 */
                mPage2Center[i].currentWidth =
                    mPage2Center[i].defaultWidth * (positionOffset - FIRST_RATE) * 1 / (1 - FIRST_RATE);
                mPage2Center[i].currentHeight =
                    mPage2Center[i].defaultHeight * (positionOffset - FIRST_RATE) * 1 / (1 - FIRST_RATE);
                mPage2Center[i].alpha((int) (255 * (positionOffset - FIRST_RATE) * (1 / (1 - FIRST_RATE))));
                mPage2Center[i].currentTop = mPage2Top.currentTop + mPage2Top.currentHeight + padding();
            } else {
                /** 第2，3张模特图，在前1张显示到一半时才显示 */
                if (mPage2Center[i - 1].currentWidth >= mPage2Center[i - 1].defaultWidth / 2) {
                    float rate = mPage2Center[i - 1].widthRate() - 0.5f;
                    mPage2Center[i].currentWidth = mPage2Center[i].defaultWidth * (rate * 2);
                    mPage2Center[i].currentHeight = mPage2Center[i].defaultHeight * (rate * 2);
                    /** 第2，3张模特图，需要根据第1张图计算left */
                    mPage2Center[i].currentLeft =
                        mPage2Center[0].currentLeft + mPage2Center[0].currentWidth + padding();
                    mPage2Center[i].currentTop = mPage2Top.currentTop + mPage2Top.currentHeight + padding();
                    if (i == 2) {
                        /** 第3张模特图，根据第2张图计算top */
                        mPage2Center[i].currentTop =
                            mPage2Center[1].currentTop + mPage2Center[1].currentHeight + padding();
                    }
                    mPage2Center[i].alpha((int) (255 * (positionOffset * rate * 2)));
                } else {
                    mPage2Center[i].alpha(0);
                }
            }
        }
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

        /** 顶部图，3张模特图，底部图，跟随矩形背景上移 */
        mPage2Top.currentTop = mRectBgCurrentTop + padding();
        mPage2Center[0].currentTop = mPage2Top.currentTop + mPage2Top.currentHeight + padding();
        mPage2Center[1].currentTop = mPage2Center[0].currentTop;
        mPage2Center[2].currentTop = mPage2Center[1].currentTop + mPage2Center[1].currentHeight;
        mPage2Bottom.currentTop = mPage2Center[0].currentTop + mPage2Center[0].currentHeight + padding();

        /** 渐渐减少透明度，隐藏第2页的顶部图，3张模特图，底部图 */
        mPage2Top.alpha((int) (255 - (255 * positionOffset * (1 / SECOND_RATE))));
        mPage2Bottom.alpha((int) (255 - (255 * positionOffset * (1 / SECOND_RATE))));
        for (ViewModel viewModel : mPage2Center) {
            viewModel.alpha((int) (255 - (255 * positionOffset * (1 / SECOND_RATE))));
        }

        /** 因为矩形背景变窄了，所以渐渐减少第2页顶部图，底部图的宽度，实现跟随矩形背景宽度变化 */
        mPage2Top.currentWidth = mRectBgCurrentWidth - padding() * 2;
        mPage2Top.currentLeft = mRectBgCurrentLeft + padding();
        mPage2Bottom.currentWidth = mRectBgCurrentWidth - padding() * 2;
        mPage2Bottom.currentLeft = mRectBgCurrentLeft + padding();
        mPage2Bottom.currentLeft = mRectBgCurrentLeft + padding();

        /** 因为矩形背景变窄了，所以渐渐减少第2，3张模特图的宽高，left和top，实现跟随矩形背景宽度变化 */
        mPage2Center[0].currentLeft = mRectBgCurrentLeft + padding();
        mPage2Center[1].currentWidth = mRectBgCurrentWidth - padding() * 3 - mPage2Center[0].defaultWidth;
        mPage2Center[1].currentHeight = mPage2Center[1].currentWidth;
        mPage2Center[1].currentLeft = mPage2Center[0].currentLeft + mPage2Center[0].defaultWidth + padding();

        mPage2Center[2].currentWidth = mRectBgCurrentWidth - padding() * 3 - mPage2Center[0].defaultWidth;
        mPage2Center[2].currentHeight = mPage2Center[2].currentWidth;
        mPage2Center[2].currentLeft = mPage2Center[0].currentLeft + mPage2Center[0].defaultWidth + padding();
        mPage2Center[2].currentTop = mPage2Center[1].currentTop + mPage2Center[1].currentHeight + padding();
    }

    private void stepByShowPage3Views(float positionOffset) {
        /** 第2页，在50->100%区间偏移，显示第3页，6张模特图 */
        for (int i = 0; i < mPage3Model.length; i++) {
            if (i == 0) {
                /** 第1张模特图先显示 */
                if (mPage3Model[i].paint.getAlpha() < 255) {
                    mPage3Model[i].alpha((int) (255 * (positionOffset - SECOND_RATE) * (1 / (1 - SECOND_RATE))));
                }
            } else {
                /** 其他模特图在前1张显示50%透明度的时候再依次展示 */
                if (mPage3Model[i - 1].paint.getAlpha() >= 255 / 2) {
                    float rate = mPage3Model[i - 1].paint.getAlpha() / 255.0f - 0.5f;
                    mPage3Model[i].alpha((int) (255 * (rate * 2)));
                } else {
                    mPage3Model[i].alpha(0);
                }
            }

            /** 6张模特图，跟随矩形背景上移 */
            if (i < mPage3ModelResources.length / 2) {
                /** 第1排，3张模特图的top计算 */
                mPage3Model[i].currentTop = mRectBgCurrentTop + padding();
            } else {
                /** 第1排，3张模特图的top，需要加上第一排的height */
                mPage3Model[i].currentTop = mRectBgCurrentTop + mPage3ModelDefaultHeight + padding() * 2;
            }
        }
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

    private void hidePage4Views() {
        mPage4Top.alpha(0);

        for (ViewModel viewModel : mPage4Model) {
            viewModel.currentWidth = 0;
            viewModel.currentHeight = 0;
            viewModel.alpha(0);
        }

        for (ViewModel viewModel : mPage4Split) {
            viewModel.alpha(0);
        }
    }

    private void hidePage3Views() {
        for (int i = 0; i < mPage3Model.length; i++) {
            mPage3Model[i].alpha(0);
        }
    }

    private void scaleHidePage3Views() {
        for (int i = 0; i < mPage3Model.length; i++) {
            mPage3Model[i].currentWidth = 0;
            mPage3Model[i].currentHeight = 0;
        }
    }

    private void hidePage1Views() {
        mPage1Top.alpha(0);
        mPage1Bottom.alpha(0);
    }

    private void hidePage2Views() {
        mPage2Top.alpha(0);
        mPage2Bottom.alpha(0);

        for (ViewModel viewModel : mPage2Center) {
            viewModel.alpha(0);
        }

        for (ViewModel viewModel : mPage2Split) {
            viewModel.alpha(0);
        }
    }

    public void drawBitmap(Canvas canvas, ViewModel viewModel) {
        canvas.drawBitmap(viewModel.bitmap, viewModel.currentLeft, viewModel.currentTop, viewModel.paint);
    }

    public void drawBitmapMatrix(Canvas canvas, ViewModel viewModel) {
        canvas.drawBitmap(viewModel.bitmap, viewModel.matrix, viewModel.paint);
    }

    public void drawBitmapRect(Canvas canvas, ViewModel viewModel) {
        RectF rect = new RectF();
        rect.left = viewModel.currentLeft;
        rect.top = viewModel.currentTop;
        rect.right = rect.left + viewModel.currentWidth;
        rect.bottom = rect.top + viewModel.currentHeight;
        canvas.drawBitmap(viewModel.bitmap, null, rect, viewModel.paint);
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

            drawPage1InCanvas(canvas);
            drawPage2InCanvas(canvas);

        } else if (fromPage2ToPage3(mCurrentPageIndex)) {

            /** 绘制第2页，裂变背景图 */
            drawBitmap(canvas, mPage2Split[0]);
            drawBitmap(canvas, mPage2Split[1]);

            /** 绘制矩形背景 */
            drawWhiteRectBackgroud(canvas);

            drawPage2InCanvas(canvas);
            drawPage3InCanvas(canvas);

        } else if (fromPage3ToPage4(mCurrentPageIndex)) {
            /** 绘制第4页，裂变背景图 */
            drawBitmapMatrix(canvas, mPage4Split[0]);
            drawBitmapMatrix(canvas, mPage4Split[1]);

            /** 绘制矩形背景 */
            drawWhiteRectBackgroud(canvas);

            drawPage3InCanvas(canvas);
            drawPage4InCanvas(canvas);

        } else if (isPage4(mCurrentPageIndex)) {

            /** 绘制第4页，裂变背景图 */
            drawBitmapMatrix(canvas, mPage4Split[0]);
            drawBitmapMatrix(canvas, mPage4Split[1]);

            /** 绘制矩形背景 */
            drawWhiteRectBackgroud(canvas);

            drawPage4InCanvas(canvas);
        }
    }

    private void drawPage1InCanvas(Canvas canvas) {
        /** 绘制第1页，顶部模特图 */
        drawBitmap(canvas, mPage1Top);

        /** 绘制第1页，底部文案图 */
        drawBitmap(canvas, mPage1Bottom);
    }

    private void drawPage4InCanvas(Canvas canvas) {
        /** 绘制第4页，顶部模特图 */
        drawBitmap(canvas, mPage4Top);

        /** 绘制第4页，底部3张模特图 */
        for (int i = 0; i < mPage4Model.length; i++) {
            drawBitmapRect(canvas, mPage4Model[i]);
        }
    }

    private void drawPage3InCanvas(Canvas canvas) {
        /** 绘制第3页，6张模特图 */
        for (int i = 0; i < mPage3Model.length; i++) {
            drawBitmapRect(canvas, mPage3Model[i]);
        }
    }

    private void drawPage2InCanvas(Canvas canvas) {
        /** 绘制第2页，顶部图 */
        drawBitmapRect(canvas, mPage2Top);

        /** 绘制第2页，第1张模特图 */
        drawBitmapRect(canvas, mPage2Center[0]);
        /** 在第1张模特图绘制到50%时，绘制第2张模特图 */
        if (mPage2Center[0].currentWidth >= mPage2Center[0].defaultWidth / 2) {
            drawBitmapRect(canvas, mPage2Center[1]);
            /** 在第2张模特图绘制到50%时，绘制第3张模特图 */
            if (mPage2Center[1].currentWidth >= mPage2Center[1].defaultWidth / 2) {
                drawBitmapRect(canvas, mPage2Center[2]);
            }
        }

        /** 绘制第2页，底部图 */
        drawBitmapRect(canvas, mPage2Bottom);
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

    public int getScreenHeight() {
        if (null != getResources() && null != getResources().getDisplayMetrics()) {
            return getResources().getDisplayMetrics().heightPixels;
        }
        return 0;
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
