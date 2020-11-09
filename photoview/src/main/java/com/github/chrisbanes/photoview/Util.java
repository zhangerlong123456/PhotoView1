package com.github.chrisbanes.photoview;

//import android.view.MotionEvent;
//import android.widget.ImageView;

import ohos.agp.components.Image;
import ohos.multimodalinput.event.TouchEvent;

class Util {

    static void checkZoomLevels(float minZoom, float midZoom,
                                float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(
                    "Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(
                    "Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    static boolean hasDrawable(Image image) {
        return image.getImageElement() != null;
    }

    static boolean isSupportedScaleMode(final Image.ScaleMode scaleMode) {
        if (scaleMode == null) {
            return false;
        }
//        switch (scaleMode) {
//            case Image.ScaleMode.STRETCH:
//                throw new IllegalStateException("Matrix scale type is not supported");
//        }
        if (scaleMode == Image.ScaleMode.STRETCH) {
            throw new IllegalStateException("Stretch scale type is not supported");
        }
        return true;
    }

    static int getPointerIndex(int action) {
//        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        return (action & TouchEvent.PRIMARY_POINT_UP) >> TouchEvent.PRIMARY_POINT_DOWN;
    }
}
