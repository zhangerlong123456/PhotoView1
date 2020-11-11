package com.github.chrisbanes.photoview.sample.slice;

import com.github.chrisbanes.photoview.PhotoView;
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
import ohos.utils.net.Uri;

public class CoilSampleAbilitySlice extends AbilitySlice {


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_simple);
        PhotoView photoView = (PhotoView) findComponentById(ResourceTable.Id_list);
        photoView.setImageURI(Uri.parse("https://images.unsplash.com/photo-1577643816920-65b43ba99fba?ixlib=rb-1.2.1&auto=format&fit=crop&w=3300&q=80"));
//        photoView.load("https://images.unsplash.com/photo-1577643816920-65b43ba99fba?ixlib=rb-1.2.1&auto=format&fit=crop&w=3300&q=80");
    }
}