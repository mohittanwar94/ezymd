package com.ezymd.restaurantapp.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;

import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.font.CustomTypeFace;

public class SnapTextView extends AppCompatTextView {

    private boolean isJustifyEnabled;
    private boolean mAllCaps;
    private int mFirstLineTextHeight = 0;
    private Rect mLineBounds = new Rect();
    public SnapTextView(Context context) {
        super(context);
        applyCustomFont(context, null);
    }

    public SnapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public SnapTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, attrs);
    }

    public void applyCustomFont(Context context, AttributeSet attrs) {
        if (attrs != null && !isInEditMode()) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.SnapTextView);
            try {
                int fontFamily = attributeArray.getInt(R.styleable.SnapTextView_textFont, 0);
                isJustifyEnabled = attributeArray.getBoolean(R.styleable.SnapTextView_isJustifyEnabled, false);
                mAllCaps = attrs.getAttributeBooleanValue(android.R.attr.textAllCaps, false);
                setTypeface(selectTypeface(context, fontFamily));
                Drawable drawableLeft = null;
                Drawable drawableRight = null;
                Drawable drawableBottom = null;
                Drawable drawableTop = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawableLeft = attributeArray.getDrawable(R.styleable.SnapTextView_drawableLeftCompat);
                    drawableRight = attributeArray.getDrawable(R.styleable.SnapTextView_drawableRightCompat);
                    drawableBottom = attributeArray.getDrawable(R.styleable.SnapTextView_drawableBottomCompat);
                    drawableTop = attributeArray.getDrawable(R.styleable.SnapTextView_drawableTopCompat);
                } else {
                    final int drawableLeftId = attributeArray.getResourceId(R.styleable.SnapTextView_drawableLeftCompat, -1);
                    final int drawableRightId = attributeArray.getResourceId(R.styleable.SnapTextView_drawableRightCompat, -1);
                    final int drawableBottomId = attributeArray.getResourceId(R.styleable.SnapTextView_drawableBottomCompat, -1);
                    final int drawableTopId = attributeArray.getResourceId(R.styleable.SnapTextView_drawableTopCompat, -1);

                    if (drawableLeftId != -1)
                        drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId);
                    if (drawableRightId != -1)
                        drawableRight = AppCompatResources.getDrawable(context, drawableRightId);
                    if (drawableBottomId != -1)
                        drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
                    if (drawableTopId != -1)
                        drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
                }
                setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
                //getEmojiTextViewHelper().updateTransformationMethod();
            } finally {
                attributeArray.recycle();
            }
        }
    }
    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(filters);
      //  super.setFilters(getEmojiTextViewHelper().getFilters(filters));
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
      //  getEmojiTextViewHelper().setAllCaps(allCaps);
    }
    private Typeface selectTypeface(Context context, int fontFamily) {
        CustomTypeFace.createInstance(context);
        switch (fontFamily) {
            case 0:
                return CustomTypeFace.book;
            case 1:
                return CustomTypeFace.bold;
            case 2:
                return CustomTypeFace.medium;
            case 3:
                return CustomTypeFace.roman;
            default:
                break;
        }
        return CustomTypeFace.book;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isJustifyEnabled || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onDraw(canvas);
            return;
        }
        getPaint().setColor(getCurrentTextColor());
        getPaint().drawableState = getDrawableState();

        String fullText = mAllCaps ? getText().toString().toUpperCase() : getText().toString();

        float lineSpacing = getLineHeight();
        float drawableWidth = getDrawableWidth();

        int lineNum = 1, lineStartIndex = 0;
        int lastWordEnd, currWordEnd = 0;

        if (fullText.indexOf(' ') == -1)
            flushWord(canvas, getPaddingTop() + lineSpacing, fullText);
        else {
            while (currWordEnd >= 0) {
                lastWordEnd = currWordEnd + 1;
                currWordEnd = fullText.indexOf(' ', lastWordEnd);

                if (currWordEnd != -1) {
                    getPaint().getTextBounds(fullText, lineStartIndex, currWordEnd, mLineBounds);

                    if (mLineBounds.width() >= drawableWidth) {
                        flushLine(canvas, lineNum, fullText.substring(lineStartIndex, lastWordEnd));
                        lineStartIndex = lastWordEnd;
                        lineNum++;
                    }

                } else {
                    getPaint().getTextBounds(fullText, lineStartIndex, fullText.length(), mLineBounds);

                    if (mLineBounds.width() >= drawableWidth) {
                        flushLine(canvas, lineNum, fullText.substring(lineStartIndex, lastWordEnd));
                        rawFlushLine(canvas, ++lineNum, fullText.substring(lastWordEnd));
                    } else {
                        if (lineNum == 1) {
                            rawFlushLine(canvas, lineNum, fullText);
                        } else {
                            rawFlushLine(canvas, lineNum, fullText.substring(lineStartIndex));
                        }
                    }
                }

            }
        }
    }

    private float getDrawableWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void setFirstLineTextHeight(String firstLine) {
        getPaint().getTextBounds(firstLine, 0, firstLine.length(), mLineBounds);
        mFirstLineTextHeight = mLineBounds.height();
    }

    private void rawFlushLine(Canvas canvas, int lineNum, String line) {
        if (lineNum == 1) setFirstLineTextHeight(line);

        float yLine = getPaddingTop() + mFirstLineTextHeight + (lineNum - 1) * getLineHeight();
        canvas.drawText(line, getPaddingLeft(), yLine, getPaint());
    }

    private void flushLine(Canvas canvas, int lineNum, String line) {
        if (lineNum == 1) setFirstLineTextHeight(line);

        float yLine = getPaddingTop() + mFirstLineTextHeight + (lineNum - 1) * getLineHeight();

        String[] words = line.split("\\s+");
        StringBuilder lineBuilder = new StringBuilder();

        for (String word : words) {
            lineBuilder.append(word);
        }

        float xStart = getPaddingLeft();
        float wordWidth = getPaint().measureText(lineBuilder.toString());
        float spacingWidth = (getDrawableWidth() - wordWidth) / (words.length - 1);

        for (String word : words) {
            canvas.drawText(word, xStart, yLine, getPaint());
            xStart += getPaint().measureText(word) + spacingWidth;
        }
    }

    private void flushWord(Canvas canvas, float yLine, String word) {
        float xStart = getPaddingLeft();
        float wordWidth = getPaint().measureText(word);
        float spacingWidth = (getDrawableWidth() - wordWidth) / (word.length() - 1);

        for (int i = 0; i < word.length(); i++) {
            canvas.drawText(word, i, i + 1, xStart, yLine, getPaint());
            xStart += getPaint().measureText(word, i, i + 1) + spacingWidth;
        }
    }
   /* private EmojiTextViewHelper getEmojiTextViewHelper() {
        if (mEmojiTextViewHelper == null) {
            mEmojiTextViewHelper = new EmojiTextViewHelper(this);
        }
        return mEmojiTextViewHelper;
    }*/
}
