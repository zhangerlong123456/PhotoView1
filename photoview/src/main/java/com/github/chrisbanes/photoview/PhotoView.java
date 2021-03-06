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

import ohos.aafwk.ability.OnClickListener;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.element.Element;
import ohos.agp.utils.Matrix;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

/**
 * A zoomable ImageView. See {@link PhotoViewAttacher} for most of the details on how the zooming
 * is accomplished
 */
@SuppressWarnings("unused")
public class PhotoView extends Image {

    private PhotoViewAttacher attacher;
    private ScaleMode pendingScaleMode;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttrSet attr) {
        this(context, attr, "");
    }

    public PhotoView(Context context, AttrSet attr, String defStyle) {
        super(context, attr, defStyle);
        init();
    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleMode(ScaleMode.STRETCH);
        //apply the previously applied scale type
        if (pendingScaleMode != null) {
            setScaleMode(pendingScaleMode);
            pendingScaleMode = null;
        }
    }

    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }


    @Override
    public ScaleMode getScaleMode() {
        return attacher.getScaleMode();
    }

    //    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    //    @Override
    public void setOnLongClickListener(LongClickedListener l) {
        attacher.setOnLongClickListener(l);
    }

    //    @Override
    public void setOnClickListener(ClickedListener l) {
        attacher.setOnClickListener(l);
    }

    @Override
    public void setClickedListener(Component.ClickedListener listener) {
        attacher.setClickedListener(listener);
    }

    @Override
    public void setScaleMode(ScaleMode scaleMode) {
        if (attacher == null) {
            pendingScaleMode = scaleMode;
        } else {
            attacher.setScaleMode(scaleMode);
        }
    }

    @Override
    public void setImageElement(Element element) {
        super.setImageElement(element);
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
    }

//    @Override
    public void setImageResource(int resId) {
//        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }


    @Override
    public void setPixelMap(int resId) {
        super.setPixelMap(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setPixelMap(PixelMap pixelMap) {
        super.setPixelMap(pixelMap);
    }

    @Override
    public void setImageAndDecodeBounds(int resId) {
        super.setImageAndDecodeBounds(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

//    @Override
    public void setImageURI(Uri uri) {
//        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
    }

    //    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
//        boolean changed = super.setFrame(l, t, r, b);
//        if (changed) {
//            attacher.update();
//        }
//        return changed;

        return false;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public Rect getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        attacher.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    // TODO 鸿蒙暂无GestureDetector对应API，后续对接或自己实现
//    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
//        attacher.setOnDoubleTapListener(onDoubleTapListener);
//    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }
}
