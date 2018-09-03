package com.greenshadow.costbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greenshadow.costbook.utils.Log;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class DefaultFabBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimating = false;
    private boolean mIsFabVisible = true;

    public DefaultFabBehavior() {
        super();
    }

    public DefaultFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
            FloatingActionButton fab, View directTargetChild, View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, fab, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout cl, FloatingActionButton child, View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(cl, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (mIsAnimating) {
            return;
        }

        if (dyConsumed > 0 && mIsFabVisible) {
            animateOut(child);
        } else if (dyConsumed < 0 && !mIsFabVisible) {
            animateIn(child);
        }
    }

    private void animateOut(FloatingActionButton fab) {
        ViewCompat.animate(fab)
                .translationY(200)
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setInterpolator(INTERPOLATOR)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        mIsAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                        mIsFabVisible = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        mIsAnimating = false;
                        mIsFabVisible = true;
                    }
                })
                .withLayer()
                .start();
    }

    private void animateIn(FloatingActionButton fab) {
        ViewCompat.animate(fab)
                .translationY(0)
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setInterpolator(INTERPOLATOR)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        mIsAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                        mIsFabVisible = true;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        mIsAnimating = false;
                        mIsFabVisible = false;
                    }
                })
                .withLayer()
                .start();
    }
}
