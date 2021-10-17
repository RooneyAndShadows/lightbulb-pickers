package com.github.RooneyAndShadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.RooneyAndShadows.lightbulb.dialogs.base.BaseDialogFragment;
import com.github.RooneyAndShadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog;
import com.github.RooneyAndShadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder;
import com.github.RooneyAndShadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;
import com.github.RooneyAndShadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.RooneyAndShadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
import com.rands.lightbulb.pickers.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
public class DialogAdapterPickerView<ModelType extends EasyAdapterDataModel> extends BaseDialogPickerView {
    private final ArrayList<ValidationCheck<ModelType>> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private EasyRecyclerAdapter<ModelType> adapter;
    private ItemDecoration itemDecoration;
    private String dialogTitle;
    private String dialogMessage;
    private BaseDialogFragment.DialogTypes pickerDialogType;
    private int[] selection;

    public DialogAdapterPickerView(Context context) {
        this(context, null);
    }

    public DialogAdapterPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

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
        return selection != null;
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
                .withPositiveButton(new BaseDialogFragment.DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialog) -> updateTextAndValidate())
                .withNegativeButton(new BaseDialogFragment.DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialog) -> updateTextAndValidate())
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
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = savedState.selection;
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    public void addSelectionChangedListener(SelectionChangedListener changedCallback) {
        selectionChangedListeners.add(changedCallback);
    }

    public void addValidationCheck(ValidationCheck<ModelType> validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public void setAdapter(EasyRecyclerAdapter<ModelType> adapter) {
        this.adapter = adapter;
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
        selection = newSelection;
        if (selectInAdapter)
            adapter.selectPositions(newSelection);
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
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

    public interface SelectionChangedListener {
        void execute(int[] newPositions, int[] oldPositions);
    }

    public interface ValidationCheck<ModelType extends EasyAdapterDataModel> {
        boolean validate(List<ModelType> selectedItems);
    }

    private static class SavedState extends View.BaseSavedState {
        private int[] selection;


        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.createIntArray();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            if (selection != null)
                out.writeIntArray(selection);
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