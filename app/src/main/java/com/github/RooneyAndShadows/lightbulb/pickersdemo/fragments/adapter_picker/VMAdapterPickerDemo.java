package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker;

import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class VMAdapterPickerDemo extends BaseObservableViewModel {
    private UUID boxedSelection;
    private UUID outlinedSelection;
    private UUID buttonSelection;
    private UUID imageButtonSelection;
    private Map<Integer, List<DemoModel>> dataSets = new HashMap<>();

    public void initialize() {
        dataSets.put(0, DemoModel.generateDemoCollection());
        dataSets.put(1, DemoModel.generateDemoCollection());
        dataSets.put(2, DemoModel.generateDemoCollection());
        dataSets.put(3, DemoModel.generateDemoCollection());
        setBoxedSelection(dataSets.get(0).get(0).getId());
        setOutlinedSelection(dataSets.get(1).get(0).getId());
        setButtonSelection(dataSets.get(2).get(0).getId());
        setImageButtonSelection(dataSets.get(3).get(0).getId());
    }

    public void setBoxedSelection(UUID boxedSelection) {
        this.boxedSelection = boxedSelection;
        notifyPropertyChanged(BR.boxedSelection);
    }

    public void setOutlinedSelection(UUID outlinedSelection) {
        this.outlinedSelection = outlinedSelection;
        notifyPropertyChanged(BR.outlinedSelection);
    }

    public void setButtonSelection(UUID buttonSelection) {
        this.buttonSelection = buttonSelection;
        notifyPropertyChanged(BR.buttonSelection);
    }

    public void setImageButtonSelection(UUID imageButtonSelection) {
        this.imageButtonSelection = imageButtonSelection;
        notifyPropertyChanged(BR.imageButtonSelection);
    }

    public Map<Integer, List<DemoModel>> getDataSets() {
        return dataSets;
    }

    @Bindable
    public UUID getBoxedSelection() {
        return boxedSelection;
    }

    @Bindable
    public UUID getOutlinedSelection() {
        return outlinedSelection;
    }

    @Bindable
    public UUID getButtonSelection() {
        return buttonSelection;
    }

    @Bindable
    public UUID getImageButtonSelection() {
        return imageButtonSelection;
    }
}
