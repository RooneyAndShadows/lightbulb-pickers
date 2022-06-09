package com.github.rooneyandshadows.lightbulb.pickers.inline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView;
import com.nex3z.flowlayout.FlowLayout;
import com.github.rooneyandshadows.lightbulb.pickers.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ChipsPickerView<ModelType extends EasyAdapterDataModel> extends LinearLayoutCompat {
    private final String CHIPS_GROUP_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_GroupTag);
    private final String CHIPS_FILTER_CONTAINER_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_FilterTag);
    private final String CHIPS_FILTER_INPUT_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_InputEditTextTag);
    private final String CHIPS_OPTIONS_RECYCLER_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_OptionsRecyclerTag);
    private final String CHIPS_CHIP_ITEM_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_ChipItemTextTag);
    private final String CHIPS_CHIP_ITEM_REMOVE_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_ChipItemRemoveTag);
    //private PopupWindow popupWindow;
    private LinearLayoutCompat rootLayout;
    private RecyclerView recyclerView;
    private TextInputView chipGroupInput;
    private FlowLayout chipGroup;
    private String pickerHintText;
    private String errorText;
    private String pickerRequiredText;
    private Drawable pickerIcon;
    private boolean required;
    private boolean allowToAddNewOptions;
    private boolean closeOnLostFocus;
    private int defaultIconColor;
    private int backgroundColor;
    private int backgroundCornerRadius;
    private int chipsGroupPadding;
    private int[] selection;
    private SelectableFilterOptionAdapter<ModelType> recyclerAdapter;
    private AdapterOptionCreator<ModelType> optionCreator;
    private OnShowListener internalOnShowListener;
    private final ArrayList<OnShowListener> onShowListeners = new ArrayList<>();
    private final ArrayList<ValidationCheck<ModelType>> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final ArrayList<OnHideListener> onHideListeners = new ArrayList<>();
    private final ArrayList<OnOptionCreatedListener<ModelType>> onOptionCreatedListeners = new ArrayList<>();
    private final TextInputView.TextChangedCallback textWatcher = (newValue, oldValue) -> {
        handleAddOptionVisibility();
        filterOptions(newValue);
    };

    public ChipsPickerView(Context context) {
        this(context, null);
    }

    public ChipsPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        readAttributes(context, attrs);
        initLayout();
    }

    public void addOnShowListener(OnShowListener onShowListener) {
        onShowListeners.add(onShowListener);
    }

    public void addOnHideListener(OnHideListener onHideListener) {
        onHideListeners.add(onHideListener);
    }

    public void addOnOptionCreatedListener(OnOptionCreatedListener<ModelType> onOptionCreatedListener) {
        onOptionCreatedListeners.add(onOptionCreatedListener);
    }

    public void addSelectionChangedListener(SelectionChangedListener changedCallback) {
        selectionChangedListeners.add(changedCallback);
    }

    public void addValidationCheck(ValidationCheck<ModelType> validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public void selectItems(int[] positions) {
        selectInternally(positions, true);
    }

    public void selectItemAt(int selection) {
        int[] newSelection = new int[]{selection};
        selectInternally(newSelection, true);
    }

    public boolean validate() {
        boolean isValid = true;
        if (required && !hasSelection()) {
            setErrorText(pickerRequiredText);
            return false;
        }
        for (ValidationCheck<ModelType> validationCallback : validationCallbacks)
            isValid &= validationCallback.validate(getSelectedItems());
        if (isValid)
            setErrorText(null);
        else {
            setErrorText(errorText);
        }
        return isValid;
    }

    public void enableOptionCreation(AdapterOptionCreator<ModelType> optionCreator) {
        this.optionCreator = optionCreator;
    }

    public void setOptions(List<ModelType> option) {
        if (recyclerAdapter == null)
            return;
        recyclerAdapter.setCollection(option);
    }

    public void addOption(ModelType option) {
        if (recyclerAdapter == null)
            return;
        recyclerAdapter.addItem(option);
    }

    public void setSelection(int[] selection) {
        selectInternally(selection, true);
    }

    public void setAllowToAddNewOptions(boolean allowToAddNewOptions) {
        this.allowToAddNewOptions = allowToAddNewOptions;
        setupAddButton();
    }

    public final void setRequired(boolean required) {
        this.required = required;
        if (required)
            validate();
    }

    public final void setPickerIcon(Drawable icon, Integer color) {
        pickerIcon = icon;
        if (color != null)
            pickerIcon.setTint(color);
        chipGroupInput.setStartIcon(pickerIcon);
    }

    public final void setPickerIcon(Drawable icon) {
        setPickerIcon(icon, defaultIconColor);
    }

    public final void setErrorText(String error) {
        errorText = error;
        chipGroupInput.setError(errorText);
    }

    public final void setHintText(String hintText) {
        pickerHintText = hintText;
        chipGroupInput.setHintText(pickerHintText);
    }

    public void setRecyclerAdapter(SelectableFilterOptionAdapter<ModelType> adapter) {
        this.recyclerAdapter = adapter;
        this.recyclerAdapter.addOnSelectionChangedListener(newSelection -> {
            selectInternally(newSelection, false);
            chipGroupInput.setText("");
            hidePicker();
        });
        recyclerView = findViewWithTag(CHIPS_OPTIONS_RECYCLER_TAG);
        recyclerView.setPadding(chipsGroupPadding, chipsGroupPadding, chipsGroupPadding, chipsGroupPadding);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);
        hidePicker();
    }

    public boolean hasSelection() {
        if (recyclerAdapter == null)
            return false;
        return recyclerAdapter.hasSelection();
    }

    public List<ModelType> getSelectedItems() {
        if (recyclerAdapter == null)
            return new ArrayList<>();
        return recyclerAdapter.getItems(selection);
    }

    public int[] getSelectedPositions() {
        if (recyclerAdapter == null)
            return new int[]{};
        return recyclerAdapter.getSelectedPositionsAsArray();
    }

    public List<ModelType> getOptions() {
        if (recyclerAdapter == null)
            return new ArrayList<>();
        return recyclerAdapter.getItems();
    }

    public SelectableFilterOptionAdapter<ModelType> getRecyclerAdapter() {
        return recyclerAdapter;
    }

    public final void attachToScrollingParent(ViewGroup parent) {
        if (parent instanceof ScrollView) {
            internalOnShowListener = () -> parent.post(() -> ((ScrollView) parent).smoothScrollTo(0, getBottom()));
            return;
        }
        if (parent instanceof NestedScrollView) {
            internalOnShowListener = () -> parent.post(() -> ((NestedScrollView) parent).smoothScrollTo(0, getBottom()));
        }
        Log.w(ChipsPickerView.class.getName(), "Scrolling parent type must be one of ScrollView|NestedScrollView");
    }

    public final String getPickerHintText() {
        return pickerHintText;
    }

    public final String getErrorText() {
        return errorText;
    }

    public void selectItem(ModelType item) {
        if (item == null || recyclerAdapter == null)
            return;
        int position = recyclerAdapter.getPosition(item);
        if (position != -1)
            selectItemAt(position);
    }

    /*private void showPopupWindow() {
        RecyclerView rc = popupWindow.getContentView().findViewWithTag("pickerRecycler");
        popupWindow.getContentView().setBackgroundColor(ResourceUtils.getColorByAttribute(getContext(), android.R.attr.colorBackground));
        popupWindow.setFocusable(false);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setOutsideTouchable(true);
        rc.setItemAnimator(null);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(recyclerAdapter);
        popupWindow.showAsDropDown(chipGroupInput, 0, 0);
    }*/

    public void showPicker() {
        if (isPickerShown())
            return;
        if (!chipGroupInput.hasFocus())
            chipGroupInput.requestFocus();
        recyclerView.setVisibility(VISIBLE);
        if (internalOnShowListener != null)
            internalOnShowListener.execute();
        for (OnShowListener onShowListener : onShowListeners) onShowListener.execute();
    }

    public void hidePicker() {
        if (!isPickerShown())
            return;
        if (chipGroupInput.hasFocus())
            chipGroupInput.clearFocus();
        recyclerView.setVisibility(GONE);
        for (OnHideListener onHideListener : onHideListeners) onHideListener.execute();
    }

    protected boolean isPickerShown() {
        return recyclerView != null && recyclerView.getVisibility() == VISIBLE;
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChipsPickerView, 0, 0);
        try {
            pickerHintText = a.getString(R.styleable.ChipsPickerView_CPV_HintText);
            pickerRequiredText = a.getString(R.styleable.ChipsPickerView_CPV_RequiredText);
            pickerHintText = StringUtils.getOrDefault(pickerHintText, "...");
            pickerRequiredText = StringUtils.getOrDefault(pickerRequiredText, "Field is required");
            backgroundColor = a.getColor(R.styleable.ChipsPickerView_CPV_BackgroundColor, ColorUtils.setAlphaComponent(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface), 30));
            backgroundCornerRadius = a.getDimensionPixelSize(R.styleable.ChipsPickerView_CPV_BackgroundCornerRadius, ResourceUtils.getDimenPxById(getContext(), com.google.android.material.R.dimen.mtrl_textinput_box_corner_radius_medium));
            backgroundCornerRadius = a.getDimensionPixelSize(R.styleable.ChipsPickerView_CPV_BackgroundCornerRadius, ResourceUtils.getDimenPxById(getContext(), com.google.android.material.R.dimen.mtrl_textinput_box_corner_radius_medium));
            chipsGroupPadding = a.getDimensionPixelSize(R.styleable.ChipsPickerView_CPV_ChipGroupPadding, ResourceUtils.getDimenPxById(getContext(), R.dimen.chips_picker_chip_group_padding));
            required = a.getBoolean(R.styleable.ChipsPickerView_CPV_Required, false);
            closeOnLostFocus = a.getBoolean(R.styleable.ChipsPickerView_CPV_CloseOnLostFocus, true);
            allowToAddNewOptions = a.getBoolean(R.styleable.ChipsPickerView_CPV_AllowToAddNewOptions, true);
            defaultIconColor = ColorUtils.setAlphaComponent(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface), 140);
        } finally {
            a.recycle();
        }
    }

    private void initLayout() {
        setOrientation(VERTICAL);
        renderLayout();
        setupViews();
    }

    private void renderLayout() {
        rootLayout = (LinearLayoutCompat) inflate(getContext(), R.layout.chips_picker_layout, this);
        chipGroup = findViewWithTag(CHIPS_GROUP_TAG);
        chipGroupInput = findViewWithTag(CHIPS_FILTER_INPUT_TAG);
    }

    private void setupViews() {
        setupBackground(backgroundColor);
        setHintText(pickerHintText);
        setErrorText(errorText);
        setupInput();
        setupChips();
        setupAddButton();
    }

    private void setupInput() {
        chipGroupInput.removeTextChangedCallback(textWatcher);
        chipGroupInput.addTextChangedCallback(textWatcher);
        chipGroupInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !isPickerShown()) showPicker();
            else if (closeOnLostFocus && !hasFocus && isPickerShown()) hidePicker();
        });
    }

    private void setupAddButton() {
        Drawable icon = ResourceUtils.getDrawable(getContext(), R.drawable.chip_picker_add_icon);
        icon.setTint(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface));
        chipGroupInput.setEndIcon(icon, v -> {
            if (!allowToAddNewOptions || recyclerAdapter == null || optionCreator == null)
                return;
            String newOptionName = chipGroupInput.getText();
            ModelType newOption = optionCreator.createOption(newOptionName);
            recyclerAdapter.addItem(newOption);
            for (OnOptionCreatedListener<ModelType> optionCreatedListener : onOptionCreatedListeners)
                optionCreatedListener.execute(newOption);
        });
        handleAddOptionVisibility();
    }

    private void setupBackground(int newColor) {
        this.backgroundColor = newColor;
        Drawable backgroundDrawable = DrawableUtils.getLayeredRoundedCornersDrawable(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorSurface), backgroundColor, backgroundCornerRadius);
        setBackground(backgroundDrawable);
    }

    private void handleAddOptionVisibility() {
        if (!allowToAddNewOptions || recyclerAdapter == null || optionCreator == null) {
            chipGroupInput.setEndIconVisible(false);
            return;
        }
        String text = chipGroupInput.getText();
        chipGroupInput.setEndIconVisible(!StringUtils.isNullOrEmptyString(text) && !recyclerAdapter.hasItemWithName(text));
    }

    private void filterOptions(String queryText) {
        if (recyclerAdapter != null)
            recyclerAdapter.getFilter().filter(queryText);
    }

    private void setupChips() {
        clearChips();
        buildChips();
    }

    private void clearChips() {
        int childCount = chipGroup.getChildCount();
        View child = chipGroup.getChildAt(0);
        while (child != null) {
            Object tag = child.getTag();
            if (tag != null && tag.equals(CHIPS_FILTER_CONTAINER_TAG))
                break;
            chipGroup.removeView(child);
            child = chipGroup.getChildAt(0);
        }
    }

    private void buildChips() {
        if (recyclerAdapter == null || recyclerAdapter.getSelectedItems().size() <= 0) {
            chipGroup.setPadding(0, 0, 0, 0);
            return;
        }
        chipGroup.setPadding(chipsGroupPadding, chipsGroupPadding, chipsGroupPadding, ResourceUtils.getDimenPxById(getContext(), R.dimen.chips_picker_spacing_size));
        List<ModelType> selectedItems = recyclerAdapter.getSelectedItems();
        for (int position = 0; position < selectedItems.size(); position++)
            buildChip(selectedItems.get(position));
    }

    private void buildChip(ModelType targetItem) {
        String itemName = targetItem.getItemName();
        LinearLayoutCompat chipLayout = (LinearLayoutCompat) LayoutInflater.from(getContext()).inflate(R.layout.chips_picker_chip, this, false);
        TextView chipTextView = chipLayout.findViewWithTag(CHIPS_CHIP_ITEM_TEXT_TAG);
        AppCompatImageButton chipRemoveButton = chipLayout.findViewWithTag(CHIPS_CHIP_ITEM_REMOVE_TAG);
        Drawable chipBackground = DrawableUtils.getRoundedCornersDrawable(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface), ResourceUtils.getDimenPxById(getContext(), R.dimen.chips_picker_chip_height));
        Drawable chipRemoveIcon = ResourceUtils.getDrawable(getContext(), R.drawable.chip_picker_remove_icon);
        chipRemoveIcon.setTint(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorPrimary));
        chipLayout.setBackground(ResourceUtils.getDrawable(getContext(), R.drawable.bg_chip_picker_item));
        chipRemoveButton.setImageDrawable(chipRemoveIcon);
        chipRemoveButton.setOnClickListener(v -> recyclerAdapter.selectItem(targetItem, false));
        chipRemoveButton.setBackgroundDrawable(ResourceUtils.getDrawable(getContext(), R.drawable.bg_chip_picker_remove_icon));
        chipTextView.setText(itemName);
        chipTextView.setTextColor(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface));
        chipTextView.setCompoundDrawablePadding(ResourceUtils.getDimenPxById(getContext(), R.dimen.chips_picker_spacing_size));
        int childrenCount = chipGroup.getChildCount();
        int index;
        if (childrenCount > 1)
            index = childrenCount - 1;
        else
            index = 0;
        chipGroup.addView(chipLayout, index);
    }

    private void selectInternally(int[] newSelection, boolean selectInAdapter) {
        int[] oldSelection = selection;
        selection = newSelection;
        if (recyclerAdapter != null && selectInAdapter)
            recyclerAdapter.selectPositions(newSelection);
        setupChips();
        validate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
    }

    private boolean compareValues(int[] v1, int[] v2) {
        return Arrays.equals(v1, v2);
    }

    private void dispatchSelectionChangedEvents(int[] oldValue, int[] newValue) {
        if (compareValues(newValue, oldValue))
            return;
        for (SelectionChangedListener selectionChangedListener : selectionChangedListeners)
            selectionChangedListener.execute(oldValue, newValue);
    }

    /*private void buildPopup() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        // popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);


        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //popupWindow.dismiss();
                return true;
            }
        });
    }*/

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.pickerIsShowing = isPickerShown();
        myState.selection = selection;
        myState.pickerHintText = pickerHintText;
        myState.pickerErrorText = errorText;
        myState.pickerRequiredText = pickerRequiredText;
        myState.pickerIsRequired = required;
        myState.pickerCloseOnLostFocus = closeOnLostFocus;
        myState.pickerBackgroundColor = backgroundColor;
        myState.pickerDefaultIconColor = defaultIconColor;
        myState.pickerChipGroupPadding = chipsGroupPadding;
        myState.pickerBackgroundCornerRadius = backgroundCornerRadius;
        myState.pickerAllowToAddNewOptions = allowToAddNewOptions;
        myState.pickerInputState = chipGroupInput.onSaveInstanceState();
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        boolean isPickerShowing = savedState.pickerIsShowing;
        selection = savedState.selection;
        pickerHintText = savedState.pickerHintText;
        errorText = savedState.pickerErrorText;
        pickerRequiredText = savedState.pickerRequiredText;
        required = savedState.pickerIsRequired;
        closeOnLostFocus = savedState.pickerCloseOnLostFocus;
        backgroundColor = savedState.pickerBackgroundColor;
        chipsGroupPadding = savedState.pickerChipGroupPadding;
        backgroundCornerRadius = savedState.pickerBackgroundCornerRadius;
        defaultIconColor = savedState.pickerDefaultIconColor;
        allowToAddNewOptions = savedState.pickerAllowToAddNewOptions;
        chipGroupInput.onRestoreInstanceState(savedState.pickerInputState);
        setupViews();
        if (isPickerShowing)
            showPicker();
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    private static class SavedState extends BaseSavedState {
        private int[] selection;
        private String pickerHintText;
        private String pickerErrorText;
        private String pickerRequiredText;
        private boolean pickerIsShowing;
        private boolean pickerIsRequired;
        private boolean pickerIsErrorEnabled;
        private boolean pickerShowSelectedTextValue;
        private boolean pickerCloseOnLostFocus;
        private boolean pickerAllowToAddNewOptions;
        private int pickerBackgroundColor;
        private int pickerDefaultIconColor;
        private int pickerChipGroupPadding;
        private int pickerBackgroundCornerRadius;
        private Parcelable pickerInputState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.createIntArray();
            pickerHintText = in.readString();
            pickerErrorText = in.readString();
            pickerRequiredText = in.readString();
            pickerIsShowing = in.readByte() != 0;
            pickerIsRequired = in.readByte() != 0;
            pickerIsErrorEnabled = in.readByte() != 0;
            pickerShowSelectedTextValue = in.readByte() != 0;
            pickerCloseOnLostFocus = in.readByte() != 0;
            pickerAllowToAddNewOptions = in.readByte() != 0;
            pickerBackgroundColor = in.readInt();
            pickerDefaultIconColor = in.readInt();
            pickerChipGroupPadding = in.readInt();
            pickerBackgroundCornerRadius = in.readInt();
            pickerInputState = in.readParcelable(getClass().getClassLoader());

        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            if (selection != null)
                out.writeIntArray(selection);
            out.writeString(pickerHintText);
            out.writeString(pickerErrorText);
            out.writeString(pickerRequiredText);
            out.writeByte((byte) (pickerIsShowing ? 1 : 0));
            out.writeByte((byte) (pickerIsRequired ? 1 : 0));
            out.writeByte((byte) (pickerIsErrorEnabled ? 1 : 0));
            out.writeByte((byte) (pickerShowSelectedTextValue ? 1 : 0));
            out.writeByte((byte) (pickerCloseOnLostFocus ? 1 : 0));
            out.writeByte((byte) (pickerAllowToAddNewOptions ? 1 : 0));
            out.writeInt(pickerBackgroundColor);
            out.writeInt(pickerDefaultIconColor);
            out.writeInt(pickerChipGroupPadding);
            out.writeInt(pickerBackgroundCornerRadius);
            out.writeParcelable(pickerInputState, flags);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface SelectionChangedListener {
        void execute(int[] oldSelection, int[] newSelection);
    }

    public interface ValidationCheck<ModelType extends EasyAdapterDataModel> {
        boolean validate(List<ModelType> selectedItems);
    }

    public interface AdapterOptionCreator<ModelType> {
        ModelType createOption(String newOptionName);
    }

    public interface OnShowListener {
        void execute();
    }

    public interface OnHideListener {
        void execute();
    }

    public interface OnOptionCreatedListener<ModelType> {
        void execute(ModelType option);
    }
}