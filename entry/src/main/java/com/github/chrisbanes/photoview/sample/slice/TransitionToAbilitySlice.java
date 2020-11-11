package com.github.chrisbanes.photoview.sample.slice;

import com.github.chrisbanes.photoview.sample.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.DirectionalLayout.LayoutConfig;
import ohos.agp.components.Text;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.TextAlignment;

public class TransitionToAbilitySlice extends AbilitySlice {


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
       setUIContent(ResourceTable.Layout_ability_transition_to);
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
