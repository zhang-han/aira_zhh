/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mapscloud.download.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.mapscloud.download.R;


public class HorizontalProgressBarWithNumber extends ProgressBar {
  private static final int DEFAULT_TEXT_SIZE = 10;
  private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#2774F4");//0XFFFC00D1
  private static final int DEFAULT_COLOR_UNREACHED_COLOR = Color.parseColor("#00000000");; //0xFFd3d6da;
  private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
  private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
  private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
  /**
   * painter of all drawing things
   */
  protected Paint mPaint = new Paint();
  /**
   * color of progress number
   */
  protected int mTextColor = DEFAULT_TEXT_COLOR;
  /**
   * size of text (sp)
   */
  protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
  /**
   * offset of draw progress
   */
  protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);
  /**
   * height of reached progress bar
   */
  protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
  /**
   * color of reached bar
   */
  protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
  /**
   * color of unreached bar
   */
  protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
  /**
   * height of unreached progress bar
   */
  protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
  /**
   * view width except padding
   */
  protected int mRealWidth;
  protected boolean mIfDrawText = true;
  protected static final int VISIBLE = 0;

  public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    obtainStyledAttributes(attrs);
    mPaint.setTextSize(mTextSize);
    mPaint.setColor(mTextColor);
  }

  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = measureHeight(heightMeasureSpec);
    setMeasuredDimension(width, height);
    mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
  }

  private int measureHeight(int measureSpec) {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      float textHeight = (mPaint.descent() - mPaint.ascent());
      result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(
          Math.max(mReachedProgressBarHeight, mUnReachedProgressBarHeight), Math.abs(textHeight)));
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }

  /**
   * get the styled attributes
   */
  private void obtainStyledAttributes(AttributeSet attrs) {
    // init values from custom attributes
    final TypedArray attributes =
        getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWithNumber);
    mTextColor =
        attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_text_color_a,
            DEFAULT_TEXT_COLOR);
    mTextSize = (int) attributes.getDimension(
        R.styleable.HorizontalProgressBarWithNumber_progress_text_size_a, mTextSize);
    mReachedBarColor =
        attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_reached_color_a,
            mTextColor);
    mUnReachedBarColor =
        attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color_a,
            DEFAULT_COLOR_UNREACHED_COLOR);
    mReachedProgressBarHeight = (int) attributes.getDimension(
        R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height_a,
        mReachedProgressBarHeight);
    mUnReachedProgressBarHeight = (int) attributes.getDimension(
        R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height_a,
        mUnReachedProgressBarHeight);
    mTextOffset = (int) attributes.getDimension(
        R.styleable.HorizontalProgressBarWithNumber_progress_text_offset_a, mTextOffset);
    int textVisible =
        attributes.getInt(R.styleable.HorizontalProgressBarWithNumber_progress_text_visibility_a,
            VISIBLE);
    if (textVisible != VISIBLE) {
      mIfDrawText = false;
    }
    attributes.recycle();
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    canvas.save();
    canvas.translate(getPaddingLeft(), getHeight() / 2);
    boolean noNeedBg = false;
    float radio = getProgress() * 1.0f / getMax();
    float progressPosX = (int) (mRealWidth * radio);
    String text = getProgress() + "%";
    // mPaint.getTextBounds(text, 0, text.length(), mTextBound);
    float textWidth = mPaint.measureText(text);
    float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
    if (progressPosX + textWidth > mRealWidth) {
      progressPosX = mRealWidth - textWidth;
      noNeedBg = true;
    }
    // draw reached bar
    float endX = progressPosX - mTextOffset / 2;
    if (endX > 0) {
      mPaint.setColor(mReachedBarColor);
      mPaint.setStrokeWidth(mReachedProgressBarHeight);
      canvas.drawLine(0, 0, endX, 0, mPaint);
    }
    // draw progress bar
    // measure text bound
    if (mIfDrawText) {
      mPaint.setColor(mTextColor);
      canvas.drawText(text, progressPosX, -textHeight, mPaint);
    }
    // draw unreached bar
    if (!noNeedBg) {
      float start = progressPosX + mTextOffset / 2 + textWidth;
      mPaint.setColor(mUnReachedBarColor);
      mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
      canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
    }
    canvas.restore();
  }

  /**
   * dp 2 px
   */
  protected int dp2px(int dpVal) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
        getResources().getDisplayMetrics());
  }

  /**
   * sp 2 px
   */
  protected int sp2px(int spVal) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
        getResources().getDisplayMetrics());
  }

  public int getmTextColor() {
    return mTextColor;
  }

  public void setmTextColor(int mTextColor) {
    this.mTextColor = mTextColor;
  }

  public int getmReachedBarColor() {
    return mReachedBarColor;
  }

  public void setmReachedBarColor(int mReachedBarColor) {
    this.mReachedBarColor = mReachedBarColor;
  }
}