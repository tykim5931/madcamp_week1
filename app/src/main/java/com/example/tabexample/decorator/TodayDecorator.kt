package com.example.tabexample.decorator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Log

import com.example.tabexample.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class TodayDecorator(): DayViewDecorator {
    private var date = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?): Boolean{
        return day?.equals(date)!!
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#E48E8E")))
    }
}