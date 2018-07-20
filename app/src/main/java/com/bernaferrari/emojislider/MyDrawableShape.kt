package com.bernaferrari.emojislider

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.github.florent37.mylittlecanvas.shape.RectShape

class MyDrawableShape : RectShape() {

    private var drawable: Drawable? = null

    fun setDrawable(drawable: Drawable): MyDrawableShape {
        this.drawable = drawable
        return this
    }

    override fun centerHorizontal(parentWidth: Float): RectShape {
        moveCenterXTo(parentWidth / 2f)
        return super.centerHorizontal(parentWidth)
    }

    override fun draw(canvas: Canvas) {
        drawable!!.draw(canvas)
    }
}