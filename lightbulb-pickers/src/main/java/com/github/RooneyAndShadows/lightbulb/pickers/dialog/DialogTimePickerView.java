package com.github.rooneyandshadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.java.commons.date.DateUtils;
import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialog;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import static com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.*;


@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class DialogTimePickerView extends BaseDialogPickerView {
    private Date cachedDate;
    private int[] selection;
    private String datePickerFormat;
    private SelectionChangedListener dataBindingListener;
    private final ArrayList<ValidationCheck> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();

    public DialogTimePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogTimePickerView(Context context) {
        this(context, null);
    }

    @Override
    protected String getViewText() {
        if (selection == null)
            return "";
        return DateUtils.getDateString(datePickerFormat, getSelectionAsDate());
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        if (StringUtils.isNullOrEmptyString(datePickerFormat))
            datePickerFormat = "HH:mm";
        if (!hasSelection()) {
            Date now = DateUtils.now();
            selection = new int[]{DateUtils.getHourOfDay(now), DateUtils.getMinuteOfHour(now)};
        }
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
            for (ValidationCheck validationCallback : validationCallbacks)
                isValid &= validationCallback.execute(selection);
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
    protected TimePickerDialog initializeDialog() {
        TimePickerDialogBuilder builder = new TimePickerDialogBuilder(manager, DIALOG_TAG)
                .withInitialTime(selection)
                .withCancelOnClickOutsude(true)
                .withPositiveButton(new DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withNegativeButton(new DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withOnCancelListener(dialogFragment -> updateTextAndValidate())
                .withOnDateSelectedEvent((previousSelection, newSelection) -> selectInternally(newSelection))
                .withAnimations(pickerDialogAnimationType);
        return builder.buildDialog();
    }

    @Override
    protected TimePickerDialog getDialog() {
        return (TimePickerDialog) pickerDialog;
    }

    public void addSelectionChangedListener(SelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void addValidationCheck(ValidationCheck validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public boolean hasSelection() {
        return selection != null;
    }

    @BindingAdapter("timePickerSelection")
    public static void updatePickerSelectionBinding(DialogTimePickerView view, Date selectedDate) {
        view.setSelectionFromDate(selectedDate);
    }

    @InverseBindingAdapter(attribute = "timePickerSelection", event = "timePickerSelectionChanged")
    public static Date getText(DialogTimePickerView view) {
        return view.getSelectionAsDate();
    }

    @BindingAdapter("timePickerSelectionChanged")
    public static void setListeners(DialogTimePickerView view, final InverseBindingListener attrChange) {
        view.dataBindingListener = (view1, newValue, oldValue) -> {
            if (view1.compareValues(newValue, oldValue))
                return;
            attrChange.onChange();
        };
    }

    public Date getSelectionAsDate() {
        if (hasSelection()) {

            // Used to keep day of previously set date
            if (cachedDate != null)
                cachedDate = DateUtils.setTimeToDate(cachedDate, selection[0], selection[1], DateUtils.getSecondOfMinute(cachedDate));
            Date now = DateUtils.now();
            return cachedDate == null ? DateUtils.setTimeToDate(now, selection[0], selection[1], DateUtils.getSecondOfMinute(now)) : cachedDate;
        }
        return null;
    }

    public int[] getSelectedTime() {
        return selection;
    }

    private boolean compareValues(int[] v1, int[] v2) {
        return Arrays.equals(v1, v2);
    }


    private void selectInternally(int[] newSelection) {
        int[] oldSelection = selection;
        newSelection = validateTimeInput(newSelection);
        selection = newSelection;
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
    }

    private void selectInternally(int hour, int minutes) {
        selectInternally(new int[]{hour, minutes});
    }

    public void setSelection(int hour, int minutes) {
        setSelection(new int[]{hour, minutes});
    }

    public void setSelection(int[] time) {
        time = validateTimeInput(time);
        if (getDialog() == null) selectInternally(time);
        else getDialog().setSelection(time);
    }

    public void setSelectionFromDate(Date newDate) {
        int[] newSelection = null;
        if (newDate != null)
            newSelection = new int[]{DateUtils.getHourOfDay(newDate), DateUtils.getMinuteOfHour(newDate)};
        cachedDate = newDate;
        setSelection(newSelection);
    }

    private int[] validateTimeInput(int[] time) {
        if (time == null)
            return null;
        int hour = time[0];
        int minutes = time[1];
        if (minutes >= 60) {
            hour++;
            minutes = 0;
        }
        if (minutes < 0)
            minutes = 0;
        if (hour >= 24) {
            hour = 23;
            minutes = 59;
        }
        if (hour < 0) {
            hour = 0;
        }
        return new int[]{hour, minutes};
    }

    private void updateUI() {
        updateTextAndValidate();
    }

    private void dispatchSelectionChangedEvents(int[] oldValue, int[] newValue) {
        if (compareValues(oldValue, newValue))
            return;
        for (SelectionChangedListener listener : selectionChangedListeners)
            listener.onSelectionChanged(this, oldValue, newValue);
        if (dataBindingListener != null)
            dataBindingListener.onSelectionChanged(this, oldValue, newValue);
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
        myState.cachedDate = DateUtils.getDateStringInDefaultFormat(cachedDate);
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = savedState.selection;
        cachedDate = DateUtils.getDateFromStringInDefaultFormat(savedState.cachedDate);
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    private static class SavedState extends View.BaseSavedState {
        private String cachedDate;
        private int[] selection;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.createIntArray();
            cachedDate = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeIntArray(selection);
            out.writeString(cachedDate);
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
        void onSelectionChanged(DialogTimePickerView view, int[] oldValue, int[] newValue);
    }

    public interface ValidationCheck {
        boolean execute(int[] currentSelection);
    }
}