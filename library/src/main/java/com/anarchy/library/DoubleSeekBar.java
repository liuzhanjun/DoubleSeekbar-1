package com.anarchy.library;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;


public class DoubleSeekBar extends View {

    private static final float THUMB_SHADOW = 3.5f;
    private static final float Y_OFFSET = 1.75f;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final float HIT_SCOPE_RATIO = 1.2f;

    private int mThumbRadius;
    private int mThumbColor;
    private int mProgressColor;
    private int mProgressBackgroundColor;
    private int mProgressWidth;
    private int mTextColor;
    private int mTextSize;


    private Point mStartPoint = new Point();//progress 起点
    private float mFirstThumbRatio = 0.0f;
    private float mSecondThumbRatio = 1.0f;

    private RectF mFirstThumb = new RectF();
    private RectF mSecondThumb = new RectF();


    private int mProgressLength;
    private int mThumbShadowRadius;
    private int mYOffset;


    private String mFirstPrompt;
    private String mSecondPrompt;


    private Paint mProgressPaint = new Paint();
    private Paint mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private onSeekBarChangeListener mOnSeekBarChangeListener;

    public DoubleSeekBar(Context context) {
        this(context, null, 0);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DoubleSeekBar, defStyleAttr, R.style.DoubleSeekBar);
        mThumbRadius = a.getDimensionPixelSize(R.styleable.DoubleSeekBar_DB_ThumbRadius, 0);
        mThumbColor = a.getColor(R.styleable.DoubleSeekBar_DB_ThumbColor, 0);
        mTextColor = a.getColor(R.styleable.DoubleSeekBar_DB_TextColor, 0);
        mProgressColor = a.getColor(R.styleable.DoubleSeekBar_DB_ProgressColor, 0);
        mProgressBackgroundColor = a.getColor(R.styleable.DoubleSeekBar_DB_ProgressBackgroundColor, 0);
        mTextSize = a.getDimensionPixelSize(R.styleable.DoubleSeekBar_DB_TextSize, 0);
        mProgressWidth = a.getDimensionPixelOffset(R.styleable.DoubleSeekBar_DB_ProgressWidth, 0);
        a.recycle();
        //初始化画笔
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mThumbShadowRadius = (int) (getResources().getDisplayMetrics().density * THUMB_SHADOW);
        mYOffset = (int) (getResources().getDisplayMetrics().density * Y_OFFSET);
        if(!isInEditMode()) {
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, mThumbPaint);
            mThumbPaint.setShadowLayer(mThumbShadowRadius, 0, mYOffset, KEY_SHADOW_COLOR);
        }
        mThumbPaint.setColor(mThumbColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //just care height
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int suggestHeight = (int) (3 * mThumbRadius + mThumbShadowRadius - fontMetrics.top + fontMetrics.bottom + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSizeAndState(suggestHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mStartPoint.set(getPaddingLeft() + mThumbRadius, h - getPaddingBottom() - mThumbRadius - mThumbShadowRadius);
        mProgressLength = w - getPaddingLeft() - getPaddingRight() - 2 * mThumbRadius;

        mFirstThumb.set(mStartPoint.x + mProgressLength * mFirstThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                mStartPoint.x + mProgressLength * mFirstThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);

        mSecondThumb.set(mStartPoint.x + mProgressLength * mSecondThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                mStartPoint.x + mProgressLength * mSecondThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int y = mStartPoint.y;
        int firstX = mStartPoint.x;
        int secondX = (int) mFirstThumb.centerX();
        int thirdX = (int) mSecondThumb.centerX();
        //draw progress
        mProgressPaint.setColor(mProgressBackgroundColor);
        canvas.drawLine(firstX, y, secondX, y, mProgressPaint);
        mProgressPaint.setColor(mProgressColor);
        canvas.drawLine(secondX, y, thirdX, y, mProgressPaint);
        mProgressPaint.setColor(mProgressBackgroundColor);
        canvas.drawLine(thirdX, y, firstX + mProgressLength, y, mProgressPaint);

        //draw Thumb
        canvas.drawCircle(secondX, y, mThumbRadius, mThumbPaint);
        canvas.drawCircle(thirdX, y, mThumbRadius, mThumbPaint);

        mFirstPrompt = ratio2DateString(mFirstThumbRatio);
        mSecondPrompt = ratio2DateString(mSecondThumbRatio);
        //draw prompt text
        if (mFirstThumbRatio < 0.1) {//设置align left
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mFirstPrompt, mFirstThumb.left, mFirstThumb.top - mThumbRadius, mTextPaint);
        } else if (mSecondThumbRatio - mFirstThumbRatio < 0.2f) {
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(mFirstPrompt, mFirstThumb.right, mFirstThumb.top - mThumbRadius, mTextPaint);
        } else {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mFirstPrompt, mFirstThumb.centerX(), mFirstThumb.top - mThumbRadius, mTextPaint);
        }


        if (mSecondThumbRatio > 0.9) {//设置align right
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(mSecondPrompt, mSecondThumb.right, mSecondThumb.top - mThumbRadius, mTextPaint);
        } else if (mSecondThumbRatio - mFirstThumbRatio < 0.2f) {
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mSecondPrompt, mSecondThumb.left, mSecondThumb.top - mThumbRadius, mTextPaint);
        } else {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mSecondPrompt, mSecondThumb.centerX(), mSecondThumb.top - mThumbRadius, mTextPaint);
        }

    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.fistThumbRatio = mFirstThumbRatio;
        ss.secondThumbRatio = mSecondThumbRatio;
        return ss;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            mFirstThumbRatio = ((SavedState) state).fistThumbRatio;
            mSecondThumbRatio = ((SavedState) state).secondThumbRatio;
            super.onRestoreInstanceState(((SavedState) state).getSuperState());
        }else {
            super.onRestoreInstanceState(state);
        }
    }

    private static final int INVALID_POINTER = -1;

    static class SelectInfo {
        int pointerId = INVALID_POINTER;
        boolean isCaptured = false;

        void invalid() {
            pointerId = INVALID_POINTER;
            isCaptured = false;
        }
    }

    private SelectInfo firstInfo = new SelectInfo();
    private SelectInfo secondInfo = new SelectInfo();

    /**
     * reset to default state
     */
    public void reset(){
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(this,mFirstThumbProperty,new float[]{0.0f}))
                .with(ObjectAnimator.ofFloat(this,mSecondThumbProperty,new float[]{1.0f}));
        set.setDuration(200);
        set.start();
    }
    /**
     * get first thumb prompt
     * @return
     */
    public int getBeginTime(){
        return (int) (mFirstThumbRatio*24);
    }


    public void setBeginTime(int beginTime){
        mFirstThumbRatio = beginTime/24f;
    }


    public void setEndTime(int endTime){
        mSecondThumbRatio = endTime/24f;
    }

    /**
     * get second thumb prompt
     * @return
     */
    public int getEndTime(){
        return (int) (mSecondThumbRatio*24);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int firstPointerId = MotionEventCompat.getPointerId(event, 0);
                if (mFirstThumb.contains(x, y)) {
                    firstInfo.pointerId = firstPointerId;
                    firstInfo.isCaptured = true;
                }
                if (mSecondThumb.contains(x, y)) {
                    secondInfo.pointerId = firstPointerId;
                    secondInfo.isCaptured = true;
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = MotionEventCompat.getActionIndex(event);
                float otherX = MotionEventCompat.getX(event, index);
                float otherY = MotionEventCompat.getY(event, index);
                if (mFirstThumb.contains(otherX, otherY)) {
                    firstInfo.isCaptured = true;
                    firstInfo.pointerId = MotionEventCompat.getPointerId(event, index);
                }
                if (mSecondThumb.contains(otherX, otherY)) {
                    secondInfo.isCaptured = true;
                    secondInfo.pointerId = MotionEventCompat.getPointerId(event, index);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (firstInfo.isCaptured) {
                    int i = MotionEventCompat.findPointerIndex(event, firstInfo.pointerId);
                    float firstX = MotionEventCompat.getX(event, i);
                    if (moveFirstThumb(firstX)) return false;
                }

                if (secondInfo.isCaptured) {
                    int i = MotionEventCompat.findPointerIndex(event, secondInfo.pointerId);
                    float secondX = MotionEventCompat.getX(event, i);
                    if (moveSecondThumb(secondX)) return false;
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                int pointerId = MotionEventCompat.getPointerId(event, MotionEventCompat.getActionIndex(event));
                if (firstInfo.pointerId == pointerId) {
                    firstInfo.invalid();
                }
                if (secondInfo.pointerId == pointerId) {
                    secondInfo.invalid();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                firstInfo.invalid();
                secondInfo.invalid();
                break;
        }
        if (firstInfo.isCaptured || secondInfo.isCaptured) {
            if(mOnSeekBarChangeListener!=null){
                mOnSeekBarChangeListener.onProgressChanged(this,mFirstThumbRatio,mSecondThumbRatio);
            }
            invalidate();
        }
        if(ViewCompat.isAttachedToWindow(this)){
            getParent().requestDisallowInterceptTouchEvent(firstInfo.isCaptured || secondInfo.isCaptured);
        }
        return firstInfo.isCaptured || secondInfo.isCaptured;
    }

    private boolean moveSecondThumb(float x) {
        if (x > mFirstThumb.right + mThumbRadius) {
            mSecondThumbRatio = (x - mStartPoint.x) / mProgressLength;
            if (mSecondThumbRatio > 1) {
                mSecondThumbRatio = 1;
                return true;
            }
            float offsetX = x - mSecondThumb.centerX();
            mSecondThumb.offset(offsetX, 0);
        }
        return false;
    }

    private boolean moveFirstThumb(float x) {
        if (x < mSecondThumb.left - mThumbRadius) {//可以移动的范围
            mFirstThumbRatio = (x - mStartPoint.x) / mProgressLength;
            if (mFirstThumbRatio < 0) {
                mFirstThumbRatio = 0f;
                return true;
            }
            float offsetX = x - mFirstThumb.centerX();
            mFirstThumb.offset(offsetX, 0);
        }
        return false;
    }

    /**
     * override this method
     * custom own tip text
     * @param ratio firstThumb and secondThumb range 0.0-1.0;
     * @return
     */
    protected String ratio2DateString(float ratio) {
        StringBuilder builder = new StringBuilder();
        int currentMinute = (int) (24 * 60 * ratio);
        String hour = String.valueOf(currentMinute / 60);
        if (hour.length() == 1) {
            builder.append("0").append(hour);
        } else {
            builder.append(hour);
        }
        builder.append(":");
        String minute = String.valueOf(currentMinute % 60);
        if (minute.length() == 1) {
            builder.append("0").append(minute);
        } else {
            builder.append(minute);
        }
        return builder.toString();
    }

    public void setOnSeekBarChangeListener(onSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface onSeekBarChangeListener {
        void onProgressChanged(DoubleSeekBar doubleSeekBar,float firstThumbRatio,float secondThumbRatio);
    }

    /**
     * set first thumb position
     * @param ratio
     */
    public void setFirstThumbRatio(float ratio){
        mFirstThumbRatio = ratio;
        invalidate();
    }

    /**
     * set second thumb position
     * @param ratio
     */
    public void setSecondThumbRatio(float ratio){
        mSecondThumbRatio = ratio;
        invalidate();
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        float fistThumbRatio;
        float secondThumbRatio;

        public SavedState(Parcel source) {
            super(source);
            fistThumbRatio = source.readFloat();
            secondThumbRatio = source.readFloat();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(fistThumbRatio);
            out.writeFloat(secondThumbRatio);
        }
    }
    private Property<DoubleSeekBar,Float> mFirstThumbProperty = new Property<DoubleSeekBar, Float>(Float.class,"firstThumbRatio") {
        @Override
        public Float get(DoubleSeekBar object) {
            return object.mFirstThumbRatio;
        }

        @Override
        public void set(DoubleSeekBar object, Float value) {
            object.mFirstThumbRatio = value;
            object.mFirstThumb.set(mStartPoint.x + mProgressLength * mFirstThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                    mStartPoint.x + mProgressLength * mFirstThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);
            object.invalidate();
        }
    };
    private Property<DoubleSeekBar,Float> mSecondThumbProperty = new Property<DoubleSeekBar, Float>(Float.class,"secondThumbRatio") {
        @Override
        public Float get(DoubleSeekBar object) {
            return object.mSecondThumbRatio;
        }

        @Override
        public void set(DoubleSeekBar object, Float value) {
            object.mSecondThumbRatio = value;
            object.mSecondThumb.set(mStartPoint.x + mProgressLength * mSecondThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                    mStartPoint.x + mProgressLength * mSecondThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);
        }
    };
}
