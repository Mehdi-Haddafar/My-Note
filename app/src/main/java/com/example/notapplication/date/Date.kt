package com.example.sequnsejenate.date

import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat


class Date {

    fun getPersianDate(): String {
        val persianDate = PersianDate()
        val formatter = PersianDateFormat("Y/m/d")
        return formatter.format(persianDate)
    }

}