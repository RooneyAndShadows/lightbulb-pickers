package com.github.rooneyandshadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment;
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.DialogButtonConfiguration;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter;
import com.github.rooneyandshadows.lightbulb.pickers.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
public abstract class DialogAdapterPickerView<ModelType extends EasyAdapterDataModel> extends BaseDialogPickerView {
    private final ArrayList<ValidationCheck<ModelType>> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final EasyRecyclerAdapter<ModelType> adapter;
    private ItemDecoration itemDecoration;
    private String dialogTitle;
    private String dialogMessage;
    private BaseDialogFragment.DialogTypes pickerDialogType;
    private int[] selection;

    public DialogAdapterPickerView(Context context) {
        this(context, null);
    }

    public DialogAdapterPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogAdapterPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DialogAdapterPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        adapter = initializeAdapter();
    }

    protected abstract EasyRecyclerAdapter<ModelType> initializeAdapter();

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogAdapterPickerView, 0, 0);
        try {
            dialogTitle = a.getString(R.styleable.DialogAdapterPickerView_APV_DialogTitle);
            dialogMessage = a.getString(R.styleable.DialogAdapterPickerView_APV_DialogMessage);
            pickerDialogType = BaseDialogFragment.DialogTypes.valueOf(a.getInt(R.styleable.DialogAdapterPickerView_APV_DialogMode, 1));
            if (dialogTitle == null || dialogTitle.equals(""))
                dialogTitle = "";
            if (dialogMessage == null || dialogMessage.equals(""))
                dialogMessage = "";
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
            for (ValidationCheck<ModelType> validationCallback : validationCallbacks)
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
    protected AdapterPickerDialog<ModelType> initializeDialog() {
        AdapterPickerDialogBuilder<ModelType> dialogBuilder = new AdapterPickerDialogBuilder<>(manager, DIALOG_TAG, adapter);
        return dialogBuilder
                .withSelection(selection)
                .withDialogType(pickerDialogType)
                .withAnimations(pickerDialogAnimationType)
                .withCancelOnClickOutsude(pickerDialogCancelable)
                .withMessage(dialogMessage)
                .withTitle(dialogTitle)
                .withItemDecoration(itemDecoration)
                .withPositiveButton(new DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialog) -> updateTextAndValidate())
                .withNegativeButton(new DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialog) -> updateTextAndValidate())
                .withOnCancelListener(dialogFragment -> updateTextAndValidate())
                .withSelectionCallback((oldValue, newValue) -> selectInternally(newValue, false))
                .buildDialog();
    }

    @Override
    protected AdapterPickerDialog<ModelType> getDialog() {
        return (AdapterPickerDialog<ModelType>) pickerDialog;
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
        myState.adapterState = adapter.saveAdapterState();
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = savedState.selection;
        adapter.restoreAdapterState(savedState.adapterState);
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    public void addSelectionChangedListener(SelectionChangedListener changedCallback) {
        selectionChangedListeners.add(changedCallback);
    }

    public void addValidationCheck(ValidationCheck<ModelType> validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public void setItemDecoration(ItemDecoration itemDecoration) {
        this.itemDecoration = itemDecoration;
    }

    public EasyRecyclerAdapter<ModelType> getAdapter() {
        return adapter;
    }

    public List<ModelType> getSelectedItems() {
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

    public void selectItem(ModelType item) {
        if (item == null)
            return;
        if (adapter != null) {
            int position = adapter.getPosition(item);
            if (position != -1)
                selectItemAt(position);
        }
    }

    public List<ModelType> getData() {
        if (adapter == null)
            return null;
        return new ArrayList<>(adapter.getItems());
    }

    public void setData(List<ModelType> data) {
        if (adapter == null)
            return;
        adapter.setCollection(data);
    }

    public void refresh() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        updateTextAndValidate();
    }

    private boolean compareValues(int[] v1, int[] v2) {
        return Arrays.equals(v1, v2);
    }

    private void dispatchSelectionChangedEvents(int[] oldValue, int[] newValue) {
        if (compareValues(oldValue, newValue))
            return;
        for (SelectionChangedListener selectionChangedListener : selectionChangedListeners)
            selectionChangedListener.execute(oldValue, newValue);
    }

    private void selectInternally(int[] newSelection, boolean selectInAdapter) {
        int[] oldSelection = selection;
        ensureAndApplySelection(newSelection);
        if (selectInAdapter)
            adapter.selectPositions(newSelection, true, false);
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, selection);
    }

    private void ensureAndApplySelection(int[] newSelection) {
        List<Integer> positionsToSelect = new ArrayList<>();
        for (int positionToSelect : newSelection) {
            if (!adapter.positionExists(positionToSelect))
                continue;
            positionsToSelect.add(positionToSelect);
        }
        selection = new int[positionsToSelect.size()];
        if (positionsToSelect.size() <= 0)
            return;
        for (int i = 0; i < positionsToSelect.size(); i++) {
            selection[i] = positionsToSelect.get(i);
        }
    }

    public interface SelectionChangedListener {
        void execute(int[] newPositions, int[] oldPositions);
    }

    public interface ValidationCheck<ModelType extends EasyAdapterDataModel> {
        boolean validate(List<ModelType> selectedItems);
    }

    private static class SavedState extends View.BaseSavedState {
        private int[] selection;
        private Bundle adapterState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.createIntArray();
            adapterState = in.readBundle(DialogAdapterPickerView.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
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