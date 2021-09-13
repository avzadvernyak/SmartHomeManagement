package m.kampukter.smarthomemanagement.data

import androidx.room.*

@Entity(
    tableName = "sensor",
    foreignKeys = [ForeignKey(
        entity = UnitInfo::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("unit_id"),
    )],
    indices = [(Index(value = ["unit_id"], name = "idx_unit_id"))]
    )
data class SensorInfo(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "unit_id")
    val unitId: String,
    val unitSensorId: String,
    val name: String,
    val measure: String?,
    val deviceType: DeviceType,
    var icon: SensorType
)
