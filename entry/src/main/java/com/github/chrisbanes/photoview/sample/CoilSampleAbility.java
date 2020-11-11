package com.github.chrisbanes.photoview.sample;

import com.github.chrisbanes.photoview.sample.slice.CoilSampleAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class CoilSampleAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(CoilSampleAbilitySlice.class.getName());
    }
}
