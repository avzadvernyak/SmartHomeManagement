package m.kampukter.smarthomemanagement.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import m.kampukter.smarthomemanagement.data.dao.SensorInfoDao

@Database(
    version = 1, exportSchema = false, entities = [
        SensorInfo::class, UnitInfo::class
    ]
)

@TypeConverters(SensorTypeConverter::class)
abstract class SmartHomeDatabase: RoomDatabase() {
    abstract fun sensorInfoDao(): SensorInfoDao
}

