package m.kampukter.smarthomemanagement.data

import androidx.room.*

@Entity(
    tableName = "sensor_remote",
    foreignKeys = [ForeignKey(
        entity = UnitInfoRemote::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("unit_id"),
    )])
data class SensorInfoRemote(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "unit_id")
    val unitId: String,
    val unitSensorId: String,
    val name: String,
    val measure: String?,
    val deviceType: DeviceType,
    val isCandidate: Boolean
)
