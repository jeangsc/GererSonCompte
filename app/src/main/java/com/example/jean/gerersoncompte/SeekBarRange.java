package com.example.jean.gerersoncompte;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class SeekBarRange extends View
{
    private final int wMin = 36;
    private final int hMin = 12;
    private final byte FOCUS_NONE = 0;
    private final byte FOCUS_LEFT = 1;
    private final byte FOCUS_RIGHT = 2;

    private Drawable mCursor;
    private Drawable mHighLightRect;
    private Drawable mEmptyRect;

    private byte cursorFocused;

    private float minVal;
    private float maxVal;
    private float posLeft;
    private float posRight;

    private int cursorDimension;
    private int barHeight;
    private int xMin;
    private int xMax;
    private int xLeft;
    private int xRight;
    private int nSteps;

    public SeekBarRange(Context context)
    {
        super(context);
        init(null, 0);
    }

    public SeekBarRange(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public SeekBarRange(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        cursorFocused = FOCUS_NONE;

        mCursor = getResources().getDrawable(R.drawable.seek_bar_range_cursor);
        mHighLightRect = getResources().getDrawable(R.drawable.seek_bar_range_highlight);
        mEmptyRect = getResources().getDrawable(R.drawable.seek_bar_range_empty);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SeekBarRange, defStyle, 0);

        minVal = a.getFloat(R.styleable.SeekBarRange_minVal, 0);
        maxVal = a.getFloat(R.styleable.SeekBarRange_maxVal, 100);
        nSteps = a.getInteger(R.styleable.SeekBarRange_nSteps, 11);
        posLeft = a.getFloat(R.styleable.SeekBarRange_posLeft, minVal);
        posRight = a.getFloat(R.styleable.SeekBarRange_posRight, maxVal);

        a.recycle();

        adjustAttrs();
    }

    private void adjustAttrs()
    {
        if(minVal > maxVal)
        {
            float tmpVal = minVal;
            minVal = maxVal;
            maxVal = tmpVal;
        }
        else if(minVal == maxVal)
        {
            minVal -= 0.5f;
            maxVal += 0.5f;
        }
        if(posLeft < minVal)
            posLeft = minVal;
        else if(posLeft > maxVal)
            posLeft = maxVal;

        if(posRight < minVal)
            posRight = minVal;
        else if(posRight > maxVal)
            posRight = maxVal;

        if(posLeft > posRight)
        {
            float posTmp = posLeft;
            posLeft = posRight;
            posRight = posTmp;
        }
        nSteps = Math.max(2, nSteps);
    }

    private void updateX()
    {
        xLeft = posToX(posLeft);
        xRight = posToX(posRight);
    }

    private void update()
    {
        adjustAttrs();
        updateX();
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        int wMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int hMode = View.MeasureSpec.getMode(heightMeasureSpec);

        int wSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int hSize = View.MeasureSpec.getSize(heightMeasureSpec);

        if(wMode == MeasureSpec.UNSPECIFIED)
            wSize = 0;
        if(hMode == MeasureSpec.UNSPECIFIED)
            hSize = 0;

        wSize = Math.max(wMin, wSize);
        hSize = Math.max(hMin, hSize);

        cursorDimension = hSize;
        barHeight = cursorDimension / 4;

        xMin = cursorDimension / 2;
        xMax = wSize - cursorDimension / 2;

        updateX();

        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //X for highlight bar
        int xlBarH = posToX(posLeft);
        int xrBarH = posToX(posRight);

        //X for left cursor
        int xlCurL = xlBarH - cursorDimension / 2;
        int xrCurL = xlCurL + cursorDimension;

        //X for right cursor
        int xlCurR = xrBarH - cursorDimension / 2;
        int xrCurR = xlCurR + cursorDimension;

        //Y for all bar
        int ytBar = (cursorDimension - barHeight) / 2;
        int ybBar = ytBar + barHeight;

        //Y for all cursor
        int ytCur = 0;
        int ybCur = ytCur + cursorDimension;

        //draw bars first, highlight above empty
        mEmptyRect.setBounds(xMin, ytBar, xMax, ybBar);
        mEmptyRect.draw(canvas);

        mHighLightRect.setBounds(xlBarH, ytBar, xrBarH, ybBar);
        mHighLightRect.draw(canvas);

        //draw left cursor
        mCursor.setBounds(xlCurL, ytCur, xrCurL, ybCur);
        mCursor.draw(canvas);

        //draw right cursor
        mCursor.setBounds(xlCurR, ytCur, xrCurR, ybCur);
        mCursor.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                Log.i("ACTION", "DOWN");
                return checkFocus(event.getX(), event.getY());
            }
            case MotionEvent.ACTION_UP:
            {
                Log.i("ACTION", "UP");
                return processEndCursor();
            }
            case MotionEvent.ACTION_MOVE:
            {
                Log.i("ACTION", "MOVE");
                return processMoveCursor(event.getX(), event.getY());
            }
        }
        return false;
    }

    private boolean checkFocus(float x, float y)
    {
        if(cursorFocused != FOCUS_NONE)
            return false;

        return true;
    }

    private boolean processMoveCursor(float x, float y)
    {
        if(cursorFocused == FOCUS_NONE)
            return false;

        return true;
    }

    private boolean processEndCursor()
    {
        if(cursorFocused == FOCUS_NONE)
            return false;

        cursorFocused = FOCUS_NONE;
        return true;
    }

    public int posToX(float pos)
    {
        return (xMin + (int)((pos - minVal) * (float)(xMax - xMin)/ (maxVal - minVal)));
    }

    public float xToPos(int x)
    {
        return (minVal + (float)(x - xMin) * (maxVal - minVal) / (float)(xMax - xMin));
    }

    public float getMinVal(){
        return minVal;
    }

    public void setMinVal(float minVal){
        this.minVal = minVal;
        update();
    }

    public float getMaxVal() {
        return this.maxVal;
    }

    public void setMaxVal(float maxVal) {
        this.maxVal = maxVal;
        update();
    }

    public float getPosLeft() {
        return this.posLeft;
    }

    public void setPosLeft(float posLeft) {
        this.posLeft = posLeft;
        update();
    }

    public float getPosRight() {
        return this.posRight;
    }

    public void setPosRight(float posRight) {
        this.posRight = posRight;
        update();
    }

    public int getnSteps() {
        return this.nSteps;
    }

    public void setnSteps(int nSteps) {
        this.nSteps = Math.max(2, nSteps);
    }
}
