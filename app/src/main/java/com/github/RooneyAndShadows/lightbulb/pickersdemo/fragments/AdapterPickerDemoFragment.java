package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable;
import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.pickersdemo.R;
import com.github.rooneyandshadows.lightbulb.pickersdemo.activity.MainActivity;
import com.github.rooneyandshadows.lightbulb.pickersdemo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.views.DemoAdapterPickerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterPickerDemoFragment extends BaseFragment {
    private DemoAdapterPickerView pickerViewBoxed;
    private DemoAdapterPickerView pickerViewOutlined;
    private DemoAdapterPickerView pickerViewButton;

    public static AdapterPickerDemoFragment getNewInstance() {
        return new AdapterPickerDemoFragment();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        return new BaseFragmentConfiguration()
                .withLeftDrawer(true)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(true)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.adapter_picker_demo_text))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adapter_picker_demo, container, false);
    }

    @Override
    protected void viewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.viewCreated(fragmentView, savedInstanceState);
        if (getFragmentState() == FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                    getContextActivity(),
                    MainActivity.class,
                    MenuConfigurations::getConfiguration
            );
        }
        setupDrawerButton();
        if (savedInstanceState == null)
            setupCollection();
    }

    @Override
    protected void selectViews() {
        super.selectViews();
        if (getView() == null)
            return;
        pickerViewBoxed = getView().findViewById(R.id.pickerViewBoxed);
        pickerViewOutlined = getView().findViewById(R.id.pickerViewOutlined);
        pickerViewButton = getView().findViewById(R.id.pickerViewButton);
    }

    private void setupDrawerButton() {
        ShowMenuDrawable actionBarDrawable = new ShowMenuDrawable(getContextActivity());
        actionBarDrawable.setEnabled(false);
        actionBarDrawable.setBackgroundColor(ResourceUtils.getColorByAttribute(getContextActivity(), R.attr.colorError));
        getActionBarManager().setHomeIcon(actionBarDrawable);
    }

    private void setupCollection() {
        pickerViewBoxed.setData(DemoModel.generateDemoCollection());
        pickerViewOutlined.setData(DemoModel.generateDemoCollection());
        pickerViewButton.setData(DemoModel.generateDemoCollection());
    }
}
