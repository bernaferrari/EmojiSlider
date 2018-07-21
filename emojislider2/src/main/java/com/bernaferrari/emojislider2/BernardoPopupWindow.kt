package com.bernaferrari.emojislider2

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.cpiz.android.bubbleview.*
import com.cpiz.android.bubbleview.R
import com.cpiz.android.bubbleview.RelativePos.CENTER_HORIZONTAL
import com.cpiz.android.bubbleview.Utils.dp2px

class BernardoPopupWindow(contentView: View, private val bubbleView: BubbleStyle) :
    BubblePopupWindow(contentView, bubbleView) {

    private var mPadding = dp2px(2)
    private var mArrowPosDelta = 0
    private var mDelayMillis: Long = 2500

    /**
     * 显示气泡弹窗，并将箭头指向目标
     *
     * @param anchor      气泡箭头对齐的目标
     * @param relativePos 气泡与目标的对齐方式
     */
    override fun showArrowTo(anchor: View, relativePos: RelativePos, marginH: Int, marginV: Int) {
        dismiss()

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val navigationBarHeight = getNavigationBarHeightDelta(anchor)
        val anchorRect = getRectInWindow(anchor)

        contentView.measure(
            View.MeasureSpec.makeMeasureSpec(screenWidth - 2 * mPadding, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(screenHeight - 2 * mPadding, View.MeasureSpec.AT_MOST)
        )
        val contentWidth = contentView.measuredWidth
        val contentHeight = contentView.measuredHeight
//        Log.d(TAG, String.format("w:%d, h:%d", contentWidth, contentHeight))

        val outProp = PopupProp()
        getPopupProp(
            screenWidth,
            screenHeight,
            navigationBarHeight,
            anchorRect,
            contentWidth,
            relativePos,
            0,
            -contentView.context.resources.getDimensionPixelSize(com.bernaferrari.emojislider2.R.dimen.slider_sticker_padding_bottom_without_question),
            mPadding,
            outProp
        )

        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        animationStyle = outProp.animationStyle
        if (contentWidth > outProp.maxWidth) {
            width = outProp.maxWidth
        }
        bubbleView.arrowDirection = outProp.direction
        bubbleView.arrowPosPolicy = BubbleStyle.ArrowPosPolicy.SelfCenter
        bubbleView.arrowTo = anchor
        bubbleView.arrowPosDelta = mArrowPosDelta.toFloat()

        println("location equals: " + outProp.x + " y: " + outProp.y)
        showAtLocation(
            anchor,
            Gravity.CENTER_HORIZONTAL or Gravity.TOP,
            outProp.x + xPadding,
            outProp.y
        )

        if (mDelayMillis > 0) {
            setCancelOnLater(mDelayMillis)
        }
    }


    var xPadding = 0


    private fun getRectInWindow(view: View): Rect {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)
    }

    /**
     * 获得用于补偿位置偏移的 NavigationBar 高度
     * 在 Android5.0 以上系统，showAtLocation 如果使用了 Gravity.BOTTOM 或 Gravity.CENTER_VERTICAL 可能出现显示偏移的Bug
     * 偏移值和 NavigationBar 高度有关
     *
     * @param view 目标View
     * @return 如果需要修正且存在NavigationBar则返回高度，否则为0
     */
    private fun getNavigationBarHeightDelta(view: View): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Utils.getNavigationBarHeight(view)
        } else {
            0
        }
    }

    private inner class PopupProp {
        internal var direction: BubbleStyle.ArrowDirection? = null
        internal var arrowPosPolicy: BubbleStyle.ArrowPosPolicy? = null
        internal var animationStyle: Int = 0
        internal var maxWidth: Int = 0
        internal var gravity: Int = 0
        internal var x: Int = 0
        internal var y: Int = 0
    }


    private fun getPopupPropOfMaxWidth(
        screenWidth: Int,
        anchorRect: Rect,
        relativePos: RelativePos,
        marginH: Int,
        padding: Int,
        outProp: PopupProp
    ) {
        when (relativePos.horizontalRelate) {
            RelativePos.ALIGN_LEFT -> outProp.maxWidth = screenWidth - anchorRect.left - marginH -
                    padding
            RelativePos.TO_RIGHT_OF -> outProp.maxWidth = screenWidth - anchorRect.right - marginH -
                    padding
            RelativePos.TO_LEFT_OF -> outProp.maxWidth = anchorRect.left - marginH - padding
            RelativePos.ALIGN_RIGHT -> outProp.maxWidth = anchorRect.right - marginH - padding
            CENTER_HORIZONTAL -> outProp.maxWidth = screenWidth - 2 * padding
        }
    }

    private fun getPopupPropOfX(
        screenWidth: Int,
        anchorRect: Rect,
        contentWidth: Int,
        relativePos: RelativePos,
        marginH: Int,
        padding: Int,
        outProp: PopupProp
    ) {
        when (relativePos.horizontalRelate) {
            RelativePos.ALIGN_LEFT -> {
                outProp.gravity = outProp.gravity or Gravity.LEFT
                outProp.x = anchorRect.left + marginH
            }
            RelativePos.TO_RIGHT_OF -> {
                outProp.gravity = outProp.gravity or Gravity.LEFT
                outProp.x = anchorRect.right + marginH
            }
            RelativePos.TO_LEFT_OF -> {
                outProp.gravity = outProp.gravity or Gravity.RIGHT
                outProp.x = screenWidth - anchorRect.left + marginH
            }
            RelativePos.ALIGN_RIGHT -> {
                outProp.gravity = outProp.gravity or Gravity.RIGHT
                outProp.x = screenWidth - anchorRect.right + marginH
            }
            CENTER_HORIZONTAL -> when {
                anchorRect.centerX() < contentWidth / 2 + padding -> {
                    outProp.gravity = outProp.gravity or Gravity.LEFT
                    outProp.x = padding
                }
                screenWidth - anchorRect.centerX() < contentWidth / 2 + padding -> {
                    outProp.gravity = outProp.gravity or Gravity.RIGHT
                    outProp.x = padding
                }
                else -> {
                    outProp.gravity = Gravity.CENTER_HORIZONTAL
                    outProp.x = anchorRect.centerX() - screenWidth / 2
                }
            }
        }
    }

    private fun getPopupProp(
        screenWidth: Int,
        screenHeight: Int,
        navigationBarHeight: Int,
        anchorRect: Rect,
        contentWidth: Int,
        relativePos: RelativePos,
        marginH: Int,
        marginV: Int,
        padding: Int,
        outProp: PopupProp
    ) {
        outProp.direction = relativePos.arrowDirection
        outProp.animationStyle = getAnimationStyle(outProp.direction!!)
        outProp.gravity = 0
        getPopupPropOfX(
            screenWidth,
            anchorRect,
            contentWidth,
            relativePos,
            marginH,
            padding,
            outProp
        )
        getPopupPropOfMaxWidth(screenWidth, anchorRect, relativePos, marginH, padding, outProp)
        getPopupPropOfY(
            screenHeight,
            navigationBarHeight,
            anchorRect,
            relativePos,
            marginV,
            outProp
        )

        when (outProp.direction) {
            BubbleStyle.ArrowDirection.Up, BubbleStyle.ArrowDirection.Down -> when (relativePos.horizontalRelate) {
                RelativePos.CENTER_HORIZONTAL -> outProp.arrowPosPolicy =
                        BubbleStyle.ArrowPosPolicy.TargetCenter
                RelativePos.ALIGN_LEFT -> outProp.arrowPosPolicy =
                        BubbleStyle.ArrowPosPolicy.SelfBegin
                RelativePos.ALIGN_RIGHT -> outProp.arrowPosPolicy =
                        BubbleStyle.ArrowPosPolicy.SelfEnd
                else -> outProp.arrowPosPolicy = BubbleStyle.ArrowPosPolicy.TargetCenter
            }
            BubbleStyle.ArrowDirection.Left, BubbleStyle.ArrowDirection.Right -> when (relativePos.verticalRelate) {
                RelativePos.CENTER_HORIZONTAL -> outProp.arrowPosPolicy =
                        BubbleStyle.ArrowPosPolicy.TargetCenter
                RelativePos.ALIGN_TOP -> outProp.arrowPosPolicy =
                        BubbleStyle.ArrowPosPolicy.SelfBegin
                RelativePos.ALIGN_BOTTOM -> outProp.arrowPosPolicy =
                        BubbleStyle.ArrowPosPolicy.SelfEnd
                else -> outProp.arrowPosPolicy = BubbleStyle.ArrowPosPolicy.TargetCenter
            }
            else -> outProp.arrowPosPolicy = BubbleStyle.ArrowPosPolicy.TargetCenter
        }
    }

    private fun getPopupPropOfY(
        screenHeight: Int,
        navigationBarHeight: Int,
        anchorRect: Rect,
        relativePos: RelativePos,
        marginV: Int,
        outProp: PopupProp
    ) {
        when (relativePos.verticalRelate) {
            RelativePos.ALIGN_TOP -> {
                outProp.gravity = outProp.gravity or Gravity.TOP
                outProp.y = anchorRect.top + marginV
            }
            RelativePos.BELOW -> {
                outProp.gravity = outProp.gravity or Gravity.TOP
                outProp.y = anchorRect.bottom + marginV
            }
            RelativePos.ALIGN_BOTTOM -> {
                outProp.gravity = outProp.gravity or Gravity.BOTTOM
                outProp.y = screenHeight + navigationBarHeight - anchorRect.bottom + marginV
            }
            RelativePos.ABOVE -> {
                outProp.gravity = outProp.gravity or Gravity.BOTTOM
                outProp.y = screenHeight + navigationBarHeight - anchorRect.top + marginV
            }
            RelativePos.CENTER_VERTICAL -> {
                outProp.gravity = outProp.gravity or Gravity.CENTER_VERTICAL
                outProp.y = anchorRect.centerY() - navigationBarHeight / 2 - screenHeight / 2
            }
        }
    }

    private fun getAnimationStyle(direction: BubbleStyle.ArrowDirection): Int {
        when (direction) {
            BubbleStyle.ArrowDirection.Up -> return R.style.AnimationArrowUp
            BubbleStyle.ArrowDirection.Down -> return R.style.AnimationArrowDown
            BubbleStyle.ArrowDirection.Left -> return R.style.AnimationArrowLeft
            BubbleStyle.ArrowDirection.Right -> return R.style.AnimationArrowRight
            else -> return R.style.AnimationArrowNone
        }
    }
}