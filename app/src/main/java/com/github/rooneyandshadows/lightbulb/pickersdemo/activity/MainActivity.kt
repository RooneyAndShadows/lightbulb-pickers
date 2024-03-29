package com.github.rooneyandshadows.lightbulb.pickersdemo.activity

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.ActivityConfiguration
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.config.SliderMenuConfiguration
import com.github.rooneyandshadows.lightbulb.pickersdemo.R

@ActivityConfiguration
class MainActivity : BaseActivity() {

    @Override
    override fun doBeforeCreate(savedInstanceState: Bundle?) {
        super.doBeforeCreate(savedInstanceState)
        setTheme(R.style.DemoTheme)
    }

    @Override
    override fun getMenuConfiguration(): SliderMenuConfiguration {
        return MenuConfigurations.getConfiguration(this)
    }

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?) {
        super.doOnCreate(savedInstanceState)
        if (savedInstanceState == null) {
            MainActivityNavigator.route().toDemoAdapter().newRootScreen()
        }
    }
}