package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndViewBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentAdapterPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.getMenuBackDrawable
import com.github.rooneyandshadows.lightbulb.pickersdemo.getShowMenuDrawable

@FragmentScreen(screenName = "Adapter", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_adapter_picker_demo", hasLeftDrawer = true)
class FragmentAdapterPickerDemo :
    BaseFragmentWithViewModelAndViewBinding<FragmentAdapterPickerDemoBinding, VMAdapterPickerDemo>() {
    override val viewModelClass: Class<VMAdapterPickerDemo> = VMAdapterPickerDemo::class.java

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val ctx = requireContext()
        val title = ResourceUtils.getPhrase(ctx, R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(ctx, R.string.adapter_picker_demo_text)
        val showMenuDrawable = getShowMenuDrawable(ctx)
        return ActionBarConfiguration(R.id.toolbar).apply {
            withHomeIcon(showMenuDrawable)
            withActionButtons(true)
            withTitle(title)
            withSubTitle(subTitle)
        }
    }

    @Override
    override fun doOnViewBound(
        viewBinding: FragmentAdapterPickerDemoBinding,
        viewModel: VMAdapterPickerDemo,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState == null) {
            viewBinding.pickerViewBoxed.data = viewModel.dataSets[0]!!
            viewBinding.pickerViewOutlined.data = viewModel.dataSets[1]!!
            viewBinding.pickerViewButton.data = viewModel.dataSets[2]!!
            viewBinding.pickerViewImageButton.data = viewModel.dataSets[3]!!
        }
        viewBinding.model = viewModel
    }
}