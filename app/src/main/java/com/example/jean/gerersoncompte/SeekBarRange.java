package com.example.jean.gerersoncompte;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class SeekBarRange extends View
{
    private final int wMin = 36;
    private final int hMin = 12;
    public static final byte FOCUS_CURSOR_NONE = 0;
    public static final byte FOCUS_CURSOR_LEFT = 1;
    public static final byte FOCUS_CURSOR_RIGHT = 2;

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

    private OnChangeListener listener;

    public interface OnChangeListener
    {
        public void onCursorChanged(int cursorFocused);
    }

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
        listener = null;
        cursorFocused = FOCUS_CURSOR_NONE;

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

        adjustVal();
    }

    private void adjustVal()
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
        adjustPos();
    }

    private void adjustPos()
    {
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
            swapFocus();
        }
    }

    private void adjustX()
    {
        if(xLeft < xMin)
            xLeft = xMin;
        else if(xLeft > xMax)
            xLeft = xMax;

        if(xRight < xMin)
            xRight = xMin;
        else if(xRight > xMax)
            xRight = xMax;

        if(xLeft > xRight)
        {
            int xTmp = xLeft;
            xLeft = xRight;
            xRight = xTmp;
            swapFocus();
        }
    }

    private void swapFocus()
    {
        if(cursorFocused == FOCUS_CURSOR_LEFT)
            cursorFocused = FOCUS_CURSOR_RIGHT;
        else if(cursorFocused == FOCUS_CURSOR_RIGHT)
            cursorFocused = FOCUS_CURSOR_LEFT;
    }

    private void updatePos()
    {
        posLeft = xToPos(xLeft);
        posRight = xToPos(xRight);
    }

    private void updateX()
    {
        xLeft = posToX(posLeft);
        xRight = posToX(posRight);
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
        hSize = Math.min(Math.max(hMin, hSize), wSize/2);

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
                return checkFocus((int)event.getX());
            }
            case MotionEvent.ACTION_UP:
            {
                //Log.i("ACTION", "UP");
                return processEndCursor();
            }
            case MotionEvent.ACTION_MOVE:
            {
                return processMoveCursor((int)event.getX());
            }
        }
        return false;
    }

    private boolean checkFocus(int x)
    {
        if(cursorFocused != FOCUS_CURSOR_NONE)
            return false;

        if(x <= xLeft)
            cursorFocused = FOCUS_CURSOR_LEFT;
        else if(x >= xRight)
            cursorFocused = FOCUS_CURSOR_RIGHT;
        else if(x - xLeft < xRight - x)
            cursorFocused = FOCUS_CURSOR_LEFT;
        else
            cursorFocused = FOCUS_CURSOR_RIGHT;

        processMoveCursor(x);
        return true;
    }

    private boolean processMoveCursor(int x)
    {
        if(cursorFocused == FOCUS_CURSOR_NONE)
            return false;

        boolean hasEventCursorChanged = false;
        if(cursorFocused == FOCUS_CURSOR_LEFT)
            hasEventCursorChanged = setxLeft(x);

        else if(cursorFocused == FOCUS_CURSOR_RIGHT)
            hasEventCursorChanged = setxRight(x);

        if(hasEventCursorChanged && listener != null)
            listener.onCursorChanged(cursorFocused);

        return true;
    }

    private boolean processEndCursor()
    {
        if(cursorFocused == FOCUS_CURSOR_NONE)
            return false;

        cursorFocused = FOCUS_CURSOR_NONE;
        return true;
    }

    public int posToX(float pos)
    {
        if(pos == minVal)
            return xMin;

        if(pos == maxVal)
            return xMax;

        return (xMin + Math.round((pos - minVal) * (float)(xMax - xMin)/ (maxVal - minVal)));
    }

    public float xToPos(int x)
    {
        if(x == xMin)
            return minVal;

        if(x == xMax)
            return maxVal;

        return (minVal + (float)(x - xMin) * (maxVal - minVal) / (float)(xMax - xMin));
    }

    private boolean setxLeft(int x)
    {
        if(x == xLeft)
            return false;

        xLeft = Math.min(x, xRight);
        adjustX();
        updatePos();
        invalidate();

        return true;
    }

    private boolean setxRight(int x)
    {
        if(x == xRight)
            return false;

        xRight = Math.max(x, xLeft);
        adjustX();
        updatePos();
        invalidate();

        return true;
    }

    public float getMinVal(){
        return minVal;
    }

    public void setMinVal(float minVal){
        if(this.minVal == minVal)
            return;

        this.minVal = minVal;
        adjustVal();
        updateX();
        invalidate();
    }

    public float getMaxVal() {
        return this.maxVal;
    }

    public void setMaxVal(float maxVal) {
        if(this.maxVal == maxVal)
            return;

        this.maxVal = maxVal;
        adjustVal();
        updateX();
        invalidate();
    }

    public float getPosLeft() {
        return this.posLeft;
    }

    public void setPosLeft(float posLeft) {
        if(this.posLeft == posLeft)
            return;

        this.posLeft = posLeft;
        adjustPos();
        updateX();
        invalidate();
    }

    public float getPosRight() {
        return this.posRight;
    }

    public void setPosRight(float posRight) {
        if(this.posRight == posRight)
            return;

        this.posRight = posRight;
        adjustPos();
        updateX();
        invalidate();
    }

    public int getnSteps() {
        return this.nSteps;
    }

    public void setnSteps(int nSteps) {
        this.nSteps = nSteps;
    }

    public void setOnChangeListener(OnChangeListener listener)
    {
        this.listener = listener;
    }
}
