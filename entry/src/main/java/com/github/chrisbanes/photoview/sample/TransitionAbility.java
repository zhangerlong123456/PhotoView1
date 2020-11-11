package com.github.chrisbanes.photoview.sample;

import com.github.chrisbanes.photoview.sample.slice.TransitionAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class TransitionAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(TransitionAbilitySlice.class.getName());
    }
}
