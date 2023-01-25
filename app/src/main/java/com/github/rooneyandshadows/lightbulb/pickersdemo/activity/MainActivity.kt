package com.github.rooneyandshadows.lightbulb.pickersdemo.activity

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.ActivityConfiguration
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.pickersdemo.R

@ActivityConfiguration
class MainActivity : BaseActivity() {
    private var router: AppRouter? = null

    override fun doBeforeCreate(savedInstanceState: Bundle?) {
        super.doBeforeCreate(savedInstanceState)
        setTheme(R.style.DemoTheme)
    }

    override fun doOnCreate(savedInstanceState: Bundle?) {
        super.doOnCreate(savedInstanceState)
       // if (savedInstanceState == null) router!!.toAdapterPickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO_AND_CLEAR_BACKSTACK)
    }
}