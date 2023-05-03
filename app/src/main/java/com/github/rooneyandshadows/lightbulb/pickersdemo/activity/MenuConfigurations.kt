package com.github.rooneyandshadows.lightbulb.pickersdemo.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.SliderMenu
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.config.SliderMenuConfiguration
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.items.PrimaryMenuItem
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.activity.MainActivityNavigator.*
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi

@Suppress("unused")
object MenuConfigurations {
    fun getConfiguration(context: Context): SliderMenuConfiguration {
        return SliderMenuConfiguration(R.layout.demo_drawer_header_view).apply {
            itemsList.apply {
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.adapter_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_ADAPTER_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoAdapter().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.color_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_COLOR_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoColor().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.icon_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_ICON_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoIcon().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.month_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_MONTH_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoMonth().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.date_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_DATE_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoDateAndTime().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.date_range_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_DATE_RANGE_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoDateRange().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.time_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_TIME_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoTime().replace()
                    }
                )
                add(
                    PrimaryMenuItem(
                        -1,
                        ResourceUtils.getPhrase(context, R.string.chips_picker_demo_text),
                        null,
                        AppIconUtils.getIconWithAttributeColor(
                            context,
                            DemoIconsUi.ICON_MENU_TIME_PICKER,
                            R.attr.colorOnBackground,
                            R.dimen.ICON_SIZE_MENU
                        ),
                        1
                    ) { slider: SliderMenu ->
                        slider.closeSlider()
                        route().toDemoChips().replace()
                    }
                )
            }
        }
    }
}