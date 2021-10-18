package com.github.rooneyandshadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.commons.date.DateUtils;
import com.github.rooneyandshadows.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.pickers.R;
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


@SuppressWarnings("unused")
public class DialogMonthPickerView extends BaseDialogPickerView {
    private Date cachedDate;
    private int[] selection;
    private String monthPickerFormat;
    private int minYear;
    private int maxYear;
    private ArrayList<int[]> disabledMonths;
    private ArrayList<int[]> enabledMonths;
    private SelectionChangedListener dataBindingListener;
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final ArrayList<ValidationCheck> validationCallbacks = new ArrayList<>();

    public DialogMonthPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String getViewText() {
        if (!hasSelection())
            return "";
        return DateUtils.getDateString(monthPickerFormat, getSelectionAsDate());
    }

    public boolean validate() {
        boolean isValid = true;
        if (validationEnabled) {
            if (required && !hasSelection()) {
                setErrorEnabled(true);
                setErrorText(pickerRequiredText);
                return false;
            }
            for (ValidationCheck validationCallback : validationCallbacks)
                isValid &= validationCallback.validate(getSelectionAsArray());
        }
        if (!isValid)
            setErrorEnabled(true);
        else {
            setErrorEnabled(false);
            setErrorText(null);
        }
        return isValid;
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogMonthPickerView, 0, 0);
        try {
            monthPickerFormat = StringUtils.getOrDefault(a.getString(R.styleable.DialogMonthPickerView_MPV_DateFormat), "YYYY, MMM");
            minYear = a.getInteger(R.styleable.DialogMonthPickerView_MPV_MinYear, 1970);
            maxYear = a.getInteger(R.styleable.DialogMonthPickerView_MPV_MaxYear, 2100);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected MonthPickerDialog initializeDialog() {
        MonthPickerDialogBuilder builder = new MonthPickerDialogBuilder(manager, DIALOG_TAG)
                .withMinYear(minYear)
                .withMaxYear(maxYear)
                .withDisabledMonths(disabledMonths)
                .withEnabledMonths(enabledMonths)
                .withPositiveButton(new DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withNegativeButton(new DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withOnCancelListener(dialogFragment -> updateTextAndValidate())
                .withOnDateSelectedEvent((oldValue, newValue) -> selectInternally(newValue))
                .withAnimations(pickerDialogAnimationType);
        if (selection != null)
            builder.withSelection(selection[0], selection[1]);
        return builder.buildDialog();
    }

    @Override
    protected MonthPickerDialog getDialog() {
        return (MonthPickerDialog) pickerDialog;
    }

    public void addSelectionChangedListener(SelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void addValidationCheck(ValidationCheck validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public void setSelection(int[] newSelection) {
        newSelection = validateSelectionInput(newSelection);
        if (newSelection == null)
            cachedDate = null;
        if (getDialog() == null) selectInternally(newSelection);
        else getDialog().setSelection(newSelection);
    }

    public void setSelection(int year, int month) {
        int[] newSelection = new int[]{year, month};
        setSelection(newSelection);
    }

    public void setSelectionFromDate(Date newDate) {
        int[] newSelection = null;
        if (newDate != null)
            newSelection = new int[]{DateUtils.extractYearFromDate(newDate), DateUtils.extractMonthOfYearFromDate(newDate)};
        cachedDate = newDate;
        setSelection(newSelection);
    }


    public void setCalendarBounds(int min, int max) {
        if (min > max) {
            maxYear = min;
            minYear = max;
        } else {
            minYear = min;
            maxYear = max;
        }
        if (getDialog() != null)
            getDialog().setCalendarBounds(minYear, maxYear);

    }

    public void setDisabledMonths(ArrayList<int[]> disabled) {
        disabledMonths = disabled;
        if (disabledMonths != null)
            for (int[] disabledMonth : disabledMonths)
                if (Arrays.equals(disabledMonth, getSelectionAsArray()))
                    setSelection(null);
        if (getDialog() != null)
            getDialog().setDisabledMonths(disabledMonths);
    }

    public void setEnabledMonths(ArrayList<int[]> enabled) {
        enabledMonths = enabled;
        if (enabledMonths != null) {
            minYear = DateUtils.extractYearFromDate(DateUtils.now());
            maxYear = minYear;
            if (enabledMonths.size() > 0) {
                minYear = enabled.get(0)[0];
                maxYear = enabled.get(0)[0];
            }
            boolean clearCurrentSelection = true;
            for (int[] month : enabledMonths) {
                int currentYear = month[0];
                if (Arrays.equals(month, getSelectionAsArray()))
                    clearCurrentSelection = false;
                if (currentYear < minYear)
                    minYear = currentYear;
                if (currentYear > maxYear)
                    maxYear = currentYear;
            }
            if (clearCurrentSelection)
                setSelection(null);
        }
        if (getDialog() != null)
            getDialog().setEnabledMonths(enabledMonths);
    }

    public int[] getSelectionAsArray() {
        return selection;
    }

    public Date getSelectionAsDate() {
        if (hasSelection()) {
            if (cachedDate != null) { // Used to keep day of previously set date
                cachedDate = DateUtils.setYearToDate(cachedDate, selection[0]);
                cachedDate = DateUtils.setMonthToDate(cachedDate, selection[1]);
            }
            return cachedDate == null ? DateUtils.date(selection[0], selection[1]) : cachedDate;
        }
        return null;
    }

    public boolean hasSelection() {
        return selection != null;
    }

    @BindingAdapter("monthPickerSelection")
    public static void updatePickerSelectionBinding(DialogMonthPickerView view, Date selectedDate) {
        view.setSelectionFromDate(selectedDate);
    }

    @InverseBindingAdapter(attribute = "monthPickerSelection", event = "monthPickerSelectionChanged")
    public static Date getText(DialogMonthPickerView view) {
        return view.getSelectionAsDate();
    }

    @BindingAdapter("monthPickerSelectionChanged")
    public static void setListeners(DialogMonthPickerView view, final InverseBindingListener attrChange) {
        view.dataBindingListener = (view1, newValue, oldValue) -> {
            if (view1.compareValues(newValue, oldValue))
                return;
            attrChange.onChange();
        };
    }

    private boolean compareValues(int[] v1, int[] v2) {
        return Arrays.equals(v1, v2);
    }

    private void selectInternally(int[] newSelection) {
        int[] oldSelection = selection;
        newSelection = validateSelectionInput(newSelection);
        selection = newSelection;
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
    }

    private int[] validateSelectionInput(int[] selection) {
        if (selection == null)
            return null;
        return validateSelectionInput(selection[0], selection[1]);
    }

    private int[] validateSelectionInput(int year, int month) {
        if (year < minYear)
            year = minYear;
        if (year > maxYear)
            year = maxYear;
        if (month < 1)
            month = 1;
        if (month > 12)
            month = 12;
        return new int[]{year, month};
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
        myState.minYear = minYear;
        myState.maxYear = maxYear;
        if (enabledMonths != null) {
            ArrayList<String> enabledMonths = new ArrayList<>();
            for (int[] enabledMonth : this.enabledMonths)
                enabledMonths.add(DateUtils.getDateStringInDefaultFormat(DateUtils.date(enabledMonth[0], enabledMonth[1])));
            myState.enabledMonths = enabledMonths;
        }
        if (disabledMonths != null) {
            ArrayList<String> disabledMonths = new ArrayList<>();
            for (int[] disabledMonth : this.disabledMonths)
                disabledMonths.add(DateUtils.getDateStringInDefaultFormat(DateUtils.date(disabledMonth[0], disabledMonth[1])));
            myState.disabledMonths = disabledMonths;
        }
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = savedState.selection;
        cachedDate = DateUtils.getDateFromStringInDefaultFormat(savedState.cachedDate);
        if (savedState.disabledMonths == null && savedState.enabledMonths == null)
            setCalendarBounds(savedState.minYear, savedState.maxYear);
        if (savedState.disabledMonths != null) {
            ArrayList<int[]> previouslyDisabledMonths = new ArrayList<>();
            for (String disabledMonth : savedState.disabledMonths) {
                Date monthAsDate = DateUtils.getDateFromStringInDefaultFormat(disabledMonth);
                int year = DateUtils.extractYearFromDate(monthAsDate);
                int month = DateUtils.extractMonthOfYearFromDate(monthAsDate);
                previouslyDisabledMonths.add(new int[]{year, month});
            }
            setDisabledMonths(previouslyDisabledMonths);
        }
        if (savedState.enabledMonths != null) {
            ArrayList<int[]> previouslyEnabledMonths = new ArrayList<>();
            for (String enabledMonth : savedState.enabledMonths) {
                Date monthAsDate = DateUtils.getDateFromStringInDefaultFormat(enabledMonth);
                int year = DateUtils.extractYearFromDate(monthAsDate);
                int month = DateUtils.extractMonthOfYearFromDate(monthAsDate);
                previouslyEnabledMonths.add(new int[]{year, month});
            }
            setEnabledMonths(previouslyEnabledMonths);
        }
        super.onRestoreInstanceState(savedState.getSuperState());
    }


    private static class SavedState extends View.BaseSavedState {
        private ArrayList<String> enabledMonths;
        private ArrayList<String> disabledMonths;
        private int[] selection;
        private String cachedDate;
        private Integer minYear;
        private Integer maxYear;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            enabledMonths = in.createStringArrayList();
            disabledMonths = in.createStringArrayList();
            minYear = in.readInt();
            maxYear = in.readInt();
            selection = in.createIntArray();
            cachedDate = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeIntArray(selection);
            out.writeInt(minYear);
            out.writeInt(maxYear);
            out.writeString(cachedDate);
            out.writeStringList(enabledMonths);
            out.writeStringList(disabledMonths);
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
        void onSelectionChanged(DialogMonthPickerView view, int[] oldValue, int[] newValue);
    }

    public interface ValidationCheck {
        boolean validate(int[] currentSelection);
    }
}