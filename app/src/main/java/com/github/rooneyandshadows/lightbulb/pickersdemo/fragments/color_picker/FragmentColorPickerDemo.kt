package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndViewBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentColorPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.getShowMenuDrawable

@FragmentScreen(screenName = "Color", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_color_picker_demo", hasLeftDrawer = true)
class FragmentColorPickerDemo :
    BaseFragmentWithViewModelAndViewBinding<FragmentColorPickerDemoBinding, VMColorPickerDemo>() {
    override val viewModelClass: Class<VMColorPickerDemo> = VMColorPickerDemo::class.java

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val ctx = requireContext()
        val title = ResourceUtils.getPhrase(ctx, R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(ctx, R.string.color_picker_demo_text)
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
        viewBinding: FragmentColorPickerDemoBinding,
        viewModel: VMColorPickerDemo,
        savedInstanceState: Bundle?,
    ) {
        if (savedInstanceState == null) {
            viewBinding.pickerViewBoxed.data = viewModel.dataSet
            viewBinding.pickerViewOutlined.data = viewModel.dataSet
            viewBinding.pickerViewButton.data = viewModel.dataSet
            viewBinding.pickerViewImageButton.data = viewModel.dataSet
        }
        viewBinding.model = viewModel
    }
}