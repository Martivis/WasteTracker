package com.example.wastetracker.data.local

import androidx.room.TypeConverter
import com.example.wastetracker.data.model.OperationType
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromOperationType(value: OperationType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toOperationType(value: String?): OperationType? {
        return value?.let { OperationType.valueOf(it) }
    }
}
