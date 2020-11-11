package com.github.chrisbanes.photoview.sample;

import com.github.chrisbanes.photoview.sample.slice.TransitionToAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class TransitionToAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(TransitionToAbilitySlice.class.getName());
    }
}
