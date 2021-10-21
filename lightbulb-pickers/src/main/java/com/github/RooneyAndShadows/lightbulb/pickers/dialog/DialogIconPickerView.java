package com.github.rooneyandshadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialog;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterSelectableModes;
import com.github.rooneyandshadows.lightbulb.pickers.R;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import static com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.*;
import static com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.*;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DialogIconPickerView extends BaseDialogPickerView {
    private final ArrayList<ValidationCheck<IconModel>> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final IconPickerAdapter adapter;
    private String dialogTitle;
    private String dialogMessage;
    private DialogTypes pickerDialogType;
    private int selectedIconSize;
    private int[] selection;

    public DialogIconPickerView(Context context) {
        this(context, null);
    }

    public DialogIconPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        adapter = new IconPickerAdapter(getContext(), EasyAdapterSelectableModes.SELECT_SINGLE);
        addSelectionChangedListener((oldPositions, newPositions) -> {
            if (newPositions != null && newPositions.length > 0)
                setPickerIcon(adapter.getDrawable(adapter.getSelectedItems().get(0), selectedIconSize));
            else setPickerIcon(null);
        });
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogIconPickerView, 0, 0);
        try {
            dialogTitle = a.getString(R.styleable.DialogIconPickerView_IPV_DialogTitle);
            dialogMessage = a.getString(R.styleable.DialogIconPickerView_IPV_DialogMessage);
            selectedIconSize = a.getDimensionPixelSize(R.styleable.DialogIconPickerView_IPV_SelectedIconSize, ResourceUtils.getDimenPxById(context, R.dimen.icon_picker_selected_size));
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
            for (ValidationCheck<IconModel> validationCallback : validationCallbacks)
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
    protected IconPickerDialog initializeDialog() {
        IconPickerDialogBuilder dialogBuilder = new IconPickerDialogBuilder(manager, DIALOG_TAG, adapter);
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
    protected IconPickerDialog getDialog() {
        return (IconPickerDialog) pickerDialog;
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
        myState.selectedIconSize = selectedIconSize;
        myState.adapterState = getAdapter().saveAdapterState();
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = savedState.selection;
        selectedIconSize = savedState.selectedIconSize;
        getAdapter().restoreAdapterState(savedState.adapterState);
        if (hasSelection())
            setPickerIcon(adapter.getDrawable(getSelectedItems().get(0), selectedIconSize));
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    @InverseBindingAdapter(attribute = "iconPickerSelection", event = "iconPickerSelectionChanged")
    public static String getSelectedValue(DialogIconPickerView view) {
        if (view.hasSelection()) {
            return view.getSelectedItems().get(0).getIconExternalName();
        } else
            return null;
    }

    @BindingAdapter(value = {"iconPickerSelection"})
    public static void setPickerSelection(DialogIconPickerView view, String newExternalName) {
        if (StringUtils.isNullOrEmptyString(newExternalName))
            return;
        if (view.hasSelection()) {
            IconModel currentSelection = view.getSelectedItems().get(0);
            if (currentSelection.getIconExternalName().equals(newExternalName))
                return;
        }
        for (IconModel selectableTransactionTypeModel : view.getData())
            if (newExternalName.equals(selectableTransactionTypeModel.getIconExternalName())) {
                view.selectItem(selectableTransactionTypeModel);
                break;
            }
    }

    @BindingAdapter(value = {"iconPickerSelectionChanged"}, requireAll = false)
    public static void bindPickerEvent(DialogIconPickerView view, final InverseBindingListener bindingListener) {
        if (view.hasSelection()) bindingListener.onChange();
        view.addSelectionChangedListener((oldPositions, newPositions) -> bindingListener.onChange());
    }

    public void addSelectionChangedListener(SelectionChangedListener changedCallback) {
        selectionChangedListeners.add(changedCallback);
    }

    public void addValidationCheck(ValidationCheck<IconModel> validationCallback) {
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

    public void refresh() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        updateTextAndValidate();
    }

    private void selectInternally(int[] newSelection, boolean selectInAdapter) {
        int[] oldSelection = selection;
        selection = newSelection;
        if (selectInAdapter)
            adapter.selectPositions(selection);
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
    }

    public void selectItemAt(int selection) {
        int[] newSelection = new int[]{selection};
        if (getDialog() == null) selectInternally(newSelection, true);
        else getDialog().setSelection(newSelection);
    }

    public void selectItem(IconModel item) {
        if (item == null)
            return;
        if (adapter != null) {
            int position = adapter.getPosition(item);
            if (position != -1) {
                selectItemAt(position);
            }
        }
    }

    public void setSelection(int[] selection) {
        if (getDialog() == null) selectInternally(selection, true);
        else getDialog().setSelection(selection);
    }

    public void setData(List<IconModel> data) {
        if (adapter == null)
            return;
        adapter.setCollection(data);
    }

    public void setSelectedIconSize(int selectedIconSize) {
        this.selectedIconSize = selectedIconSize;
        if (adapter.hasSelection())
            setPickerIcon(adapter.getDrawable(adapter.getSelectedItems().get(0), selectedIconSize));
    }

    public List<IconModel> getSelectedItems() {
        if (adapter == null)
            return new ArrayList<>();
        return adapter.getItems(selection);
    }

    public List<IconModel> getData() {
        if (adapter == null)
            return null;
        return new ArrayList<>(adapter.getItems());
    }

    public IconPickerAdapter getAdapter() {
        return adapter;
    }

    public int getSelectedIconSize() {
        return selectedIconSize;
    }

    public interface SelectionChangedListener {
        void execute(int[] oldPositions, int[] newPositions);
    }

    public interface ValidationCheck<ModelType extends EasyAdapterDataModel> {
        boolean validate(List<ModelType> selectedItems);
    }

    private static class SavedState extends View.BaseSavedState {
        private Bundle adapterState;
        private int selectedIconSize;
        private int[] selection;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.createIntArray();
            selectedIconSize = in.readInt();
            adapterState = in.readBundle(DialogIconPickerView.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            if (selection != null)
                out.writeIntArray(selection);
            out.writeInt(selectedIconSize);
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