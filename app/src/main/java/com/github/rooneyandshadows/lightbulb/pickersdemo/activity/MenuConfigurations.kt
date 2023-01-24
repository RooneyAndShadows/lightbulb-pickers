package com.github.rooneyandshadows.lightbulb.pickersdemo.activity

import android.view.View
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import java.util.function.Consumer

object MenuConfigurations {
    fun getConfiguration(activity: BaseActivity): SliderMenuConfiguration {
        val headingView: View = activity.getLayoutInflater().inflate(R.layout.demo_drawer_header_view, null)
        val configuration = SliderMenuConfiguration()
        configuration.withHeaderView(headingView)
        configuration.addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.adapter_picker_demo_text),
                null,
                AppIconUtils.getIconWithAttributeColor(
                    activity,
                    DemoIconsUi.ICON_MENU_ADAPTER_PICKER,
                    R.attr.colorOnBackground,
                    R.dimen.ICON_SIZE_MENU
                ),
                1,
                action(Consumer<T> { slider: T ->
                    Router.Companion.getInstance().getRouter()
                        .toAdapterPickerDemo(BaseApplicationRouter.NavigationCommands.BACK_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.color_picker_demo_text),
                null,
                AppIconUtils.getIconWithAttributeColor(
                    activity,
                    DemoIconsUi.ICON_MENU_COLOR_PICKER,
                    R.attr.colorOnBackground,
                    R.dimen.ICON_SIZE_MENU
                ),
                1,
                action(Consumer<T> { slider: T ->
                    Router.Companion.getInstance().getRouter()
                        .toColorPickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.icon_picker_demo_text),
                null,
                AppIconUtils.getIconWithAttributeColor(
                    activity,
                    DemoIconsUi.ICON_MENU_ICON_PICKER,
                    R.attr.colorOnBackground,
                    R.dimen.ICON_SIZE_MENU
                ),
                1,
                action(Consumer<T> { slider: T ->
                    Router.Companion.getInstance().getRouter()
                        .toIconPickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.month_picker_demo_text),
                null,
                AppIconUtils.getIconWithAttributeColor(
                    activity,
                    DemoIconsUi.ICON_MENU_MONTH_PICKER,
                    R.attr.colorOnBackground,
                    R.dimen.ICON_SIZE_MENU
                ),
                1,
                action(Consumer<T> { slider: T ->
                    Router.Companion.getInstance().getRouter()
                        .toMonthPickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.date_picker_demo_text),
                null,
                AppIconUtils.getIconWithAttributeColor(
                    activity,
                    DemoIconsUi.ICON_MENU_DATE_PICKER,
                    R.attr.colorOnBackground,
                    R.dimen.ICON_SIZE_MENU
                ),
                1,
                action(Consumer<T> { slider: T ->
                    Router.Companion.getInstance().getRouter()
                        .toDatePickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        ).addMenuItem(
            PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.date_range_picker_demo_text),
                null,
                AppIconUtils.getIconWithAttributeColor(
                    activity,
                    DemoIconsUi.ICON_MENU_DATE_RANGE_PICKER,
                    R.attr.colorOnBackground,
                    R.dimen.ICON_SIZE_MENU
                ),
                1,
                action(Consumer<T> { slider: T ->
                    Router.Companion.getInstance().getRouter()
                        .toDateRangePickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO)
                    slider.closeSlider()
                })
            )
        )
        return configuration
    }

    fun <T> action(callable: Consumer<T>): Function1<T, Unit> {
        return { t: T ->
            callable.accept(t)
            Unit
        }
    }
}