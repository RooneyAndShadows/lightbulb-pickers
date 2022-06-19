package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker;

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
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentAdapterPickerDemoBinding;
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.views.DemoAdapterPickerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class FragmentAdapterPickerDemo extends BaseFragment {
    private VMAdapterPickerDemo viewModel;
    private DemoAdapterPickerView pickerViewBoxed;
    private DemoAdapterPickerView pickerViewOutlined;
    private DemoAdapterPickerView pickerViewButton;
    private DemoAdapterPickerView pickerViewImageButton;

    public static FragmentAdapterPickerDemo getNewInstance() {
        return new FragmentAdapterPickerDemo();
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
    protected void create(@Nullable Bundle savedInstanceState) {
        super.create(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(VMAdapterPickerDemo.class);
        if (savedInstanceState == null)
            viewModel.initialize();
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAdapterPickerDemoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_adapter_picker_demo, container, false);
        binding.setModel(viewModel);
        return binding.getRoot();
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
        pickerViewImageButton = getView().findViewById(R.id.pickerViewImageButton);
    }

    private void setupDrawerButton() {
        ShowMenuDrawable actionBarDrawable = new ShowMenuDrawable(getContextActivity());
        actionBarDrawable.setEnabled(false);
        actionBarDrawable.setBackgroundColor(ResourceUtils.getColorByAttribute(getContextActivity(), R.attr.colorError));
        getActionBarManager().setHomeIcon(actionBarDrawable);
    }

    private void setupCollection() {
        pickerViewBoxed.setData(viewModel.getDataSets().get(0));
        pickerViewOutlined.setData(viewModel.getDataSets().get(1));
        pickerViewButton.setData(viewModel.getDataSets().get(2));
        pickerViewImageButton.setData(viewModel.getDataSets().get(3));
    }
}
