package com.github.rooneyandshadows.lightbulb.pickers.dialog.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogButtonConfiguration
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseDialogPickerView<DialogType : BasePickerDialogFragment<out EasyAdapterDataModel>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var fragmentManager: FragmentManager
    private val pickerDialog: DialogType by lazy { return@lazy initializeDialog() }
    private val triggerAttachedCallback: MutableList<TriggerAttachedCallback<DialogType>> = mutableListOf()
    private val isPickerDialogShowing: Boolean
        get() = pickerDialog.isDialogShown
    val text: String
        get() = triggerView?.triggerText ?: ""
    var triggerView: DialogPickerTriggerLayout? = null
        set(value) {
            field = value
            addViewInternally(field as View)
        }
    var dialogTag: String = ""
        set(value) {
            field = value
            pickerDialog.setDialogTag(field)
        }
    var pickerDialogPositiveButtonText: String? = null
        set(value) {
            field = value
            pickerDialog.dialogPositiveButton = generateButtonConfig(field)
        }
    var pickerDialogNegativeButtonText: String? = null
        set(value) {
            field = value
            pickerDialog.dialogNegativeButton = generateButtonConfig(field)
        }
    var errorEnabled = false
        set(value) {
            field = value
            triggerView!!.setTriggerErrorEnabled(field)
        }
    var isValidationEnabled = false
    var required = false
        set(value) {
            field = value
            validate()
        }
    var showSelectedTextValue = false
        set(value) {
            field = value
            invalidate()
        }
    var pickerDialogCancelable = false
        set(value) {
            field = value
            pickerDialog.isCancelable = field
        }
    var pickerIcon: Drawable? = null
        set(value) {
            field = value
            triggerView!!.setTriggerIcon(value, null)
        }
    var pickerDialogAnimationType: DialogAnimationTypes = NO_ANIMATION
        set(value) {
            field = value
            pickerDialog.dialogAnimationType = field
        }
    var pickerHintText: String? = null
        set(value) {
            if (!showSelectedTextValue) return
            field = value
            triggerView!!.setTriggerHintText(field)
        }
    var errorText: String? = null
        set(value) {
            field = value
            triggerView!!.setTriggerErrorText(errorText)
        }

    abstract fun validate(): Boolean
    protected abstract val viewText: String
    protected abstract fun initializeDialog(): DialogType
    protected abstract fun readAttributes(context: Context, attrs: AttributeSet?)

    init {
        isSaveEnabled = true
        readAttrs(context, attrs)
        if (!isInEditMode)
            fragmentManager = (context as FragmentActivity).supportFragmentManager
        orientation = VERTICAL
    }

    @Override
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (triggerView != null) triggerView!!.setEnabled(enabled)
    }

    @Override
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        addViewInternally(child)
    }

    fun addOnTriggerAttachedListener(callback: TriggerAttachedCallback<DialogType>) {
        triggerAttachedCallback.add(callback)
    }

    fun showPickerDialog() {
        pickerDialog.show()
    }

    fun setRequired(required: Boolean, validateOnCall: Boolean) {
        this.required = required
        if (validateOnCall) validate()
    }

    fun setPickerIcon(icon: Drawable?, color: Int) {
        pickerIcon = icon
        triggerView!!.setTriggerIcon(icon, color)
    }

    private fun addViewInternally(child: View?) {
        if (child == null) removeAllViews()
        if (triggerView !is DialogPickerTriggerLayout) {
            Log.w(BaseDialogPickerView::class.java.name,
                "Picker view child is ignored. All child views must implement com.rands.lightbulb.pickers.dialog.base.TriggerView")
            return
        }
        removeAllViews()
        super.addView(child)
        triggerView?.apply {
            setEnabled(this@BaseDialogPickerView.isEnabled)
            attachTo(this@BaseDialogPickerView)
            triggerAttachedCallback.forEach {
                it.onAttached(this, this@BaseDialogPickerView)
            }
        }
    }

    private fun generateButtonConfig(buttonText: String?): DialogButtonConfiguration? {
        if (buttonText.isNullOrBlank()) return null
        return DialogButtonConfiguration(buttonTitle = buttonText, buttonEnabled = true, closeDialogOnClick = true)
    }

    private fun updateTextAndValidate() {
        if (!showSelectedTextValue) return
        val newTextValue = viewText
        triggerView?.triggerText = newTextValue
        validate()
    }

    private fun readAttrs(context: Context, attrs: AttributeSet?) {
        readBaseAttributes(context, attrs)
        readAttributes(context, attrs)
    }

    private fun readBaseAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.PickerView_PV_HintText).apply {
                    val default = ""
                    pickerHintText = this ?: default
                }
                getString(R.styleable.PickerView_PV_ErrorText).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_error_text)
                    errorText = this ?: default
                }
                getString(R.styleable.PickerView_PV_DialogButtonPosText).apply {
                    pickerDialogPositiveButtonText = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_positive_button_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getString(R.styleable.PickerView_PV_DialogButtonNegText).apply {
                    pickerDialogNegativeButtonText = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_negative_button_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getString(R.styleable.PickerView_PV_DialogTag).apply {
                    dialogTag = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_dialog_tag_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getInt(R.styleable.PickerView_PV_DialogAnimation, 1).apply {
                    pickerDialogAnimationType = DialogAnimationTypes.valueOf(this)
                }
                errorEnabled = getBoolean(R.styleable.PickerView_PV_ErrorEnabled, false)
                required = getBoolean(R.styleable.PickerView_PV_Required, false)
                isValidationEnabled = getBoolean(R.styleable.PickerView_PV_ValidationEnabled, false)
                pickerDialogCancelable = getBoolean(R.styleable.PickerView_PV_DialogCancelable, true)
                showSelectedTextValue = getBoolean(R.styleable.PickerView_PV_ShowSelectedText, true)
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    @Override
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    @Override
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    @Override
    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.isDialogShown = isPickerDialogShowing
        myState.pickerHintText = pickerHintText
        myState.pickerErrorText = errorText
        myState.pickerIsRequired = required
        myState.pickerIsErrorEnabled = errorEnabled
        myState.pickerIsValidationEnabled = isValidationEnabled
        myState.pickerShowSelectedTextValue = showSelectedTextValue
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        val isPickerDialogShowing = savedState.isDialogShown
        pickerHintText = savedState.pickerHintText
        errorText = savedState.pickerErrorText
        required = savedState.pickerIsRequired
        errorEnabled = savedState.pickerIsErrorEnabled
        isValidationEnabled = savedState.pickerIsValidationEnabled
        showSelectedTextValue = savedState.pickerShowSelectedTextValue
        updateTextAndValidate()
        if (isPickerDialogShowing) showPickerDialog()
    }

    private class SavedState : BaseSavedState {
        var isDialogShown = false
        var pickerHintText: String? = null
        var pickerErrorText: String? = null
        var pickerIsRequired = false
        var pickerIsErrorEnabled = false
        var pickerIsValidationEnabled = false
        var pickerShowSelectedTextValue = false

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            isDialogShown = parcel.readByte().toInt() != 0
            pickerHintText = parcel.readString()
            pickerErrorText = parcel.readString()
            pickerIsRequired = parcel.readByte().toInt() != 0
            pickerIsErrorEnabled = parcel.readByte().toInt() != 0
            pickerIsValidationEnabled = parcel.readByte().toInt() != 0
            pickerShowSelectedTextValue = parcel.readByte().toInt() != 0
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (isDialogShown) 1 else 0).toByte())
            out.writeString(pickerHintText)
            out.writeString(pickerErrorText)
            out.writeByte((if (pickerIsRequired) 1 else 0).toByte())
            out.writeByte((if (pickerIsErrorEnabled) 1 else 0).toByte())
            out.writeByte((if (pickerIsValidationEnabled) 1 else 0).toByte())
            out.writeByte((if (pickerShowSelectedTextValue) 1 else 0).toByte())
        }

        @Override
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    interface TriggerAttachedCallback<DialogType : BasePickerDialogFragment<out EasyAdapterDataModel>> {
        fun onAttached(triggerView: DialogPickerTriggerLayout, pickerView: BaseDialogPickerView<DialogType>)
    }
}