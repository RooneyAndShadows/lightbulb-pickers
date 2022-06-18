package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color;


import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.colors.DemoColors;

import java.util.ArrayList;

public class AppColorUtils {

    public static int getColor(IDemoColor desiredColor) {
        return desiredColor.getColor();
    }

    public static ArrayList<ColorPickerAdapter.ColorModel> getAllForPicker() {
        ArrayList<ColorPickerAdapter.ColorModel> result = new ArrayList<>();
        for (DemoColors color : DemoColors.values())
            result.add(new ColorPickerAdapter.ColorModel(color.getColorHex(), color.getName()));
        return result;
    }
}
