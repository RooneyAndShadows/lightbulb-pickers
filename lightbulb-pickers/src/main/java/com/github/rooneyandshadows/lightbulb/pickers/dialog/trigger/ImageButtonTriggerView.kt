package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView

@Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
class ImageButtonTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : DialogTriggerView(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var iconButtonView: AppCompatImageButton
    private lateinit var errorTextView: AppCompatTextView
    var buttonBackgroundColor: Int = -1
        private set

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
        inflateView()
        syncUserInterface()
        setupBackground()
    }

    @Override
    override fun initializeDefaultIconColor(): Int {
        return ResourceUtils.getColorByAttribute(
            context,
            R.attr.colorOnPrimary
        )
    }

    @Override
    override fun onIconChange() {
        val color = if (iconColor == -1) defaultIconColor else iconColor
        if (icon != null)
            icon!!.setTint(color)
        iconButtonView.setImageDrawable(icon)
    }

    @Override
    override fun onIconColorChange() {
        iconColor.apply color@{
            iconButtonView.drawable?.apply {
                val colorToSet = if (this@color == -1) defaultIconColor else this@color
                setTint(colorToSet)
            }
        }
    }

    @Override
    override fun onErrorEnabledChange() {
        errorTextView.visibility = if (errorEnabled) VISIBLE else GONE
    }

    @Override
    override fun onTextChange() {
        // NOT SUPPORTED
    }

    @Override
    override fun onHintTextChange() {
        // NOT SUPPORTED
    }

    @Override
    override fun onErrorTextChange() {
        errorTextView.apply {
            text = errorText
        }
    }

    @Override
    override fun onErrorTextAppearanceChange() {
        errorTextView.apply {
            setTextAppearance(errorTextAppearance)
        }
    }

    @Override
    override fun onHintTextAppearanceChange() {
        // NOT SUPPORTED
    }

    @Override
    override fun onEnabledChange() {
        iconButtonView.isEnabled = isEnabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        iconButtonView.setOnClickListener { requirePickerView().showPickerDialog() }
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
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.buttonBackgroundColor = buttonBackgroundColor
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        setButtonBackgroundColor(savedState.buttonBackgroundColor)
    }

    fun setButtonBackgroundColor(backgroundColor: Int) {
        buttonBackgroundColor = backgroundColor
        setupBackground()
    }

    private fun setupBackground() {
        iconButtonView.apply {
            if (buttonBackgroundColor == -1) setBackgroundColor(Color.TRANSPARENT)
            else setBackgroundColor(buttonBackgroundColor)
        }
    }

    private fun inflateView() {
        inflate(context, R.layout.dialog_picker_image_button_layout, this) as LinearLayout
        iconButtonView = findViewById(R.id.picker_view_image_button)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ImageButtonTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                getColor(R.styleable.ImageButtonTriggerView_ibtv_background_color, Color.TRANSPARENT).apply {
                    buttonBackgroundColor = this
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private class SavedState : BaseSavedState {
        var buttonBackgroundColor = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                buttonBackgroundColor = ParcelUtils.readInt(this)!!
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                ParcelUtils.writeInt(this, buttonBackgroundColor)
            }
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
}