package com.github.chrisbanes.photoview.sample.slice;

import com.github.chrisbanes.photoview.sample.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.DirectionalLayout.LayoutConfig;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.TextAlignment;
import ohos.agp.window.dialog.ToastDialog;

public class TransitionAbilitySlice extends AbilitySlice {


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_transition);
        ListContainer list =(ListContainer) findComponentById(ResourceTable.Id_list);
//        list.setLayoutManager(new GridLayoutManager(this, 2));
//        ImageAdapter imageAdapter = new ImageAdapter(new ImageAdapter.Listener() {
//            @Override
//            public void onImageClicked(View view) {
//                transition(view);
//            }
//        });
//        list.setAdapter(imageAdapter);

    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void transition(Component view) {
//        if (Build.VERSION.SDK_INT < 21) {
//            ToastDialog.(ActivityTransitionActivity.this, "21+ only, keep out", Toast.LENGTH_SHORT).show();
//        } else {
            Intent intent = new Intent();
            Operation option = new Intent.OperationBuilder()
                       .withDeviceId("")
                    .withBundleName("com.github.chrisbanes.photoview.sample")
                    .withAbilityName("com.github.chrisbanes.photoview.sample.TransitionToAbility")
                    .build();
            intent.setOperation(option);
            startAbility(intent);
//        }
    }
}
