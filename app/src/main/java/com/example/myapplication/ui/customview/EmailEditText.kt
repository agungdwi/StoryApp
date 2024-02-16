package com.example.myapplication.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class EmailEditText: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                error =
                    if (s.isNotEmpty()) {
                        if (!s.toString().matches(emailPattern)) {
                            context.getString(R.string.email_error)
                        } else null
                    }else null
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error =
                    if (s.isNotEmpty()) {
                        if (!s.toString().matches(emailPattern)) {
                            context.getString(R.string.email_error)
                        } else null
                }else null

                background = if(s.isEmpty() or !s.toString().matches(emailPattern)){
                    ContextCompat.getDrawable(context, R.drawable.error_edit_text)
                }else{
                    ContextCompat.getDrawable(context, R.drawable.custom_edit_text)
                }

            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.



            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        maxLines = 1
    }


    companion object{
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    }

}