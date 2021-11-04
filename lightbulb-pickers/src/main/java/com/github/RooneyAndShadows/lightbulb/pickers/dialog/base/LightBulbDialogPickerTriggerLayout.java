package com.github.rooneyandshadows.lightbulb.pickers.dialog.base;

import android.graphics.drawable.Drawable;

public interface LightBulbDialogPickerTriggerLayout {

    void attachTo(LightBulbDialogPickerView pickerView);

    void setTriggerIcon(Drawable icon, Integer color);

    void setTriggerText(String newText);

    void setTriggerErrorText(String errorText);

    void setTriggerHintText(String hintText);

    void setTriggerErrorEnabled(boolean errorEnabled);

    String getTriggerText();
}