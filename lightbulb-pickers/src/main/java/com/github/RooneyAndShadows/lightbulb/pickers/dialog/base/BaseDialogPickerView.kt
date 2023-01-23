package com.github.rooneyandshadows.lightbulb.pickers.dialog.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes.NO_ANIMATION
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogButtonConfiguration
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.NORMAL
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogButtonClickListener
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogCancelListener
import com.github.rooneyandshadows.lightbulb.pickers.R

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseDialogPickerView<SelectionType> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var fragmentManager: FragmentManager
    private val validationCallbacks: MutableList<ValidationCheck<SelectionType>> = mutableListOf()
    private val selectionChangedListeners: MutableList<SelectionChangedListener<SelectionType>> = mutableListOf()
    private val triggerAttachedCallback: MutableList<TriggerAttachedCallback<SelectionType>> = mutableListOf()
    protected var dataBindingListener: SelectionChangedListener<SelectionType>? = null
    private val isPickerDialogShowing: Boolean
        get() = pickerDialog.isDialogShown
    protected val pickerDialog: BasePickerDialogFragment<SelectionType> by lazy {
        val dialog = initializeDialog(fragmentManager)
        onDialogInitialized(dialog)
        return@lazy dialog
    }
    val text: String
        get() = triggerView?.triggerText ?: ""
    var isValidationEnabled = false
    var pickerRequiredText: String? = null
    var triggerView: DialogPickerTriggerLayout? = null
        set(value) {
            field = value
            addViewInternally(field as View)
        }
    var pickerDialogTag: String = ""
        set(value) {
            field = value
            pickerDialog.setDialogTag(field)
        }
    var pickerDialogTitle: String? = null
        set(value) {
            field = value
            pickerDialog.dialogTitle = field
        }
        get() = pickerDialog.dialogTitle
    var pickerDialogMessage: String? = null
        set(value) {
            field = value
            pickerDialog.dialogMessage = field
        }
        get() = pickerDialog.dialogTitle
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
    var pickerDialogType: DialogTypes = NORMAL
        set(value) {
            field = value
            pickerDialog.dialogType = field
        }
        get() = pickerDialog.dialogType
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
    val hasSelection: Boolean
        get() = pickerDialog.hasSelection()
    open var selection: SelectionType?
        set(value) {
            pickerDialog.setSelection(value)
        }
        get() = pickerDialog.getSelection()

    protected abstract val viewText: String
    protected abstract fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<SelectionType>
    protected abstract fun readAttributes(context: Context, attrs: AttributeSet?)

    protected open fun validate(): Boolean {
        var isValid = true
        if (isValidationEnabled) {
            if (required && !hasSelection) {
                errorEnabled = true
                errorText = pickerRequiredText
                return false
            }
            validationCallbacks.forEach {
                isValid = isValid and it.validate(selection)
                if (!isValid) return@forEach
            }
        }
        if (!isValid) errorEnabled = true
        else {
            errorEnabled = false
            errorText = null
        }
        return isValid
    }

    protected open fun onDialogInitialized(dialog: BasePickerDialogFragment<SelectionType>) {
        dialog.apply {
            addOnPositiveClickListener(object : DialogButtonClickListener {
                override fun doOnClick(buttonView: View?, dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnNegativeClickListeners(object : DialogButtonClickListener {
                override fun doOnClick(buttonView: View?, dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnCancelListener(object : DialogCancelListener {
                override fun doOnCancel(dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnSelectionChangedListener(object : BasePickerDialogFragment.SelectionChangedListener<SelectionType> {
                override fun onSelectionChanged(
                    dialog: BasePickerDialogFragment<SelectionType>,
                    oldValue: SelectionType?,
                    newValue: SelectionType?
                ) {
                    updateTextAndValidate()
                    dispatchSelectionChangedEvents(oldValue, newValue)
                }
            })
        }
    }

    init {
        isSaveEnabled = true
        if (!isInEditMode)
            fragmentManager = (context as FragmentActivity).supportFragmentManager
        readAttrs(context, attrs)
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

    protected fun updateTextAndValidate() {
        if (!showSelectedTextValue) return
        val newTextValue = viewText
        triggerView?.triggerText = newTextValue
        validate()
    }

    fun addSelectionChangedListener(listener: SelectionChangedListener<SelectionType>) {
        selectionChangedListeners.add(listener)
    }

    fun removeSelectionChangedListener(listener: SelectionChangedListener<SelectionType>) {
        selectionChangedListeners.add(listener)
    }

    fun removeAllSelectionChangedListeners() {
        selectionChangedListeners.clear()
    }

    fun addValidationCheck(validationCheck: ValidationCheck<SelectionType>) {
        validationCallbacks.add(validationCheck)
    }

    fun removeValidationCheck(validationCheck: ValidationCheck<SelectionType>) {
        validationCallbacks.remove(validationCheck)
    }

    fun removeAllValidationChecks() {
        validationCallbacks.clear()
    }

    fun addOnTriggerAttachedListener(callback: TriggerAttachedCallback<SelectionType>) {
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
            Log.w(
                BaseDialogPickerView::class.java.name,
                "Picker view child is ignored. All child views must implement com.rands.lightbulb.pickers.dialog.base.TriggerView"
            )
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

    private fun readAttrs(context: Context, attrs: AttributeSet?) {
        readBaseAttributes(context, attrs)
        readAttributes(context, attrs)
    }

    private fun readBaseAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.PickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.PickerView_PV_DialogTitle).apply {
                    val default = ""
                    pickerDialogTitle = this ?: default
                }
                getString(R.styleable.PickerView_PV_DialogMessage).apply {
                    val default = ""
                    pickerDialogMessage = this ?: default
                }
                getString(R.styleable.PickerView_PV_HintText).apply {
                    val default = ""
                    pickerHintText = this ?: default
                }
                getString(R.styleable.PickerView_PV_ErrorText).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_error_text)
                    errorText = this ?: default
                }
                getString(R.styleable.PickerView_PV_RequiredText).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_required_text)
                    pickerRequiredText = this ?: default
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
                    pickerDialogTag = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_dialog_tag_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getInt(R.styleable.PickerView_PV_DialogType, NORMAL.value).apply {
                    pickerDialogType = DialogTypes.valueOf(this)
                }
                getInt(R.styleable.PickerView_PV_DialogAnimation, NO_ANIMATION.value).apply {
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

    private fun dispatchSelectionChangedEvents(newValue: SelectionType?, oldValue: SelectionType?) {
        selectionChangedListeners.forEach {
            it.execute(newValue, oldValue)
        }
        dataBindingListener?.execute(newValue, oldValue)
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
        myState.pickerRequiredText = pickerRequiredText
        myState.pickerIsRequired = required
        myState.pickerIsErrorEnabled = errorEnabled
        myState.pickerIsValidationEnabled = isValidationEnabled
        myState.pickerShowSelectedTextValue = showSelectedTextValue
        myState.dialogState = pickerDialog.saveDialogState()
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        val isPickerDialogShowing = savedState.isDialogShown
        pickerHintText = savedState.pickerHintText
        errorText = savedState.pickerErrorText
        pickerRequiredText = savedState.pickerRequiredText
        required = savedState.pickerIsRequired
        errorEnabled = savedState.pickerIsErrorEnabled
        isValidationEnabled = savedState.pickerIsValidationEnabled
        showSelectedTextValue = savedState.pickerShowSelectedTextValue
        pickerDialog.restoreDialogState(savedState.dialogState)
        updateTextAndValidate()
        if (isPickerDialogShowing) showPickerDialog()
    }

    interface SelectionChangedListener<SelectionType> {
        fun execute(newSelection: SelectionType?, oldSelection: SelectionType?)
    }

    interface ValidationCheck<SelectionType> {
        fun validate(currentSelection: SelectionType?): Boolean
    }

    interface TriggerAttachedCallback<SelectionType> {
        fun onAttached(triggerView: DialogPickerTriggerLayout, pickerView: BaseDialogPickerView<SelectionType>)
    }

    private class SavedState : BaseSavedState {
        var isDialogShown = false
        var pickerHintText: String? = null
        var pickerErrorText: String? = null
        var pickerRequiredText: String? = null
        var pickerIsRequired = false
        var pickerIsErrorEnabled = false
        var pickerIsValidationEnabled = false
        var pickerShowSelectedTextValue = false
        var dialogState: Bundle? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            isDialogShown = parcel.readByte().toInt() != 0
            pickerHintText = parcel.readString()
            pickerErrorText = parcel.readString()
            pickerRequiredText = parcel.readString()
            pickerIsRequired = parcel.readByte().toInt() != 0
            pickerIsErrorEnabled = parcel.readByte().toInt() != 0
            pickerIsValidationEnabled = parcel.readByte().toInt() != 0
            pickerShowSelectedTextValue = parcel.readByte().toInt() != 0
            dialogState = parcel.readBundle(Bundle::class.java.classLoader)
        }

        @Override
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte((if (isDialogShown) 1 else 0).toByte())
            parcel.writeString(pickerHintText)
            parcel.writeString(pickerErrorText)
            parcel.writeString(pickerRequiredText)
            parcel.writeByte((if (pickerIsRequired) 1 else 0).toByte())
            parcel.writeByte((if (pickerIsErrorEnabled) 1 else 0).toByte())
            parcel.writeByte((if (pickerIsValidationEnabled) 1 else 0).toByte())
            parcel.writeByte((if (pickerShowSelectedTextValue) 1 else 0).toByte())
            parcel.writeBundle(dialogState)
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