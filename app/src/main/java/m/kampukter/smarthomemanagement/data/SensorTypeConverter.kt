package m.kampukter.smarthomemanagement.data

import androidx.room.TypeConverter

class SensorTypeConverter {
    @TypeConverter
    fun fromSensorType(value: SensorType): Int{
        return value.ordinal
    }

    @TypeConverter
    fun toSensorType(value: Int): SensorType{
        return when(value){
            1 ->SensorType.RELAY
            else -> SensorType.SENSOR
        }
    }
}
