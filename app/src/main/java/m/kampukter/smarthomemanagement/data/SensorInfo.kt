package m.kampukter.smarthomemanagement.data

import androidx.room.*

@Entity(
    tableName = "sensor",
    foreignKeys = [ForeignKey(
        entity = UnitInfo::class,
        parentColumns = arrayOf("device_id"),
        childColumns = arrayOf("device_id"),
    )])
data class SensorInfo(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    val deviceSensorId: String,
    val name: String,
    val measure: String?,
    @TypeConverters(SensorTypeConverter::class)
    val type: SensorType,
    val icon: Int
)
enum class SensorType(val value: Int) { SENSOR(0), RELAY(1) }
