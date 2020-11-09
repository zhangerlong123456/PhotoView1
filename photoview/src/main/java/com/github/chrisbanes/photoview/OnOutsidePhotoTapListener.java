package com.github.chrisbanes.photoview;


import ohos.agp.components.Image;

/**
 * Callback when the user tapped outside of the photo
 */
public interface OnOutsidePhotoTapListener {

    /**
     * The outside of the photo has been tapped
     */
    void onOutsidePhotoTap(Image image);
}
