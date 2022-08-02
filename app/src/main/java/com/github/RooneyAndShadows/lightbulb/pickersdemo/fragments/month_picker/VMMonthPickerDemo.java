package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.month_picker;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.IconModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class VMMonthPickerDemo extends BaseObservableViewModel {
    private OffsetDateTime boxedSelection;
    private OffsetDateTime outlinedSelection;
    private OffsetDateTime buttonSelection;
    private OffsetDateTime imageButtonSelection;

    public void initialize() {
        boxedSelection = DateUtilsOffsetDate.nowLocal();
        outlinedSelection = DateUtilsOffsetDate.nowLocal();
        buttonSelection = DateUtilsOffsetDate.nowLocal();
        imageButtonSelection = DateUtilsOffsetDate.nowLocal();
    }

    public void setBoxedSelection(OffsetDateTime boxedSelection) {
        this.boxedSelection = boxedSelection;
        notifyPropertyChanged(BR.boxedSelection);
    }

    public void setOutlinedSelection(OffsetDateTime outlinedSelection) {
        this.outlinedSelection = outlinedSelection;
        notifyPropertyChanged(BR.outlinedSelection);
    }

    public void setButtonSelection(OffsetDateTime buttonSelection) {
        this.buttonSelection = buttonSelection;
        notifyPropertyChanged(BR.buttonSelection);
    }

    public void setImageButtonSelection(OffsetDateTime imageButtonSelection) {
        this.imageButtonSelection = imageButtonSelection;
        notifyPropertyChanged(BR.imageButtonSelection);
    }

    @Bindable
    public OffsetDateTime getBoxedSelection() {
        return boxedSelection;
    }

    @Bindable
    public OffsetDateTime getOutlinedSelection() {
        return outlinedSelection;
    }

    @Bindable
    public OffsetDateTime getButtonSelection() {
        return buttonSelection;
    }

    @Bindable
    public OffsetDateTime getImageButtonSelection() {
        return imageButtonSelection;
    }
}
