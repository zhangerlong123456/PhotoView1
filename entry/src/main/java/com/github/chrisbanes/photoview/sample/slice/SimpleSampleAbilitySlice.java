package com.github.chrisbanes.photoview.sample.slice;

import com.github.chrisbanes.photoview.*;
import com.github.chrisbanes.photoview.sample.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.DirectionalLayout.LayoutConfig;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.Matrix;
import ohos.agp.utils.Rect;
import ohos.agp.utils.TextAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.global.resource.Element;
import ohos.multimodalinput.event.TouchEvent;

public class SimpleSampleAbilitySlice extends AbilitySlice {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";

    private PhotoView mPhotoView;
    private Text mCurrMatrixTv;

    private ToastDialog mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;

    private DirectionalLayout myLayout = new DirectionalLayout(this);


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        LayoutConfig config = new LayoutConfig(LayoutConfig.MATCH_PARENT, LayoutConfig.MATCH_PARENT);
        myLayout.setLayoutConfig(config);
        myLayout.setOrientation(Component.VERTICAL);
        ShapeElement element = new ShapeElement();
        element.setRgbColor(new RgbColor(255, 255, 255));
        myLayout.setBackground(element);

        mPhotoView=new PhotoView(this);
        mPhotoView.setWidth(750);
        mPhotoView.setHeight(1280);
        myLayout.addComponent(mPhotoView);

        mCurrMatrixTv = new Text(this);
        mCurrMatrixTv.setLayoutConfig(config);
//        mCurrMatrixTv.setText("Hello World");
        mCurrMatrixTv.setTextColor(new Color(0xFF000000));
        mCurrMatrixTv.setTextSize(50);
        mCurrMatrixTv.setTextAlignment(TextAlignment.CENTER);
        myLayout.addComponent(mCurrMatrixTv);
        super.setUIContent(myLayout);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Simple Sample");
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        toolbar.inflateMenu(R.menu.main_menu);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.menu_zoom_toggle:
//                        mPhotoView.setZoomable(!mPhotoView.isZoomable());
//                        item.setTitle(mPhotoView.isZoomable() ? R.string.menu_zoom_disable : R.string.menu_zoom_enable);
//                        return true;
//
//                    case R.id.menu_scale_fit_center:
//                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER);
//                        return true;
//
//                    case R.id.menu_scale_fit_start:
//                        mPhotoView.setScaleType(ImageView.ScaleType.FIT_START);
//                        return true;
//
//                    case R.id.menu_scale_fit_end:
//                        mPhotoView.setScaleType(ImageView.ScaleType.FIT_END);
//                        return true;
//
//                    case R.id.menu_scale_fit_xy:
//                        mPhotoView.setScaleType(ImageView.ScaleType.FIT_XY);
//                        return true;
//
//                    case R.id.menu_scale_scale_center:
//                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER);
//                        return true;
//
//                    case R.id.menu_scale_scale_center_crop:
//                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        return true;
//
//                    case R.id.menu_scale_scale_center_inside:
//                        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                        return true;
//
//                    case R.id.menu_scale_random_animate:
//                    case R.id.menu_scale_random:
//                        Random r = new Random();
//
//                        float minScale = mPhotoView.getMinimumScale();
//                        float maxScale = mPhotoView.getMaximumScale();
//                        float randomScale = minScale + (r.nextFloat() * (maxScale - minScale));
//                        mPhotoView.setScale(randomScale, item.getItemId() == R.id.menu_scale_random_animate);
//
//                        showToast(String.format(SCALE_TOAST_STRING, randomScale));
//
//                        return true;
//                    case R.id.menu_matrix_restore:
//                        if (mCurrentDisplayMatrix == null)
//                            showToast("You need to capture display matrix first");
//                        else
//                            mPhotoView.setDisplayMatrix(mCurrentDisplayMatrix);
//                        return true;
//                    case R.id.menu_matrix_capture:
//                        mCurrentDisplayMatrix = new Matrix();
//                        mPhotoView.getDisplayMatrix(mCurrentDisplayMatrix);
//                        return true;
//                }
//                return false;
//            }
//        });
        mCurrMatrixTv =(Text)findComponentById(ResourceTable.Id_tv_current_matrix);

        mPhotoView.setImageAndDecodeBounds(ResourceTable.Media_wallpaper);

        // Lets attach some listeners, not required though!
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
    }



    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(Image image, float x, float y) {

        }
    }

    private void showToast(CharSequence text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }

//        mCurrentToast = ToastDialog.makeText(SimpleSampleActivity.this, text, Toast.LENGTH_SHORT);
//        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {


        @Override
        public void onMatrixChanged(Rect rect) {

        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {
        @Override
        public boolean onFling(TouchEvent e1, TouchEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
