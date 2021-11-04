package com.github.rooneyandshadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.dialogs.base.LightBulbDialogFragment;
import com.github.rooneyandshadows.lightbulb.dialogs.base.LightBulbDialogFragment.DialogButtonConfiguration;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.ColorModel;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialog;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.LightBulbDialogPickerView;
import com.github.rooneyandshadows.lightbulb.pickers.R;
import com.github.rooneyandshadows.lightbulb.recycleradapters.LightBulbAdapter;
import com.github.rooneyandshadows.lightbulb.recycleradapters.LightBulbAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.LightBulbAdapterSelectableModes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DialogColorPickerView extends LightBulbDialogPickerView {
    private final ArrayList<ValidationCheck<ColorModel>> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final ColorPickerAdapter adapter;
    private String dialogTitle;
    private String dialogMessage;
    private LightBulbDialogFragment.DialogTypes pickerDialogType;
    private int[] selection;

    public DialogColorPickerView(Context context) {
        this(context, null);
    }

    public DialogColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        adapter = new ColorPickerAdapter(getContext(), LightBulbAdapterSelectableModes.SELECT_SINGLE);
        addSelectionChangedListener((oldPositions, newPositions) -> updatePickerIcon(newPositions));
        addOnTriggerAttachedCallback((triggerView1, pickerView) -> updatePickerIcon(null));
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogColorPickerView, 0, 0);
        try {
            dialogTitle = a.getString(R.styleable.DialogColorPickerView_CPV_DialogTitle);
            dialogMessage = a.getString(R.styleable.DialogColorPickerView_CPV_DialogMessage);
            if (dialogTitle == null || dialogTitle.equals(""))
                dialogTitle = "";
            if (dialogMessage == null || dialogMessage.equals(""))
                dialogMessage = "";
            showSelectedTextValue = false;
        } finally {
            a.recycle();
        }
    }

    @Override
    protected String getViewText() {
        String text = "";
        if (adapter != null && selection != null)
            text = adapter.getPositionStrings(selection);
        return text;
    }

    public boolean hasSelection() {
        return selection != null && selection.length > 0;
    }

    @Override
    public boolean validate() {
        boolean isValid = true;
        if (validationEnabled) {
            if (required && !hasSelection()) {
                setErrorEnabled(true);
                setErrorText(pickerRequiredText);
                return false;
            }
            for (ValidationCheck<ColorModel> validationCallback : validationCallbacks)
                isValid &= validationCallback.validate(getSelectedItems());
        }
        if (!isValid) {
            setErrorEnabled(true);
        } else {
            setErrorEnabled(false);
            setErrorText(null);
        }
        return isValid;
    }

    @Override
    protected ColorPickerDialog initializeDialog() {
        ColorPickerDialogBuilder dialogBuilder = new ColorPickerDialogBuilder(manager, DIALOG_TAG, adapter);
        return dialogBuilder
                .withSelection(selection)
                .withDialogType(pickerDialogType)
                .withAnimations(pickerDialogAnimationType)
                .withCancelOnClickOutsude(pickerDialogCancelable)
                .withMessage(dialogMessage)
                .withTitle(dialogTitle)
                .withPositiveButton(new DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialog) -> updateTextAndValidate())
                .withNegativeButton(new DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialog) -> updateTextAndValidate())
                .withOnCancelListener(dialogFragment -> updateTextAndValidate())
                .withSelectionCallback((oldValue, newValue) -> selectInternally(newValue, false))
                .buildDialog();
    }

    @Override
    protected ColorPickerDialog getDialog() {
        return (ColorPickerDialog) pickerDialog;
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.selection = selection;
        myState.adapterState = getAdapter().saveAdapterState();
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = savedState.selection;
        getAdapter().restoreAdapterState(savedState.adapterState);
        updatePickerIcon(selection);
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    @InverseBindingAdapter(attribute = "colorPickerSelection", event = "colorPickerSelectionChanged")
    public static String getSelectedValue(DialogColorPickerView view) {
        if (view.hasSelection()) {
            return view.getSelectedItems().get(0).getColorExternalName();
        } else
            return null;
    }

    @BindingAdapter(value = {"colorPickerSelection"})
    public static void setPickerSelection(DialogColorPickerView view, String newExternalName) {
        if (StringUtils.isNullOrEmptyString(newExternalName))
            return;
        if (view.hasSelection()) {
            ColorModel currentSelection = view.getSelectedItems().get(0);
            if (currentSelection.getColorExternalName().equals(newExternalName))
                return;
        }
        for (ColorModel selectableTransactionTypeModel : view.getData())
            if (newExternalName.equals(selectableTransactionTypeModel.getColorExternalName())) {
                view.selectItem(selectableTransactionTypeModel);
                break;
            }
    }

    @BindingAdapter(value = {"colorPickerSelectionChanged"}, requireAll = false)
    public static void bindPickerEvent(DialogColorPickerView view, final InverseBindingListener bindingListener) {
        if (view.hasSelection()) bindingListener.onChange();
        view.addSelectionChangedListener((newPositions, oldPositions) -> bindingListener.onChange());
    }

    public void addSelectionChangedListener(SelectionChangedListener changedCallback) {
        selectionChangedListeners.add(changedCallback);
    }

    public void addValidationCheck(ValidationCheck<ColorModel> validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    private boolean compareValues(int[] v1, int[] v2) {
        return Arrays.equals(v1, v2);
    }

    private void dispatchSelectionChangedEvents(int[] oldValue, int[] newValue) {
        if (compareValues(oldValue, newValue))
            return;
        for (SelectionChangedListener selectionChangedListener : selectionChangedListeners) {
            selectionChangedListener.execute(oldValue, newValue);
        }
    }

    public LightBulbAdapter<ColorModel> getAdapter() {
        return adapter;
    }

    public List<ColorModel> getSelectedItems() {
        if (adapter == null)
            return new ArrayList<>();
        return adapter.getItems(selection);
    }

    public void selectItemAt(int selection) {
        int[] newSelection = new int[]{selection};
        if (getDialog() == null) selectInternally(newSelection, true);
        else getDialog().setSelection(newSelection);
    }

    public void setSelection(int[] selection) {
        if (getDialog() == null) selectInternally(selection, true);
        else getDialog().setSelection(selection);
    }

    public void selectItem(ColorModel item) {
        if (item == null)
            return;
        if (adapter != null) {
            int position = adapter.getPosition(item);
            if (position != -1) {
                selectItemAt(position);
            }
        }
    }

    public List<ColorModel> getData() {
        if (adapter == null)
            return null;
        return new ArrayList<>(adapter.getItems());
    }

    public void setData(List<ColorModel> data) {
        if (adapter == null)
            return;
        adapter.setCollection(data);
    }

    public void refresh() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        updateTextAndValidate();
    }

    private void selectInternally(int[] newSelection, boolean selectInAdapter) {
        int[] oldSelection = selection;
        selection = newSelection;
        if (selectInAdapter)
            adapter.selectPositions(newSelection);
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
    }

    private void updatePickerIcon(int[] selection) {
        if (!hasSelection()) {
            Drawable defaultDrawable = ResourceUtils.getDrawable(getContext(), R.drawable.color_picker_default_icon);
            setPickerIcon(defaultDrawable);
        } else {
            ColorModel selectedModel = adapter.getItem(selection[0]);
            Drawable drawable = adapter.getColorDrawable(getSelectedItems().get(0));
            int color = Color.parseColor(selectedModel.getColorHex());
            setPickerIcon(drawable, color);
        }
    }

    public interface SelectionChangedListener {
        void execute(int[] oldPositions, int[] newPositions);
    }

    public interface ValidationCheck<ModelType extends LightBulbAdapterDataModel> {
        boolean validate(List<ModelType> selectedItems);
    }

    private static class SavedState extends View.BaseSavedState {
        private Bundle adapterState;
        private int[] selection;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.createIntArray();
            adapterState = in.readBundle(DialogColorPickerView.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            if (selection != null)
                out.writeIntArray(selection);
            out.writeBundle(adapterState);
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
}