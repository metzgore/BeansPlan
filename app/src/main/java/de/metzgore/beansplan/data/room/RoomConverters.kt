package de.metzgore.beansplan.data.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.GsonBuilder
import de.metzgore.beansplan.data.ShowResponse
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import java.util.*


object RoomConverters {

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun typeEnumToInt(type: ShowResponse.Type): Int {
        return type.value
    }

    @TypeConverter
    @JvmStatic
    fun intToTypeEnum(value: Int): ShowResponse.Type {
        for (type in ShowResponse.Type.values()) {
            if (type.value == value) {
                return type
            }
        }
        return ShowResponse.Type.NONE
    }

    @TypeConverter
    @JvmStatic
    fun weeklyScheduleResponseToString(raw: WeeklyScheduleResponse): String {
        val gson = GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss z yyyy")
                .enableComplexMapKeySerialization().create()
        return gson.toJson(raw)
    }

    @TypeConverter
    @JvmStatic
    fun stringToWeeklyScheduleResponse(raw: String): WeeklyScheduleResponse {
        val gson = GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss z yyyy")
                .enableComplexMapKeySerialization().create()
        return gson.fromJson(raw, WeeklyScheduleResponse::class.java)
    }
}