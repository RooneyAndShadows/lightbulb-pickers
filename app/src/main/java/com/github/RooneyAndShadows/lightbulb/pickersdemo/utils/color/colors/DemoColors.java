package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.colors;

import android.graphics.Color;

import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.IDemoColor;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum DemoColors implements IDemoColor {
    BROWN("BROWN", "#795548"),
    BROWN_DARK("BROWN_DARK", "#5D4037"),
    DEEP_ORANGE("DEEP_ORANGE", "#FF5722"),
    DEEP_ORANGE_DARK("DEEP_ORANGE_DARK", "#E64A19"),
    ORANGE("ORANGE", "#FF9800"),
    ORANGE_DARK("ORANGE_DARK", "#F57C00"),
    AMBER("AMBER", "#FFC107"),
    AMBER_DARK("AMBER_DARK", "#FFA000"),
    YELLOW("AMBER", "#FFEB3B"),
    YELLOW_DARK("AMBER_DARK", "#FBC02D"),
    LIME("LIME", "#CDDC39"),
    LIME_DARK("LIME_DARK", "#AFB42B"),
    LIGHT_GREEN("LIGHT_GREEN", "#8BC34A"),
    LIGHT_GREEN_DARK("LIGHT_GREEN_DARK", "#689F38"),
    GREEN("GREEN", "#4CAF50"),
    GREEN_DARK("GREEN_DARK", "#388E3C"),
    TEAL("TEAL", "#009688"),
    TEAL_DARK("TEAL_DARK", "#00796B"),
    CYAN("CYAN", "#00BCD4"),
    CYAN_DARK("CYAN_DARK", "#0097A7"),
    LIGHT_BLUE("LIGHT_BLUE", "#03A9F4"),
    LIGHT_BLUE_DARK("LIGHT_BLUE_DARK", "#0288D1"),
    BLUE("BLUE", "#2196F3"),
    BLUE_DARK("BLUE_DARK", "#1976D2"),
    INDIGO("INDIGO", "#3F51B5"),
    INDIGO_DARK("INDIGO_DARK", "#673AB7"),
    DEEP_PURPLE("DEEP_PURPLE", "#673AB7"),
    DEEP_PURPLE_DARK("DEEP_PURPLE_DARK", "#512DA8"),
    PURPLE("PURPLE", "#9C27B0"),
    PURPLE_DARK("PURPLE_DARK", "#7B1FA2"),
    PINK("PINK", "#E91E63"),
    PINK_DARK("PINK_DARK", "#D32F2F"),
    RED("RED", "#F44336"),
    RED_DARK("RED_DARK", "#D32F2F");

    private final String name;
    private final String colorHex;
    private static final Map<String, DemoColors> values = new HashMap<>();

    DemoColors(String value, String color) {
        this.name = value;
        this.colorHex = color;
    }

    static {
        for (DemoColors icon : DemoColors.values()) {
            values.put(icon.name, icon);
        }
    }

    public static DemoColors getByName(String colorName) {
        return values.get(colorName);
    }

    public static DemoColors getRandom() {
        return DemoColors.values()[new Random().nextInt(DemoColors.values().length)];
    }

    public String getName() {
        return name;
    }

    public String getColorHex() {
        return colorHex;
    }

    @Override
    public int getColor() {
        return Color.parseColor(colorHex);
    }
}