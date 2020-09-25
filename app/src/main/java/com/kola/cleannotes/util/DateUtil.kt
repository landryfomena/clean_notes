package com.kola.cleannotes.util


import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil
@Inject
constructor(
    private val dataFormat: SimpleDateFormat
) {
    // date format: "2019-07-23 HH:MM:SS"
    fun removeTimeFromString(sd: String): String {
        return sd.substring(0, sd.indexOf(" "))
    }

    fun convertFirebaseTimeStampToStringDate(timestamp: Timestamp): String {
        return dataFormat.format(timestamp.toDate())
    }

    fun convertStringDateToFirebasetimeStamp(date: String): Timestamp {
        return Timestamp(dataFormat.parse(date))
    }

    fun getCurrentTimeStamp(): String {
        return dataFormat.format(Date())
    }
}