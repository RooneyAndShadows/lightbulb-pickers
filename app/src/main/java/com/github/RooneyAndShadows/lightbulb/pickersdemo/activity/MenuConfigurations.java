package com.github.rooneyandshadows.lightbulb.pickersdemo.activity;

import android.view.View;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.config.SliderMenuConfiguration;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.items.PrimaryMenuItem;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.pickersdemo.R;

import java.util.function.Consumer;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MenuConfigurations {

    public static SliderMenuConfiguration getConfiguration(BaseActivity activity) {
        View headingView = activity.getLayoutInflater().inflate(R.layout.demo_drawer_header_view, null);
        SliderMenuConfiguration configuration = new SliderMenuConfiguration();
        configuration.withHeaderView(headingView);
        configuration.addMenuItem(new PrimaryMenuItem(
                -1,
                ResourceUtils.getPhrase(activity, R.string.adapter_picker_demo_text),
                null,
                null,
                1,
                action((slider) -> {
                    Router.getInstance().getRouter().toDemoRegular(BaseApplicationRouter.NavigationCommands.BACK_TO);
                    slider.closeSlider();
                })
        ));
        return configuration;
    }

    public static <T> Function1<T, Unit> action(Consumer<T> callable) {
        return t -> {
            callable.accept(t);
            return Unit.INSTANCE;
        };
    }
}