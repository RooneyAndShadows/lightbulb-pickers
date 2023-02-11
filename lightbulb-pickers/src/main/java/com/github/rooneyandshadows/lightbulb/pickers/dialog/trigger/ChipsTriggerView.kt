package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.setPadding
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView
import com.nex3z.flowlayout.FlowLayout

@Suppress("MemberVisibilityCanBePrivate")
class ChipsTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : DialogTriggerView(context, attrs, defStyleAttr) {
    private lateinit var flowLayout: FlowLayout
    private lateinit var errorTextView: AppCompatTextView

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
        inflateView()
        setupView()
    }

    @Override
    override fun isClickable(): Boolean {
        return true
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
        //Not supported
    }

    @Override
    override fun onIconColorChange() {
        //Not supported
    }

    @Override
    override fun onErrorEnabledChange() {
        errorTextView.apply {
            visibility = if (errorEnabled) VISIBLE else GONE
        }
    }

    @Override
    override fun onTextChange() {
        println(text)
        updateChips()
    }

    @Override
    override fun onHintTextChange() {
        //buttonView.apply {
        //    text = hintText
        //}
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
        //NOT SUPPORTED
    }

    @Override
    override fun onEnabledChange() {
        // buttonView.isEnabled = isEnabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        setOnClickListener {
            println("sssssssssssss")
            requirePickerView().showPickerDialog()
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
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
    }

    private fun inflateView() {
        inflate(context, R.layout.dialog_picker_chips_layout, this) as LinearLayoutCompat
        flowLayout = findViewById(R.id.chipsContainer)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
    }

    private fun setupView() {
        val padding = ResourceUtils.getDimenPxById(context, R.dimen.trigger_view_chips_items_spacing)
        orientation = VERTICAL
        isClickable = true
        background = ResourceUtils.getDrawable(context, R.drawable.dialog_chips_trigger_bg)
        setPadding(padding)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonTriggerView, 0, 0)
        try {
            attrTypedArray.apply {

            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun updateChips() {
        flowLayout.apply {
            removeAllViews()
            text?.apply {
                split(",").forEach { chipTitle ->
                    addView(inflateChip(chipTitle))
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    fun inflateChip(title: String): View {
        return LayoutInflater.from(context).inflate(R.layout.dialog_picker_chip_item, null).apply {
            val titleTextView: TextView = findViewById(R.id.chip_item_text_view)
            titleTextView.text = title
        }
    }

    private class SavedState : BaseSavedState {
        var buttonTextColor = -1
        var buttonBackgroundColor = -1
        var buttonBackgroundCornerRadius = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                buttonTextColor = ParcelUtils.readInt(this)!!
                buttonBackgroundColor = ParcelUtils.readInt(this)!!
                buttonBackgroundCornerRadius = ParcelUtils.readInt(this)!!
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                ParcelUtils.writeInt(this, buttonTextColor)
                ParcelUtils.writeInt(this, buttonBackgroundColor)
                ParcelUtils.writeInt(this, buttonBackgroundCornerRadius)
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