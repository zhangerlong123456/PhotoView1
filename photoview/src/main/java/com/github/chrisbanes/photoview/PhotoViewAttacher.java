/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.github.chrisbanes.photoview;


import com.github.chrisbanes.photoview.copy.android.Interpolator;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentParent;
import ohos.agp.components.Image;
import ohos.agp.components.element.Element;
import ohos.agp.utils.Matrix;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.multimodalinput.event.TouchEvent;

/**
 * The component of {@link PhotoView} which does the work allowing for zooming, scaling, panning, etc.
 * It is made public in case you need to subclass something other than AppCompatImageView and still
 * gain the functionality that {@link PhotoView} offers
 */
//public class PhotoViewAttacher implements View.OnTouchListener,
//    View.OnLayoutChangeListener {
public class PhotoViewAttacher implements Component.TouchEventListener,
        Component.ComponentStateChangedListener {

    private static float DEFAULT_MAX_SCALE = 3.0f;
    private static float DEFAULT_MID_SCALE = 1.75f;
    private static float DEFAULT_MIN_SCALE = 1.0f;
    private static int DEFAULT_ZOOM_DURATION = 200;

    private static final int HORIZONTAL_EDGE_NONE = -1;
    private static final int HORIZONTAL_EDGE_LEFT = 0;
    private static final int HORIZONTAL_EDGE_RIGHT = 1;
    private static final int HORIZONTAL_EDGE_BOTH = 2;
    private static final int VERTICAL_EDGE_NONE = -1;
    private static final int VERTICAL_EDGE_TOP = 0;
    private static final int VERTICAL_EDGE_BOTTOM = 1;
    private static final int VERTICAL_EDGE_BOTH = 2;
    private static int SINGLE_TOUCH = 1;

    // TODO 鸿蒙目前缺少对应的API，后续对接或者自己封装
//    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;

    private boolean mAllowParentInterceptOnEdge = true;
    private boolean mBlockParentIntercept = false;

    private Image mImage;

    // Gesture Detectors
//    private GestureDetector mGestureDetector;
    private CustomGestureDetector mScaleDragDetector;

    // These are set so we don't keep allocating them on the heap
    // TODO 鸿蒙目前缺少对应的API，后续对接或者自己封装
//    private final Matrix mBaseMatrix = new Matrix();
//    private final Matrix mDrawMatrix = new Matrix();
//    private final Matrix mSuppMatrix = new Matrix();
    private final Rect mDisplayRect = new Rect();
    private final float[] mMatrixValues = new float[9];

    // Listeners
    private OnMatrixChangedListener mMatrixChangeListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnOutsidePhotoTapListener mOutsidePhotoTapListener;
    private OnViewTapListener mViewTapListener;
    //        private View.OnClickListener mOnClickListener;
    private Component.ClickedListener mOnClickListener;
    //    private OnLongClickListener mLongClickListener;
    private Component.LongClickedListener mLongClickListener;
    private OnScaleChangedListener mScaleChangeListener;
    private OnSingleFlingListener mSingleFlingListener;
    private OnViewDragListener mOnViewDragListener;

    private FlingRunnable mCurrentFlingRunnable;
    private int mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH;
    private int mVerticalScrollEdge = VERTICAL_EDGE_BOTH;
    private float mBaseRotation;

    private boolean mZoomEnabled = true;
    private Image.ScaleMode mScaleMode = Image.ScaleMode.CENTER;

    private OnGestureListener onGestureListener = new OnGestureListener() {
        @Override
        public void onDrag(float dx, float dy) {
            if (mScaleDragDetector.isScaling()) {
                return; // Do not drag if we are already scaling
            }
            if (mOnViewDragListener != null) {
                mOnViewDragListener.onDrag(dx, dy);
            }
//            mSuppMatrix.postTranslate(dx, dy);
            checkAndDisplayMatrix();

            /*
             * Here we decide whether to let the ImageView's parent to start taking
             * over the touch event.
             *
             * First we check whether this function is enabled. We never want the
             * parent to take over if we're scaling. We then check the edge we're
             * on, and the direction of the scroll (i.e. if we're pulling against
             * the edge, aka 'overscrolling', let the parent take over).
             */
            ComponentParent parent = mImage.getComponentParent();
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling() && !mBlockParentIntercept) {
                if (mHorizontalScrollEdge == HORIZONTAL_EDGE_BOTH
                        || (mHorizontalScrollEdge == HORIZONTAL_EDGE_LEFT && dx >= 1f)
                        || (mHorizontalScrollEdge == HORIZONTAL_EDGE_RIGHT && dx <= -1f)
                        || (mVerticalScrollEdge == VERTICAL_EDGE_TOP && dy >= 1f)
                        || (mVerticalScrollEdge == VERTICAL_EDGE_BOTTOM && dy <= -1f)) {
                    if (parent != null) {
//                        parent.requestDisallowInterceptTouchEvent(false);
//                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }
            } else {
                if (parent != null) {
//                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
        }

        @Override
        public void onFling(float startX, float startY, float velocityX, float velocityY) {
            mCurrentFlingRunnable = new FlingRunnable(mImage.getContext());
            mCurrentFlingRunnable.fling(getImageViewWidth(mImage),
                    getImageViewHeight(mImage), (int) velocityX, (int) velocityY);
//            mImage.post(mCurrentFlingRunnable);
        }

        @Override
        public void onScale(float scaleFactor, float focusX, float focusY) {
            if (getScale() < mMaxScale || scaleFactor < 1f) {
                if (mScaleChangeListener != null) {
                    mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
                }
//                mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                checkAndDisplayMatrix();
            }
        }
    };

    public PhotoViewAttacher(Image image) {
        mImage = image;
        image.setTouchEventListener(this);
//        image.addOnLayoutChangeListener(this);
//        if (image.isInEditMode()) {
//            return;
//        }
        mBaseRotation = 0.0f;
        // Create Gesture Detectors...
        mScaleDragDetector = new CustomGestureDetector(image.getContext(), onGestureListener);
//        mGestureDetector = new GestureDetector(image.getContext(), new GestureDetector.SimpleOnGestureListener() {
//
//            // forward long click listener
//            @Override
//            public void onLongPress(TouchEvent e) {
//                if (mLongClickListener != null) {
////                    mLongClickListener.onLongClick(mImage);
//                }
//            }
//
//            @Override
//            public boolean onFling(TouchEvent e1, TouchEvent e2,
//                                   float velocityX, float velocityY) {
//                if (mSingleFlingListener != null) {
//                    if (getScale() > DEFAULT_MIN_SCALE) {
//                        return false;
//                    }
//                    if (e1.getPointerCount() > SINGLE_TOUCH
//                            || e2.getPointerCount() > SINGLE_TOUCH) {
//                        return false;
//                    }
//                    return mSingleFlingListener.onFling(e1, e2, velocityX, velocityY);
//                }
//                return false;
//            }
//        });
//        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
//            @Override
//            public boolean onSingleTapConfirmed(TouchEvent e) {
//                if (mOnClickListener != null) {
//                    mOnClickListener.onClick(mImage);
//                }
//                final Rect displayRect = getDisplayRect();
//                final float x = e.getX(), y = e.getY();
//                if (mViewTapListener != null) {
//                    mViewTapListener.onViewTap(mImageView, x, y);
//                }
//                if (displayRect != null) {
//                    // Check to see if the user tapped on the photo
//                    if (displayRect.contains(x, y)) {
//                        float xResult = (x - displayRect.left)
//                                / displayRect.getWidth();
//                        float yResult = (y - displayRect.top)
//                                / displayRect.getHeight();
//                        if (mPhotoTapListener != null) {
//                            mPhotoTapListener.onPhotoTap(mImage, xResult, yResult);
//                        }
//                        return true;
//                    } else {
//                        if (mOutsidePhotoTapListener != null) {
//                            mOutsidePhotoTapListener.onOutsidePhotoTap(mImage);
//                        }
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTap(TouchEvent ev) {
//                try {
//                    float scale = getScale();
//                    float x = ev.getX();
//                    float y = ev.getY();
//                    if (scale < getMediumScale()) {
//                        setScale(getMediumScale(), x, y, true);
//                    } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
//                        setScale(getMaximumScale(), x, y, true);
//                    } else {
//                        setScale(getMinimumScale(), x, y, true);
//                    }
//                } catch (ArrayIndexOutOfBoundsException e) {
//                    // Can sometimes happen when getX() and getY() is called
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(TouchEvent e) {
//                // Wait for the confirmed onDoubleTap() instead
//                return false;
//            }
//        });
    }

//    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
//        this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
//    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return mZoomEnabled;
    }

    public Rect getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }
        if (mImage.getImageElement() == null) {
            return false;
        }
//        mSuppMatrix.set(finalMatrix);
        checkAndDisplayMatrix();
        return true;
    }

    public void setBaseRotation(final float degrees) {
        mBaseRotation = degrees % 360;
        update();
        setRotationBy(mBaseRotation);
        checkAndDisplayMatrix();
    }

    public void setRotationTo(float degrees) {
//        mSuppMatrix.setRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
//        mSuppMatrix.postRotate(degrees % 360);
        checkAndDisplayMatrix();
    }

    public float getMinimumScale() {
        return mMinScale;
    }

    public float getMediumScale() {
        return mMidScale;
    }

    public float getMaximumScale() {
        return mMaxScale;
    }

    public float getScale() {
//        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow
//                (getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
        return 0;
    }

    //    public ScaleType getScaleType() {
//        return mScaleType;
//    }
    public Image.ScaleMode getScaleMode() {
        return mScaleMode;
    }

//    @Override
//    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int
//            oldRight, int oldBottom) {
//        // Update our base matrix, as the bounds have changed
//        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
//            updateBaseMatrix(mImageView.getDrawable());
//        }
//    }

//    @Override
//    public boolean onTouch(Component v, TouchEvent ev) {
//        boolean handled = false;
//        if (mZoomEnabled && Util.hasDrawable((Image) v)) {
//            switch (ev.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    ComponentParent parent = v.getComponentParent();
//                    // First, disable the Parent from intercepting the touch
//                    // event
//                    if (parent != null) {
////                        parent.requestDisallowInterceptTouchEvent(true);
//                    }
//                    // If we're flinging, and the user presses down, cancel
//                    // fling
//                    cancelFling();
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                    // If the user has zoomed less than min scale, zoom back
//                    // to min scale
//                    if (getScale() < mMinScale) {
//                        RectF rect = getDisplayRect();
//                        if (rect != null) {
//                            v.post(new AnimatedZoomRunnable(getScale(), mMinScale,
//                                    rect.centerX(), rect.centerY()));
//                            handled = true;
//                        }
//                    } else if (getScale() > mMaxScale) {
//                        Rect rect = getDisplayRect();
//                        if (rect != null) {
//                            v.post(new AnimatedZoomRunnable(getScale(), mMaxScale,
//                                    rect.centerX(), rect.centerY()));
//                            handled = true;
//                        }
//                    }
//                    break;
//            }
//            // Try the Scale/Drag detector
//            if (mScaleDragDetector != null) {
//                boolean wasScaling = mScaleDragDetector.isScaling();
//                boolean wasDragging = mScaleDragDetector.isDragging();
//                handled = mScaleDragDetector.onTouchEvent(mImage, ev);
//                boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
//                boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();
//                mBlockParentIntercept = didntScale && didntDrag;
//            }
//            // Check to see if the user double tapped
//            if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
//                handled = true;
//            }
//
//        }
//        return handled;
//    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    public void setMinimumScale(float minimumScale) {
        Util.checkZoomLevels(minimumScale, mMidScale, mMaxScale);
        mMinScale = minimumScale;
    }

    public void setMediumScale(float mediumScale) {
        Util.checkZoomLevels(mMinScale, mediumScale, mMaxScale);
        mMidScale = mediumScale;
    }

    public void setMaximumScale(float maximumScale) {
        Util.checkZoomLevels(mMinScale, mMidScale, maximumScale);
        mMaxScale = maximumScale;
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        Util.checkZoomLevels(minimumScale, mediumScale, maximumScale);
        mMinScale = minimumScale;
        mMidScale = mediumScale;
        mMaxScale = maximumScale;
    }

    public void setOnLongClickListener(Component.LongClickedListener listener) {
        mLongClickListener = listener;
    }

    public void setOnClickListener(Component.ClickedListener listener) {
        mOnClickListener = listener;
    }

    private Component.ClickedListener mClickedListener;

    public void setClickedListener(Component.ClickedListener listener) {
        mClickedListener = listener;
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mMatrixChangeListener = listener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener mOutsidePhotoTapListener) {
        this.mOutsidePhotoTapListener = mOutsidePhotoTapListener;
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        mViewTapListener = listener;
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        mOnViewDragListener = listener;
    }

    public void setScale(float scale) {
        setScale(scale, false);
    }

    public void setScale(float scale, boolean animate) {
        setScale(scale,
                (mImage.getWidth()) / 2,
                (mImage.getHeight()) / 2,
                animate);
    }

    public void setScale(float scale, float focalX, float focalY,
                         boolean animate) {
        // Check to see if the scale is within bounds
        if (scale < mMinScale || scale > mMaxScale) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }
        if (animate) {
//            mImage.post(new AnimatedZoomRunnable(getScale(), scale,
//                    focalX, focalY));
        } else {
//            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    public void setZoomInterpolator(Interpolator interpolator) {
//        mInterpolator = interpolator;
    }

//    public void setScaleType(ScaleType scaleType) {
//        if (Util.isSupportedScaleType(scaleType) && scaleType != mScaleType) {
//            mScaleType = scaleType;
//            update();
//        }
//    }

    public void setScaleMode(Image.ScaleMode scaleMode) {
        if (Util.isSupportedScaleMode(scaleMode) && scaleMode != mScaleMode) {
            mScaleMode = scaleMode;
            update();
        }
    }

    public boolean isZoomable() {
        return mZoomEnabled;
    }

    public void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;
        update();
    }

    public void update() {
        if (mZoomEnabled) {
            // Update the base matrix using the current drawable
            updateBaseMatrix(mImage.getImageElement());
        } else {
            // Reset the Matrix...
            resetMatrix();
        }
    }

    /**
     * Get the display matrix
     *
     * @param matrix target matrix to copy to
     */
    public void getDisplayMatrix(Matrix matrix) {
//        matrix.set(getDrawMatrix());
    }

    /**
     * Get the current support matrix
     */
    public void getSuppMatrix(Matrix matrix) {
//        matrix.set(mSuppMatrix);
    }

    private Matrix getDrawMatrix() {
//        mDrawMatrix.set(mBaseMatrix);
//        mDrawMatrix.postConcat(mSuppMatrix);
//        return mDrawMatrix;
        return null;
    }

    public Matrix getImageMatrix() {
//        return mDrawMatrix;
        return null;
    }

    public void setZoomTransitionDuration(int milliseconds) {
        this.mZoomDuration = milliseconds;
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
//        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays its contents
     */
    private void resetMatrix() {
//        mSuppMatrix.reset();
        setRotationBy(mBaseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
//        mImage.setImageMatrix(matrix);
        // Call MatrixChangedListener if needed
        if (mMatrixChangeListener != null) {
            Rect displayRect = getDisplayRect(matrix);
            if (displayRect != null) {
                mMatrixChangeListener.onMatrixChanged(displayRect);
            }
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private Rect getDisplayRect(Matrix matrix) {
        Element e = mImage.getImageElement();
        if (e != null) {
            mDisplayRect.set(0, 0, e.getBounds().getWidth(),
                    e.getBounds().getHeight());
//            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param drawable - Drawable being displayed
     */
    private void updateBaseMatrix(Element drawable) {
        if (drawable == null) {
            return;
        }
        final float viewWidth = getImageViewWidth(mImage);
        final float viewHeight = getImageViewHeight(mImage);
        final int drawableWidth = drawable.getBounds().getWidth();
        final int drawableHeight = drawable.getBounds().getHeight();
//        mBaseMatrix.reset();
        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;
        if (mScaleMode == Image.ScaleMode.CENTER) {
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
//                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleMode == Image.ScaleMode.CLIP_CENTER) {
            float scale = Math.max(widthScale, heightScale);
//            mBaseMatrix.postScale(scale, scale);
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
//                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleMode == Image.ScaleMode.ZOOM_CENTER) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
//            mBaseMatrix.postScale(scale, scale);
//            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
//                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            Rect mTempSrc = new Rect(0, 0, drawableWidth, drawableHeight);
            Rect mTempDst = new Rect(0, 0, (int) viewWidth, (int) viewHeight);
            if ((int) mBaseRotation % 180 != 0) {
                mTempSrc = new Rect(0, 0, drawableHeight, drawableWidth);
            }
            switch (mScaleMode) {
                case ZOOM_CENTER:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
                    break;
                case ZOOM_START:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START);
                    break;
                case ZOOM_END:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END);
                    break;
                case STRETCH:
//                    mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL);
                    break;
                default:
                    break;
            }
        }
        resetMatrix();
    }

    private boolean checkMatrixBounds() {
        final Rect rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        final float height = rect.getHeight(), width = rect.getWidth();
        float deltaX = 0, deltaY = 0;
        final int viewHeight = getImageViewHeight(mImage);
        if (height <= viewHeight) {
            switch (mScaleMode) {
                case ZOOM_START:
                    deltaY = -rect.top;
                    break;
                case ZOOM_END:
                    deltaY = viewHeight - height - rect.top;
                    break;
                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
            mVerticalScrollEdge = VERTICAL_EDGE_BOTH;
        } else if (rect.top > 0) {
            mVerticalScrollEdge = VERTICAL_EDGE_TOP;
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            mVerticalScrollEdge = VERTICAL_EDGE_BOTTOM;
            deltaY = viewHeight - rect.bottom;
        } else {
            mVerticalScrollEdge = VERTICAL_EDGE_NONE;
        }
        final int viewWidth = getImageViewWidth(mImage);
        if (width <= viewWidth) {
            switch (mScaleMode) {
                case ZOOM_START:
                    deltaX = -rect.left;
                    break;
                case ZOOM_END:
                    deltaX = viewWidth - width - rect.left;
                    break;
                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH;
        } else if (rect.left > 0) {
            mHorizontalScrollEdge = HORIZONTAL_EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mHorizontalScrollEdge = HORIZONTAL_EDGE_RIGHT;
        } else {
            mHorizontalScrollEdge = HORIZONTAL_EDGE_NONE;
        }
        // Finally actually translate the matrix
//        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private int getImageViewWidth(Image imageView) {
//        return imageView.getWidth() - imageView.getLeft() - imageView.get;
        return imageView.getWidth();
    }

    private int getImageViewHeight(Image imageView) {
//        return imageView.getHeight() - imageView.getTop() - imageView.getb;
        return imageView.getHeight();
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    @Override
    public void onComponentStateChanged(Component component, int i) {

    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent ev) {
        boolean handled = false;
        if (mZoomEnabled && Util.hasDrawable((Image) component)) {
            switch (ev.getAction()) {
                case TouchEvent.PRIMARY_POINT_DOWN:
                    ComponentParent parent = component.getComponentParent();
                    // First, disable the Parent from intercepting the touch
                    // event
                    if (parent != null) {
//                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    // If we're flinging, and the user presses down, cancel
                    // fling
                    cancelFling();
                    break;
                case TouchEvent.CANCEL:
                case TouchEvent.PRIMARY_POINT_UP:
                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (getScale() < mMinScale) {
                        Rect rect = getDisplayRect();
                        if (rect != null) {
//                            component.post(new AnimatedZoomRunnable(getScale(), mMinScale,
//                                    rect.getCenterX(), rect.getCenterY()));
                            handled = true;
                        }
                    } else if (getScale() > mMaxScale) {
                        Rect rect = getDisplayRect();
                        if (rect != null) {
//                            component.post(new AnimatedZoomRunnable(getScale(), mMaxScale,
//                                    rect.getCenterX(), rect.getCenterY()));
                            handled = true;
                        }
                    }
                    break;
            }
            // Try the Scale/Drag detector
            if (mScaleDragDetector != null) {
                boolean wasScaling = mScaleDragDetector.isScaling();
                boolean wasDragging = mScaleDragDetector.isDragging();
                handled = mScaleDragDetector.onTouchEvent(mImage, ev);
                boolean didntScale = !wasScaling && !mScaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !mScaleDragDetector.isDragging();
                mBlockParentIntercept = didntScale && didntDrag;
            }
            // Check to see if the user double tapped
//            if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
//                handled = true;
//            }

        }
        return handled;
    }

    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {
            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();
            onGestureListener.onScale(deltaScale, mFocalX, mFocalY);
            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(mImage, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
//            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

//        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
//            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
//            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX,
                          int velocityY) {
            final Rect rect = getDisplayRect();
            if (rect == null) {
                return;
            }
            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;
            if (viewWidth < rect.getWidth()) {
                minX = 0;
                maxX = Math.round(rect.getWidth() - viewWidth);
            } else {
                minX = maxX = startX;
            }
            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.getHeight()) {
                minY = 0;
                maxY = Math.round(rect.getHeight() - viewHeight);
            } else {
                minY = maxY = startY;
            }
            mCurrentX = startX;
            mCurrentY = startY;
            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
//                mScroller.fling(startX, startY, velocityX, velocityY, minX,
//                        maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
//            if (mScroller.isFinished()) {
//                return; // remaining post that should not be handled
//            }
//            if (mScroller.computeScrollOffset()) {
//                final int newX = mScroller.getCurrX();
//                final int newY = mScroller.getCurrY();
//                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
//                checkAndDisplayMatrix();
//                mCurrentX = newX;
//                mCurrentY = newY;
//                // Post On animation
//                Compat.postOnAnimation(mImage, this);
//            }
        }
    }
}
