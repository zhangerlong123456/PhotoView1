package com.github.chrisbanes.photoview.sample;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.sample.slice.SimpleSampleAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.agp.utils.Matrix;

public class SimpleSampleAbility extends Ability {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(SimpleSampleAbilitySlice.class.getName());
    }
}
