package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout

class ImageButtonTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), DialogPickerTriggerLayout {
    private val IMAGE_BUTTON_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ImageButtonTag)
    private val IMAGE_BUTTON_ERROR_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ErrorTextTag)
    private var pickerView: BaseDialogPickerView? = null
    private var rootView: LinearLayout? = null
    protected var iconButtonView: AppCompatImageButton? = null
    protected var errorTextView: AppCompatTextView? = null
    protected var pickerIcon: Drawable? = null
    protected var pickerBackgroundColor: Int? = null
    protected var pickerStartIconColor = 0
    protected var defaultIconColor = 0
    protected var errorAppearance = 0
    private var hasDefinedBoxBackgroundAttribute = false

    init {
        readAttributes(context, attrs)
        initView()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (iconButtonView != null) iconButtonView!!.isEnabled = enabled
    }

    override fun attachTo(pickerView: BaseDialogPickerView) {
        this.pickerView = pickerView
        iconButtonView!!.setOnClickListener { v: View? -> pickerView.showPickerDialog() }
        iconButtonView!!.isEnabled = isEnabled
        val errorEnabled = pickerView.isErrorEnabled
        setTriggerErrorEnabled(errorEnabled)
        if (errorEnabled) errorTextView!!.error = pickerView.errorText
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
        //NOT SUPPORTED
    }

    override fun setTriggerErrorEnabled(errorEnabled: Boolean) {
        errorTextView!!.visibility = if (errorEnabled) VISIBLE else GONE
    }

    //NOT SUPPORTED
    override var triggerText: String
        get() = ""
        set(newTextValue) {
            //NOT SUPPORTED
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
        myState.pickerStartIconColor = pickerStartIconColor
        myState.defaultIconColor = defaultIconColor
        return myState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        pickerBackgroundColor = savedState.pickerBackgroundColor
        pickerStartIconColor = savedState.pickerStartIconColor
        defaultIconColor = savedState.defaultIconColor
        setupViews()
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ImageButtonTriggerView, 0, 0)
        try {
            pickerBackgroundColor = ResourceUtils.readNullableColorAttributeFromTypedArray(getContext(),
                a,
                R.styleable.ImageButtonTriggerView_IBTV_BackgroundColor)
            hasDefinedBoxBackgroundAttribute = a.hasValue(R.styleable.ImageButtonTriggerView_IBTV_BackgroundColor)
            if (pickerBackgroundColor == null) pickerBackgroundColor = Color.TRANSPARENT
            errorAppearance = a.getResourceId(R.styleable.ImageButtonTriggerView_IBTV_ErrorTextAppearance,
                R.style.PickerViewErrorTextAppearance)
            defaultIconColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface)
            pickerStartIconColor = a.getColor(R.styleable.ImageButtonTriggerView_IBTV_StartIconColor, defaultIconColor)
        } finally {
            a.recycle()
        }
    }

    private fun initView() {
        isSaveEnabled = true
        renderLayout()
        setupViews()
    }

    private fun renderLayout() {
        rootView = inflate(context, R.layout.dialog_picker_image_button_layout, this) as LinearLayout
        iconButtonView = rootView!!.findViewWithTag(IMAGE_BUTTON_TAG)
        errorTextView = rootView!!.findViewWithTag(IMAGE_BUTTON_ERROR_TEXT_TAG)
    }

    private fun setupViews() {
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
        if (hasDefinedBoxBackgroundAttribute && pickerBackgroundColor == Color.TRANSPARENT) iconButtonView!!.setBackgroundColor(
            Color.TRANSPARENT)
    }

    private fun setupStartIcon() {
        if (pickerIcon != null) pickerIcon!!.setTint(pickerStartIconColor)
        iconButtonView!!.setImageDrawable(pickerIcon)
    }

    private fun setupErrorAppearance() {
        errorTextView!!.setTextAppearance(context, errorAppearance)
    }

    private class SavedState : BaseSavedState {
        var pickerBackgroundColor = 0
        private var errorAppearance = 0
        private var backgroundRadius = 0
        var pickerStartIconColor = 0
        var defaultIconColor = 0
        private var textColor = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            pickerBackgroundColor = `in`.readInt()
            errorAppearance = `in`.readInt()
            backgroundRadius = `in`.readInt()
            pickerStartIconColor = `in`.readInt()
            defaultIconColor = `in`.readInt()
            textColor = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(pickerBackgroundColor)
            out.writeInt(errorAppearance)
            out.writeInt(backgroundRadius)
            out.writeInt(pickerStartIconColor)
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