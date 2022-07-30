package com.iqbalmungid.mystory.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.iqbalmungid.mystory.R

class CustomEditPass: TextInputEditText, View.OnTouchListener {
    private lateinit var visibleOffImg: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        showVisibleButton()
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
    private fun init() {
        visibleOffImg = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showVisibleButton() else hideVisibleButton()
            }
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length < 6) showError()
            }
        })
    }

    private fun showError() {
        error = context.getString(R.string.error_pass)
    }

    private fun showVisibleButton() {
        setButtonDrawables(endOfTheText = visibleOffImg)
    }
    private fun hideVisibleButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val visibleButtonStart: Float
            val visibleButtonEnd: Float
            var isVisibleButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                visibleButtonEnd = (visibleOffImg.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < visibleButtonEnd -> isVisibleButtonClicked = true
                }
            } else {
                visibleButtonStart = ( width - paddingEnd - visibleOffImg.intrinsicWidth).toFloat()
                when {
                    event.x > visibleButtonStart -> isVisibleButtonClicked = true
                }
            }

            if (isVisibleButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hideVisibleButton()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod = PasswordTransformationMethod.getInstance()
                            visibleOffImg = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable
                            showVisibleButton()
                        } else {
                            transformationMethod = HideReturnsTransformationMethod.getInstance()
                            visibleOffImg = ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
                            showVisibleButton()
                        }
                        return true
                    }
                    else -> return false
                }
            }else return false
        }
        return false
    }
}