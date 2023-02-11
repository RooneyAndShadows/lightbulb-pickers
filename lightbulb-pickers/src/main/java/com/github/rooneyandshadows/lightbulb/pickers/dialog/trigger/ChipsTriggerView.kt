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
import android.view.View.MeasureSpec.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
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
    private lateinit var flowLayoutContainer: RelativeLayout
    private lateinit var flowLayout: FlowLayout
    private lateinit var errorTextView: AppCompatTextView
    private lateinit var hintTextView: AppCompatTextView
    private var nMoreItemsFormat: String = ""
    private var maxRows = DEFAULT_MAX_ROWS
    private val emptyLayoutHeight by lazy {
        return@lazy inflateChip("Chip").let {
            val widthMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            measure(widthMeasureSpec, heightMeasureSpec)
            return@let measuredHeight + flowLayout.paddingTop + flowLayout.paddingBottom
        }
    }

    companion object {
        private const val DEFAULT_MAX_ROWS = 2
    }

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
        syncChips()
    }

    @Override
    override fun onHintTextChange() {
        hintTextView.apply {
            text = hintText
        }
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
        hintTextView.apply {
            setTextAppearance(hintTextAppearance)
        }
    }

    @Override
    override fun onEnabledChange() {
        // buttonView.isEnabled = isEnabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        flowLayoutContainer.apply {
            setOnClickListener {
                requirePickerView().showPickerDialog()
            }
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
        flowLayoutContainer = findViewById(R.id.flowLayoutContainer)
        flowLayout = findViewById(R.id.chipsContainer)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
        hintTextView = findViewById(R.id.picker_view_hint_text_view)
    }

    private fun setupView() {
        orientation = VERTICAL
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ChipsTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                getInt(R.styleable.ChipsTriggerView_cpv_max_rows, DEFAULT_MAX_ROWS).apply {
                    maxRows = this
                }
                getString(R.styleable.ChipsTriggerView_cpv_hidden_items_format_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_chips_n_more_items_format_text)
                    nMoreItemsFormat = if (isNullOrBlank()) default
                    else this
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun syncChips() {
        flowLayout.apply flowLayout@{
            removeAllViews()
            text.apply chipTitle@{
                if (isNullOrBlank()) {
                    hintTextView.visibility = VISIBLE
                    flowLayoutContainer.layoutParams.height = emptyLayoutHeight
                    return@chipTitle
                }
                flowLayoutContainer.layoutParams.height = WRAP_CONTENT
                split(",").forEach { chipTitle ->
                    hintTextView.visibility = GONE
                    addView(inflateChip(chipTitle.trim()))
                }
            }
        }
    }

    private fun generateViewsForFlowLayout(elements: Array<String>): List<View> {
        val result = mutableListOf<View>()
        var requiredWidth = 0
        val widthMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
        val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
        val chipSpacing = ResourceUtils.getDimenPxById(context, R.dimen.trigger_view_chips_items_spacing)
        val maxWidth = layoutParams.width
        var fitElements = 0
        elements.apply {
            for (i in 0 until size) {
                val chipTitle = this[i].trim()
                val chipView = inflateChip(chipTitle)
                chipView.measure(widthMeasureSpec, heightMeasureSpec)
                val widthToAdd = (chipView.measuredWidth + chipSpacing)
                val requiredRows = (requiredWidth + widthToAdd) / maxWidth
                val chipWillFit = requiredRows <= maxRows
                if (chipWillFit) {
                    result.add(chipView)
                    requiredWidth += (chipView.measuredWidth + chipSpacing)
                    fitElements++
                } else {
                    val hiddenItemsCount = size - fitElements
                    inflateHiddenItemsView(result, hiddenItemsCount, requiredWidth, maxWidth, chipSpacing)
                    break
                }
            }
        }
        return result
    }

    fun inflateHiddenItemsView(
        flowLayoutViews: MutableList<View>,
        hiddenItemsCount: Int,
        currentRequiredWidth: Int,
        maxWidth: Int,
        spacing: Int,
    ) {
        val nHiddenViewsLayout = TextView(context).apply {
            val padding = ResourceUtils.getDimenPxById(context, R.dimen.trigger_view_chips_items_spacing)
            val nMoreItemsText = nMoreItemsFormat.format(hiddenItemsCount)
            setPadding(padding)
            text = nMoreItemsText
            val widthMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            measure(widthMeasureSpec, heightMeasureSpec)
        }
        val requiredWidth = nHiddenViewsLayout.measuredWidth + spacing
        var willViewFit = ((currentRequiredWidth + requiredWidth) / maxWidth) <= maxRows
        var newRequiredWidth = currentRequiredWidth
        while (!willViewFit && flowLayoutViews.isNotEmpty()) {
            val viewToRemove = flowLayoutViews.removeLast()
            val widthToRemove = viewToRemove.measuredWidth + spacing
            newRequiredWidth -= widthToRemove
            willViewFit = ((newRequiredWidth + requiredWidth) / maxWidth) <= maxRows
        }
        flowLayoutViews.add(nHiddenViewsLayout)
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