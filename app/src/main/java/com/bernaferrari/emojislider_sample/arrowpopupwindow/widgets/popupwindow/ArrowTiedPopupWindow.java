package com.bernaferrari.emojislider_sample.arrowpopupwindow.widgets.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.bernaferrari.emojislider_sample.arrowpopupwindow.utils.Util;


/**
 * Create a arrowed popup window, it can be tied to a view.
 */
public class ArrowTiedPopupWindow extends ArrowPopupWindow {
    private View mTiedView;
    private int tiedViewWidth, tiedViewHeight;
    private int mScreenW, mScreenH;
    private int xOffset, yOffset;
    private int posX, posY;
    private Context mContext;
    private boolean isShownSuccess;
    private int edgeTop, edgeBottom, edgeLeft, edgeRight;
    private TiedDirection mTiedDirection;

    public ArrowTiedPopupWindow(Context context) {
        super(context);
        mContext = context;
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenW = mWindowManager.getDefaultDisplay().getWidth();
        mScreenH = mWindowManager.getDefaultDisplay().getHeight();
    }

    /**
     * Tied this popup window to a view.
     *
     * @param view      the view you want to tie
     * @param direction the direction
     */
    public void setTiedView(View view, TiedDirection direction) {
        mTiedView = view;
        mTiedDirection = direction;
        ArrowDirection mArrowDirection = ArrowDirection.BOTTOM;
        switch (direction) {
            case TOP:
                mArrowDirection = ArrowDirection.BOTTOM;
                break;
            case BOTTOM:
                mArrowDirection = ArrowDirection.TOP;
                break;
            case LEFT:
                mArrowDirection = ArrowDirection.RIGHT;
                break;
            case RIGHT:
                mArrowDirection = ArrowDirection.LEFT;
                break;
        }
        super.setArrowDirection(mArrowDirection);
    }

    /**
     * Set the offset to this popup window
     *
     * @param xoffset x offset(dp)
     * @param yoffset y offset(dp)
     */
    public void setOffset(int xoffset, int yoffset) {
        this.xOffset = Util.DpToPx(mContext, xoffset);
        this.yOffset = Util.DpToPx(mContext, yoffset);
    }

    /**
     * Set the edges to this popup window.
     *
     * @param top    top edge(dp)
     * @param right  right edge(dp)
     * @param bottom bottom edge(dp)
     * @param left   left edge(dp)
     */
    public void setEdge(int top, int right, int bottom, int left) {
        edgeTop = Util.DpToPx(mContext, top);
        edgeRight = Util.DpToPx(mContext, right);
        edgeBottom = Util.DpToPx(mContext, bottom);
        edgeLeft = Util.DpToPx(mContext, left);
    }

    @Override
    public void preShow() {
        super.preShow();
    }

    /**
     * Show this popup window
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        tiedViewWidth = mTiedView.getMeasuredWidth();
        tiedViewHeight = mTiedView.getMeasuredHeight();
        getPosition();
        if (checkShowable() && mTiedView.isShown()) {
            showAtLocation(mTiedView, Gravity.NO_GRAVITY, posX, posY);
            isShownSuccess = true;
        } else {
            isShownSuccess = false;
            dismiss();
        }
    }

    /**
     * Is this popup window show successfully?
     *
     * @return
     */
    public boolean isShownSuccess() {
        return isShownSuccess;
    }

    protected void getPosition() {
        int[] tiedViewLocation = new int[2];
        mTiedView.getLocationOnScreen(tiedViewLocation);

        switch (mTiedDirection) {
            case TOP:
                posX = tiedViewLocation[0] + tiedViewWidth / 2 - arrowPointOffset + xOffset;
                posY = tiedViewLocation[1] - mViewHeight + yOffset;
                break;
            case BOTTOM:
                posX = tiedViewLocation[0] + tiedViewWidth / 2 - arrowPointOffset + xOffset;
                posY = tiedViewLocation[1] + tiedViewHeight + yOffset;
                break;
            case LEFT:
                posX = tiedViewLocation[0] - mViewWidth + xOffset;
                posY = tiedViewLocation[1] + tiedViewHeight / 2 - arrowPointOffset + yOffset;
                break;
            case RIGHT:
                posX = tiedViewLocation[0] + tiedViewWidth + xOffset;
                posY = tiedViewLocation[1] + tiedViewHeight / 2 - arrowPointOffset + yOffset;
                break;
        }
    }

    protected View getTiedView() {
        return mTiedView;
    }

    protected boolean updatePosition() {
        getPosition();
        if (checkShowable() && mTiedView.isShown()) {
            update(posX, posY, mViewWidth, mViewHeight);
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkShowable() {
        if (posY + mViewHeight > mScreenH - edgeBottom || posY < edgeTop) {
            return false;
        }
        return posX >= edgeLeft && posX + mViewWidth <= mScreenW - edgeRight;
    }

    public enum TiedDirection {
        LEFT, RIGHT, TOP, BOTTOM
    }

}
