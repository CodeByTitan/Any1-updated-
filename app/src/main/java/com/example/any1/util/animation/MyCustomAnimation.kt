package com.example.any1.util.animation

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout


class MyCustomAnimation(view: View, duration: Int, type: Int) :
    Animation() {
    private val mView: View
    private var mEndHeight: Int
    private val mType: Int
    private val mLayoutParams: LinearLayout.LayoutParams
    var height: Int
        get() = mView.height
        set(height) {
            mEndHeight = height
        }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (interpolatedTime < 1.0f) {
            if (mType == EXPAND) {
                mLayoutParams.height = (mEndHeight * interpolatedTime).toInt()
            } else {
                mLayoutParams.height = (mEndHeight * (1 - interpolatedTime)).toInt()
            }
            mView.requestLayout()
        } else {
            if (mType == EXPAND) {
                mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                mView.requestLayout()
            } else {
                mView.visibility = View.GONE
            }
        }
    }

    companion object {
        const val COLLAPSE = 1
        const val EXPAND = 0
    }

    init {
        setDuration(duration.toLong())
        mView = view
        mEndHeight = mView.height
        mLayoutParams = view.layoutParams as LinearLayout.LayoutParams
        mType = type
        if (mType == EXPAND) {
            mLayoutParams.height = 0
        } else {
            mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        view.visibility = View.VISIBLE
    }
}