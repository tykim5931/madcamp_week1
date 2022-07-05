package com.example.tabexample.decorator

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class DotDecorator(
    color_param: Int,
    dates_param: Collection<CalendarDay>
): DayViewDecorator {
    private val color = color_param
    private val dates = HashSet<CalendarDay>(dates_param)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(7.5F, color))
    }
}