package com.ezymd.restaurantapp.customviews

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.font.CustomTypeFace

class SnapButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        applyCustomFont(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyCustomFont(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyCustomFont(context, attrs)
    }

    private fun applyCustomFont(context: Context, attrs: AttributeSet?) {
        if (attrs != null && !isInEditMode) {
            val attributeArray = context.obtainStyledAttributes(
                attrs, R.styleable.SnapButton
            )
            try {
                val fontFamily = attributeArray.getInt(R.styleable.SnapButton_buttonFont, 0)
                typeface = selectTypeface(context, fontFamily)
                var drawableLeft: Drawable? = null
                var drawableRight: Drawable? = null
                var drawableBottom: Drawable? = null
                var drawableTop: Drawable? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawableLeft =
                        attributeArray.getDrawable(R.styleable.SnapButton_buttonDrawableLeft)
                    drawableRight =
                        attributeArray.getDrawable(R.styleable.SnapButton_buttonDrawableRight)
                    drawableBottom =
                        attributeArray.getDrawable(R.styleable.SnapButton_buttonDrawableBottom)
                    drawableTop =
                        attributeArray.getDrawable(R.styleable.SnapButton_buttonDrawableTop)
                } else {
                    val drawableLeftId =
                        attributeArray.getResourceId(R.styleable.SnapButton_buttonDrawableLeft, -1)
                    val drawableRightId =
                        attributeArray.getResourceId(R.styleable.SnapButton_buttonDrawableRight, -1)
                    val drawableBottomId = attributeArray.getResourceId(
                        R.styleable.SnapButton_buttonDrawableBottom,
                        -1
                    )
                    val drawableTopId =
                        attributeArray.getResourceId(R.styleable.SnapButton_buttonDrawableTop, -1)

                    if (drawableLeftId != -1)
                        drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId)
                    if (drawableRightId != -1)
                        drawableRight = AppCompatResources.getDrawable(context, drawableRightId)
                    if (drawableBottomId != -1)
                        drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId)
                    if (drawableTopId != -1)
                        drawableTop = AppCompatResources.getDrawable(context, drawableTopId)
                }
                setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    drawableTop,
                    drawableRight,
                    drawableBottom
                )
            } finally {
                attributeArray.recycle()
            }
        }
    }

    private fun selectTypeface(context: Context, fontFamily: Int): Typeface {
        CustomTypeFace.createInstance(context)
        when (fontFamily) {
            0 -> return CustomTypeFace.book
            1 -> return CustomTypeFace.bold
            2 -> return CustomTypeFace.medium
            else -> {

                return CustomTypeFace.roman
            }
        }

    }

}
