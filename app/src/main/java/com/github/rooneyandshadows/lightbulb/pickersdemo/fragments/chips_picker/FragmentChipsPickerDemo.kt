package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.chips_picker

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndViewBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentChipsPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentIconPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.getShowMenuDrawable

@FragmentScreen(screenName = "Chips", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_chips_picker_demo", hasLeftDrawer = true)
class FragmentChipsPickerDemo :
    BaseFragmentWithViewModelAndViewBinding<FragmentChipsPickerDemoBinding, VMChipsPickerDemo>() {
    override val viewModelClass: Class<VMChipsPickerDemo> = VMChipsPickerDemo::class.java

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val ctx = requireContext()
        val title = ResourceUtils.getPhrase(ctx, R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(ctx, R.string.chips_picker_demo_text)
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
        viewBinding: FragmentChipsPickerDemoBinding,
        viewModel: VMChipsPickerDemo,
        savedInstanceState: Bundle?,
    ) {
        if (savedInstanceState == null) {
            viewBinding.pickerViewChips.data = viewModel.dataSet
        }
        viewBinding.model = viewModel
    }
}