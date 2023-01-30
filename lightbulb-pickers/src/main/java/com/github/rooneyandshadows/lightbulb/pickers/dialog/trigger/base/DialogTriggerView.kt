package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView

@Suppress("MemberVisibilityCanBePrivate")
abstract class DialogTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    protected lateinit var pickerView: BaseDialogPickerView<*>
    var icon: Drawable? = null
        private set
    var errorEnabled: Boolean = false
        private set
    var text: String? = null
        private set
    var hintText: String? = null
        private set
    var errorText: String? = null
        private set
    var iconColor: Int = -1
        private set
    var errorTextAppearance = 0
        private set
    var hintTextAppearance = 0
        private set
    protected val defaultIconColor: Int by lazy {
        initializeDefaultIconColor()
    }

    protected abstract fun inflateView()
    protected abstract fun readAttributes(context: Context, attrs: AttributeSet?)
    protected abstract fun onIconChange()
    protected abstract fun onIconColorChange()
    protected abstract fun onErrorEnabledChange()
    protected abstract fun onTextChange()
    protected abstract fun onHintTextChange()
    protected abstract fun onErrorTextChange()
    protected abstract fun onErrorTextAppearanceChange()
    protected abstract fun onHintTextAppearanceChange()
    protected abstract fun onEnabledChange()
    abstract fun attachTo(pickerView: BaseDialogPickerView<*>)

    protected open fun setupView() {
    }

    protected open fun initializeDefaultIconColor(): Int {
        return ResourceUtils.getColorByAttribute(
            context,
            R.attr.colorPrimary
        )
    }

    init {
        isSaveEnabled = true
        initializeView(context, attrs)
    }

    @Override
    override fun isEnabled(): Boolean {
        return super.isEnabled()
    }

    @Override
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        onEnabledChange()
    }

    @JvmOverloads
    fun setIcon(icon: Drawable?, iconColor: Int = -1) {
        this.iconColor = iconColor
        this.icon = icon?.apply {
            val color = if (iconColor == -1) defaultIconColor else iconColor
            setTint(color)
        }
        onIconChange()
    }

    fun setErrorEnabled(errorEnabled: Boolean) {
        this.errorEnabled = errorEnabled
        onErrorEnabledChange()
    }

    fun setText(text: String?) {
        this.text = text
        onTextChange()
    }

    fun setHintText(hintText: String?) {
        this.hintText = hintText
        onHintTextChange()
    }

    fun setErrorText(errorText: String?) {
        this.errorText = errorText
        onErrorTextChange()
    }

    fun setIconColor(iconColor: Int) {
        this.iconColor = iconColor
        onIconColorChange()
    }

    fun setErrorTextAppearance(errorTextAppearance: Int) {
        this.errorTextAppearance = errorTextAppearance
        onErrorTextAppearanceChange()
    }

    fun setHintTextAppearance(hintTextAppearance: Int) {
        this.hintTextAppearance = hintTextAppearance
        onHintTextAppearanceChange()
    }

    fun requirePickerView(): BaseDialogPickerView<*> {
        if (!this::pickerView.isInitialized)
            throw Exception("ButtonTriggerView is not attached to picker.")
        return pickerView
    }

    private fun initializeView(context: Context, attrs: AttributeSet?) {
        readAttrs(context, attrs)
        inflateView()
        setupView()
        syncUserInterface()
    }

    private fun syncUserInterface() {
        onIconChange()
        onIconColorChange()
        onErrorEnabledChange()
        onTextChange()
        onHintTextChange()
        onErrorTextChange()
        onErrorTextAppearanceChange()
        onHintTextAppearanceChange()
        onEnabledChange()
    }

    private fun readAttrs(context: Context, attrs: AttributeSet?) {
        readBaseAttributes(context, attrs)
        readAttributes(context, attrs)
    }

    private fun readBaseAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                errorEnabled = getBoolean(R.styleable.DialogTriggerView_dtv_error_enabled, false)
                getString(R.styleable.DialogTriggerView_dtv_error_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_error_text)
                    errorText = this ?: default
                }
                getString(R.styleable.DialogTriggerView_dtv_hint_text).apply {
                    val default = ""
                    hintText = this ?: default
                }
                getResourceId(
                    R.styleable.DialogTriggerView_dtv_error_text_appearance,
                    R.style.PickerViewErrorTextAppearance
                ).apply {
                    errorTextAppearance = this
                }
                getResourceId(
                    R.styleable.DialogTriggerView_dtv_hint_text_appearance,
                    R.style.PickerViewHintTextAppearance
                ).apply {
                    hintTextAppearance = this
                }
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
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.apply {
            val view = this@DialogTriggerView
            errorEnabled = view.errorEnabled
            text = view.text
            hintText = view.hintText
            errorText = view.errorText
            errorTextAppearance = view.errorTextAppearance
            hintTextAppearance = view.hintTextAppearance
            iconColor = view.iconColor
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        savedState.apply {
            val view = this@DialogTriggerView
            view.setErrorEnabled(errorEnabled)
            view.setText(text)
            view.setHintText(hintText)
            view.setErrorText(errorText)
            view.setErrorTextAppearance(errorTextAppearance)
            view.setHintTextAppearance(hintTextAppearance)
            view.setIconColor(iconColor)
        }
    }

    private class SavedState : BaseSavedState {
        var errorEnabled: Boolean = false
        var text: String? = null
        var hintText: String? = null
        var errorText: String? = null
        var errorTextAppearance: Int = -1
        var hintTextAppearance: Int = -1
        var iconColor: Int = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                ParcelUtils.writeBoolean(this, errorEnabled)
                    .writeString(this, text)
                    .writeString(this, hintText)
                    .writeString(this, errorText)
                    .writeInt(this, errorTextAppearance)
                    .writeInt(this, hintTextAppearance)
                    .writeInt(this, iconColor)
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                errorEnabled = ParcelUtils.readBoolean(this)!!
                text = ParcelUtils.readString(this)
                hintText = ParcelUtils.readString(this)
                errorText = ParcelUtils.readString(this)
                errorTextAppearance = ParcelUtils.readInt(this)!!
                hintTextAppearance = ParcelUtils.readInt(this)!!
                iconColor = ParcelUtils.readInt(this)!!
            }
        }

        @Override
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}