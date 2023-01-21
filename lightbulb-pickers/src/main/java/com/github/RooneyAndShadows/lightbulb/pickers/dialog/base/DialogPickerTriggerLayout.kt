package com.github.rooneyandshadows.lightbulb.pickers.dialog.base

import android.graphics.drawable.Drawable

interface DialogPickerTriggerLayout {
    fun attachTo(pickerView: BaseDialogPickerView<*>)
    fun setEnabled(enabled: Boolean)
    fun setTriggerIcon(icon: Drawable?, color: Int?)
    fun setTriggerErrorText(errorText: String?)
    fun setTriggerHintText(hintText: String?)
    fun setTriggerErrorEnabled(errorEnabled: Boolean)
    var triggerText: String
}