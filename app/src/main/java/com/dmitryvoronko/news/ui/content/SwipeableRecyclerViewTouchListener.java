package com.dmitryvoronko.news.ui.content;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;

/**
 *
 * Created by Dmitry on 20/10/2016.
 */

final class SwipeableRecyclerViewTouchListener
        implements RecyclerView.OnItemTouchListener
{
    // Cached ViewConfiguration and system-wide constant values
    private final int mSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private final long mAnimationTime;

    // Fixed properties
    private final RecyclerView mRecyclerView;
    private final SwipeListener mSwipeListener;
    // Transient properties
    private final List<PendingDismissData> mPendingDismisses = new ArrayList<>();
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero
    private int mDismissAnimationRefCount = 0;
    private float mAlpha;
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mDownPosition;
    private int mAnimatingPosition = ListView.INVALID_POSITION;
    private View mDownView;
    private boolean mPaused;
    private float mFinalDelta;

    private boolean mSwipingLeft;
    private boolean mSwipingRight;

    SwipeableRecyclerViewTouchListener(final RecyclerView recyclerView,
                                       final SwipeListener listener)
    {
        final ViewConfiguration vc = ViewConfiguration.get(recyclerView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime =
                recyclerView.getContext()
                            .getResources()
                            .getInteger(
                                    android.R.integer.config_shortAnimTime);
        mRecyclerView = recyclerView;
        mSwipeListener = listener;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView,
                                             final int newState)
            {
                setEnabled(newState != RecyclerView.SCROLL_STATE_DRAGGING);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView,
                                   final int dx,
                                   final int dy)
            {
            }
        });
    }

    private void setEnabled(final boolean enabled)
    {
        mPaused = !enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView rv,
                                         final MotionEvent motionEvent)
    {
        return handleTouchEvent(motionEvent);
    }

    @Override
    public void onTouchEvent(final RecyclerView rv,
                             final MotionEvent motionEvent)
    {
        handleTouchEvent(motionEvent);
    }



    @Override
    public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept)
    {
        // Do nothing.
    }

    private boolean handleTouchEvent(final MotionEvent motionEvent)
    {
        if (mViewWidth < 2)
        {
            mViewWidth = mRecyclerView.getWidth();
        }
        switch (motionEvent.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (mPaused)
                {
                    break;
                }

                // Find the child ui that was touched (perform a hit test)
                final Rect rect = new Rect();
                final int childCount = mRecyclerView.getChildCount();
                final int[] listViewCoordinates = new int[2];
                mRecyclerView.getLocationOnScreen(listViewCoordinates);
                final int x = (int) motionEvent.getRawX() - listViewCoordinates[0];
                final int y = (int) motionEvent.getRawY() - listViewCoordinates[1];
                View child;
                for (int i = 0; i < childCount; i++)
                {
                    child = mRecyclerView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x,
                                      y))
                    {
                        mDownView = child;
                        break;
                    }
                }

                if (mDownView != null && mAnimatingPosition != mRecyclerView.getChildLayoutPosition(
                        mDownView))
                {
                    mAlpha = ViewCompat.getAlpha(mDownView);
                    mDownX = motionEvent.getRawX();
                    mDownY = motionEvent.getRawY();
                    mDownPosition = mRecyclerView.getChildLayoutPosition(mDownView);
                    mSwipingLeft = false;
                    mSwipingRight = true;
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionEvent);
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            {
                if (mVelocityTracker == null)
                {
                    break;
                }

                if (mDownView != null && mSwiping)
                {
                    // cancel
                    ViewCompat.animate(mDownView)
                              .translationX(0)
                              .alpha(mAlpha)
                              .setDuration(mAnimationTime)
                              .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mDownView = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                if (mVelocityTracker == null)
                {
                    break;
                }

                mFinalDelta = motionEvent.getRawX() - mDownX;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                final float velocityX = mVelocityTracker.getXVelocity();
                final float absVelocityX = Math.abs(velocityX);
                final float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(mFinalDelta) > (float) mViewWidth / (float) 2 && mSwiping)
                {
                    dismiss = true;
                    dismissRight = mFinalDelta > 0;
                } else if ((float) mMinFlingVelocity <= absVelocityX && absVelocityX <=
                        (float) mMaxFlingVelocity
                        && absVelocityY < absVelocityX && mSwiping)
                {
                    // dismiss only if flinging in the same direction as dragging
                    dismiss = (velocityX < (float) 0) == (mFinalDelta < (float) 0);
                    dismissRight = mVelocityTracker.getXVelocity() > (float) 0;
                }
                if (dismiss && mDownPosition != mAnimatingPosition &&
                        mDownPosition != ListView.INVALID_POSITION)
                {
                    // dismiss
                    final View downView = mDownView; // mDownView gets null'd before animation ends
                    final int downPosition = mDownPosition;
                    ++mDismissAnimationRefCount;
                    mAnimatingPosition = mDownPosition;
                    ViewCompat.animate(mDownView)
                              .translationX(dismissRight ?
                                            mViewWidth :
                                            -mViewWidth)
                              .alpha(0)
                              .setDuration(mAnimationTime)
                              .setListener(new ViewPropertyAnimatorListener()
                              {
                                  @Override
                                  public void onAnimationStart(final View view)
                                  {
                                      // Do nothing.
                                  }

                                  @Override
                                  public void onAnimationEnd(final View view)
                                  {
                                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                      {
                                          performDismiss(downView,
                                                         downPosition);
                                      }
                                  }

                                  @Override
                                  public void onAnimationCancel(final View view)
                                  {
                                      // Do nothing.
                                  }
                              });
                } else
                {
                    // cancel
                    ViewCompat.animate(mDownView)
                              .translationX(0)
                              .alpha(mAlpha)
                              .setDuration(mAnimationTime)
                              .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mDownView = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (mVelocityTracker == null || mPaused)
                {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                final float deltaX = motionEvent.getRawX() - mDownX;
                final float deltaY = motionEvent.getRawY() - mDownY;
                if (!mSwiping && Math.abs(deltaX) > mSlop &&
                        Math.abs(deltaY) < Math.abs(deltaX) / 2)
                {
                    mSwiping = true;
                    mSwipingSlop =
                            (deltaX > 0 ?
                             mSlop :
                             -mSlop);
                }

                if (deltaX < 0 && !mSwipingLeft)
                {
                    mSwiping = false;
                }
                if (deltaX > 0 && !mSwipingRight)
                {
                    mSwiping = false;
                }

                if (mSwiping)
                {
                    ViewCompat.setTranslationX(mDownView,
                                               deltaX - mSwipingSlop);
                    ViewCompat.setAlpha(mDownView,
                                        Math.max(0f,
                                                 Math.min(mAlpha,
                                                          mAlpha * (1f -
                                                                  Math.abs(deltaX) / mViewWidth))));
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void performDismiss(final View dismissView,
                                final int dismissPosition)
    {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more productively.

        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalLayoutParamsHeight = lp.height;
        final int originalHeight = dismissView.getHeight();

        final ValueAnimator
                animator =
                ValueAnimator.ofInt(originalHeight,
                                    1)
                             .setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(final Animator animation)
            {
                --mDismissAnimationRefCount;
                if (mDismissAnimationRefCount == 0)
                {
                    // No active animations, process all pending dismisses.
                    // Sort by descending position
                    Collections.sort(mPendingDismisses);

                    final int[] dismissPositions = new int[mPendingDismisses.size()];
                    for (int i = mPendingDismisses.size() - 1; i >= 0; i--)
                    {
                        dismissPositions[i] = mPendingDismisses.get(i).position;
                    }

                    if (mFinalDelta >= 0)
                    {
                        mSwipeListener.onDismissedBySwipeRight(dismissPositions);
                    }

                    // Reset mDownPosition to avoid MotionEvent.ACTION_UP trying to start a dismiss
                    // animation with a stale position
                    mDownPosition = ListView.INVALID_POSITION;

                    ViewGroup.LayoutParams lp;
                    for (final PendingDismissData pendingDismiss : mPendingDismisses)
                    {
                        // Reset ui presentation
                        pendingDismiss.view.setAlpha(mAlpha);
                        pendingDismiss.view.setTranslationX(0);

                        lp = pendingDismiss.view.getLayoutParams();
                        lp.height = originalLayoutParamsHeight;

                        pendingDismiss.view.setLayoutParams(lp);
                    }

                    // Send a cancel event
                    final long time = SystemClock.uptimeMillis();
                    final MotionEvent cancelEvent = MotionEvent.obtain(
                            time,
                            time,
                            MotionEvent.ACTION_CANCEL,
                            0,
                            0,
                            0);
                    mRecyclerView.dispatchTouchEvent(cancelEvent);

                    mPendingDismisses.clear();
                    mAnimatingPosition = ListView.INVALID_POSITION;
                }
            }
        });

        animator.addUpdateListener(new MyAnimatorUpdateListener(lp, dismissView));

        mPendingDismisses.add(new PendingDismissData(dismissPosition,
                                                     dismissView));
        animator.start();
    }

    interface SwipeListener
    {
        void onDismissedBySwipeRight(final int[] reverseSortedPositions);
    }
    @EqualsAndHashCode
    private static final class PendingDismissData implements Comparable<PendingDismissData>
    {
        private final int position;
        private final View view;

        PendingDismissData(final int position,
                           final View view)
        {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(final @NonNull PendingDismissData other)
        {
            return other.position - position;
        }
    }

    private static final class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener
    {
        private final ViewGroup.LayoutParams lp;
        private final View dismissView;

        public MyAnimatorUpdateListener(final ViewGroup.LayoutParams lp, final View dismissView)
        {
            this.lp = lp;
            this.dismissView = dismissView;
        }

        @Override
        public final void onAnimationUpdate(final ValueAnimator valueAnimator)
        {
            lp.height = (Integer) valueAnimator.getAnimatedValue();
            dismissView.setLayoutParams(lp);
        }
    }
}
