package com.github.RooneyAndShadows.lightbulb.pickers.dialog.base;

import android.graphics.drawable.Drawable;

public interface DialogPickerTriggerLayout {

    void attachTo(BaseDialogPickerView pickerView);

    void setTriggerIcon(Drawable icon, Integer color);

    void setTriggerText(String newText);

    void setTriggerErrorText(String errorText);

    void setTriggerHintText(String hintText);

    void setTriggerErrorEnabled(boolean errorEnabled);

    String getTriggerText();
}