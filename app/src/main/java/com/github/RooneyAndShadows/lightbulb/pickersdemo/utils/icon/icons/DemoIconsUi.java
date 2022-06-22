package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons;

import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.IDemoIcon;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome;


import java.util.HashMap;
import java.util.Map;

public enum DemoIconsUi implements IDemoIcon {
    ICON_MENU_ADAPTER_PICKER(1, FontAwesome.Icon.faw_shapes),
    ICON_MENU_COLOR_PICKER(2, FontAwesome.Icon.faw_palette),
    ICON_PICKER_INDICATOR(3, FontAwesome.Icon.faw_shapes);


    private final Integer value;
    private final IIcon icon;
    private static final Map<Integer, DemoIconsUi> mapValues = new HashMap<>();

    DemoIconsUi(Integer value, IIcon icon) {
        this.value = value;
        this.icon = icon;
    }

    static {
        for (DemoIconsUi icon : DemoIconsUi.values()) {
            mapValues.put(icon.value, icon);
        }
    }

    public static DemoIconsUi valueOf(Integer icon) {
        return (DemoIconsUi) mapValues.get(icon);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public IIcon getIcon() {
        return icon;
    }
}