package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout
import com.google.android.material.button.MaterialButton

class ButtonTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), DialogPickerTriggerLayout {
    private val BUTTON_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ButtonTag)
    private val BUTTON_ERROR_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ErrorTextTag)
    private var pickerView: BaseDialogPickerView? = null
    private var rootView: LinearLayout? = null
    private var buttonView: MaterialButton? = null
    protected var errorTextView: AppCompatTextView? = null
    protected var pickerIcon: Drawable? = null
    protected var pickerBackgroundColor: Int? = null
    protected var pickerStartIconColor = 0
    protected var defaultIconColor = 0
    protected var errorAppearance = 0
    protected var cornerRadius = 0
    private var hasDefinedBoxBackgroundAttribute = false

    init {
        readAttributes(context, attrs)
        initializeView()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (buttonView != null) buttonView!!.isEnabled = enabled
    }

    override fun attachTo(pickerView: BaseDialogPickerView) {
        this.pickerView = pickerView
        buttonView!!.setOnClickListener { v: View? -> pickerView.showPickerDialog() }
        val errorEnabled = pickerView.isErrorEnabled
        setTriggerErrorEnabled(errorEnabled)
        if (errorEnabled) buttonView!!.error = pickerView.errorText
        buttonView!!.isEnabled = isEnabled
        setTriggerHintText(pickerView.pickerHintText)
    }

    override fun setTriggerIcon(icon: Drawable?, iconColor: Int?) {
        pickerIcon = icon
        setPickerStartIconColor(iconColor)
    }

    override fun setTriggerErrorText(errorText: String?) {
        errorTextView!!.text = errorText
    }

    override fun setTriggerHintText(hintText: String?) {
        buttonView!!.text = hintText
    }

    override fun setTriggerErrorEnabled(errorEnabled: Boolean) {
        errorTextView!!.visibility = if (errorEnabled) VISIBLE else GONE
    }

    override var triggerText: String
        get() = buttonView!!.text.toString()
        set(newTextValue) {
            if (StringUtils.isNullOrEmptyString(newTextValue)) buttonView.setText(pickerView.getPickerHintText()) else buttonView!!.text =
                newTextValue
        }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.pickerBackgroundColor = pickerBackgroundColor!!
        myState.defaultIconColor = defaultIconColor
        return myState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        pickerBackgroundColor = savedState.pickerBackgroundColor
        defaultIconColor = savedState.defaultIconColor
        setupInputLayout()
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonTriggerView, 0, 0)
        try {
            pickerBackgroundColor = ResourceUtils.readNullableColorAttributeFromTypedArray(getContext(),
                a,
                R.styleable.ButtonTriggerView_BTV_BackgroundColor)
            hasDefinedBoxBackgroundAttribute = a.hasValue(R.styleable.ButtonTriggerView_BTV_BackgroundColor)
            if (pickerBackgroundColor == null) pickerBackgroundColor = Color.TRANSPARENT
            cornerRadius = ResourceUtils.dpToPx(a.getInt(R.styleable.ButtonTriggerView_BTV_BackgroundRadius, 5))
            errorAppearance =
                a.getResourceId(R.styleable.ButtonTriggerView_BTV_ErrorTextAppearance, R.style.PickerViewErrorTextAppearance)
            defaultIconColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnPrimary)
            pickerStartIconColor = a.getColor(R.styleable.ButtonTriggerView_BTV_StartIconColor, defaultIconColor)
        } finally {
            a.recycle()
        }
    }

    private fun initializeView() {
        isSaveEnabled = true
        renderLayout()
        setupInputLayout()
    }

    private fun renderLayout() {
        rootView = inflate(context, R.layout.dialog_picker_button_layout, this) as LinearLayout
        buttonView = rootView!!.findViewWithTag(BUTTON_TAG)
        errorTextView = rootView!!.findViewWithTag(BUTTON_ERROR_TEXT_TAG)
    }

    private fun setupInputLayout() {
        setupBackground()
        setupStartIcon()
        setupErrorAppearance()
    }

    fun setPickerStartIconColor(pickerStartIconColor: Int?) {
        this.pickerStartIconColor = pickerStartIconColor ?: defaultIconColor
        setupStartIcon()
    }

    fun setPickerBackgroundColor(pickerBackgroundColor: Int?) {
        this.pickerBackgroundColor = pickerBackgroundColor
        setupBackground()
    }

    fun setErrorAppearance(errorAppearance: Int) {
        this.errorAppearance = errorAppearance
        setupErrorAppearance()
    }

    private fun setupBackground() {
        if (hasDefinedBoxBackgroundAttribute && pickerBackgroundColor == Color.TRANSPARENT) buttonView!!.setBackgroundColor(
            Color.TRANSPARENT)
        buttonView!!.cornerRadius = cornerRadius
    }

    private fun setupStartIcon() {
        buttonView!!.iconTint = ColorStateList(arrayOf(intArrayOf()), intArrayOf(
            pickerStartIconColor
        ))
        buttonView!!.icon = pickerIcon
    }

    private fun setupErrorAppearance() {
        errorTextView!!.setTextAppearance(context, errorAppearance)
    }

    private class SavedState : BaseSavedState {
        var pickerBackgroundColor = 0
        private var errorAppearance = 0
        private var backgroundRadius = 0
        private var startIconColor = 0
        var defaultIconColor = 0
        private var textColor = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            pickerBackgroundColor = `in`.readInt()
            errorAppearance = `in`.readInt()
            backgroundRadius = `in`.readInt()
            startIconColor = `in`.readInt()
            defaultIconColor = `in`.readInt()
            textColor = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(pickerBackgroundColor)
            out.writeInt(errorAppearance)
            out.writeInt(backgroundRadius)
            out.writeInt(startIconColor)
            out.writeInt(defaultIconColor)
            out.writeInt(textColor)
        }

        companion object {
            val CREATOR: Creator<SavedState> = object : Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}