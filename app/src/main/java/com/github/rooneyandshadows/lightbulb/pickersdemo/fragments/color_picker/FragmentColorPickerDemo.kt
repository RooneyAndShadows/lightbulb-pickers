package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndDataBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentAdapterPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentColorPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker.VMAdapterPickerDemo

@FragmentScreen(screenName = "Color", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "R.layout.fragment_color_picker_demo")
class FragmentColorPickerDemo :
    BaseFragmentWithViewModelAndDataBinding<FragmentColorPickerDemoBinding, VMColorPickerDemo>() {
    @Override
    override fun getViewModelClass(): Class<VMColorPickerDemo> {
        return VMColorPickerDemo::class.java
    }

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val title = ResourceUtils.getPhrase(requireContext(), R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(requireContext(), R.string.color_picker_demo_text)
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .withTitle(title)
            .withSubTitle(subTitle)
    }

    @Override
    override fun initializeViewModel(viewModel: VMColorPickerDemo) {
        super.initializeViewModel(viewModel)
        if (getFragmentState() == FragmentStates.CREATED) viewModel.initialize()
    }

    @Override
    override fun doOnViewBound(viewBinding: FragmentColorPickerDemoBinding) {
        super.doOnViewBound(viewBinding)
        if (getFragmentState() == FragmentStates.CREATED) {
            viewBinding.pickerViewBoxed.data = viewModel.dataSets[0]!!
            viewBinding.pickerViewOutlined.data = viewModel.dataSets[1]!!
            viewBinding.pickerViewButton.data = viewModel.dataSets[2]!!
            viewBinding.pickerViewImageButton.data = viewModel.dataSets[3]!!
        }
    }

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?, viewModel: VMColorPickerDemo) {
        super.doOnCreate(savedInstanceState, viewModel)
        viewBinding.model = viewModel
    }
}