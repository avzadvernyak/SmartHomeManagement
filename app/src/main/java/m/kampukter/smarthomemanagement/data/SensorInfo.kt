package m.kampukter.smarthomemanagement.data

import androidx.room.*

@Entity(
    tableName = "sensor",
    foreignKeys = [ForeignKey(
        entity = UnitInfo::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("unit_id"),
    )])
data class SensorInfo(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "unit_id")
    val unitId: String,
    val unitSensorId: String,
    val name: String,
    val measure: String?,
    @TypeConverters(SensorTypeConverter::class)
    val type: SensorType,
    val icon: Int
)
enum class SensorType(val value: Int) { SENSOR(0), RELAY(1) }
