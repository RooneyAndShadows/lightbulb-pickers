package com.github.RooneyAndShadows.lightbulb.pickers.dialog.trigger.base

import android.graphics.drawable.Drawable
import com.github.RooneyAndShadows.lightbulb.pickers.dialog.base.BaseDialogPickerView

interface DialogPickerTriggerLayout {
    fun attachTo(pickerView: BaseDialogPickerView<*>)
    fun setEnabled(enabled: Boolean)
    fun setTriggerIcon(icon: Drawable?, color: Int?)
    fun setTriggerErrorText(errorText: String?)
    fun setTriggerHintText(hintText: String?)
    fun setTriggerErrorEnabled(errorEnabled: Boolean)
    var triggerText: String
}