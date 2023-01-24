package com.github.rooneyandshadowss.lightbulb.pickersdemo.fragments.adapter_picker;

import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel;
import com.github.rooneyandshadowss.lightbulb.pickersdemo.models.DemoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class VMAdapterPickerDemo extends BaseObservableViewModel {
    private final List<UUID> boxedSelection = new ArrayList<>();
    private final List<UUID> outlinedSelection = new ArrayList<>();
    private final List<UUID> buttonSelection = new ArrayList<>();
    private final List<UUID> imageButtonSelection = new ArrayList<>();
    private final Map<Integer, List<DemoModel>> dataSets = new HashMap<>();

    public void initialize() {
        dataSets.put(0, DemoModel.generateDemoCollection());
        dataSets.put(1, DemoModel.generateDemoCollection());
        dataSets.put(2, DemoModel.generateDemoCollection());
        dataSets.put(3, DemoModel.generateDemoCollection());
        boxedSelection.add(dataSets.get(0).get(0).getId());
        outlinedSelection.add(dataSets.get(1).get(0).getId());
        buttonSelection.add(dataSets.get(2).get(0).getId());
        imageButtonSelection.add(dataSets.get(3).get(0).getId());
    }

    public void setBoxedSelection(List<UUID> boxedSelection) {
        this.boxedSelection.clear();
        this.boxedSelection.addAll(boxedSelection);
        notifyPropertyChanged(BR.boxedSelection);
    }

    public void setOutlinedSelection(List<UUID> outlinedSelection) {
        this.outlinedSelection.clear();
        this.outlinedSelection.addAll(outlinedSelection);
        notifyPropertyChanged(BR.outlinedSelection);
    }

    public void setButtonSelection(List<UUID> buttonSelection) {
        this.buttonSelection.clear();
        this.buttonSelection.addAll(buttonSelection);
        notifyPropertyChanged(BR.buttonSelection);
    }

    public void setImageButtonSelection(List<UUID> imageButtonSelection) {
        this.imageButtonSelection.clear();
        this.imageButtonSelection.addAll(imageButtonSelection);
        notifyPropertyChanged(BR.imageButtonSelection);
    }

    public Map<Integer, List<DemoModel>> getDataSets() {
        return dataSets;
    }

    @Bindable
    public List<UUID> getBoxedSelection() {
        return boxedSelection;
    }

    @Bindable
    public List<UUID> getOutlinedSelection() {
        return outlinedSelection;
    }

    @Bindable
    public List<UUID> getButtonSelection() {
        return buttonSelection;
    }

    @Bindable
    public List<UUID> getImageButtonSelection() {
        return imageButtonSelection;
    }
}
