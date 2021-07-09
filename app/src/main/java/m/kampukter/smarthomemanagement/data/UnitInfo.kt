package m.kampukter.smarthomemanagement.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "unit",
    indices = [(androidx.room.Index(value = ["device_id"], name = "idx_device_id"))]
)
data class UnitInfo(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    val deviceName: String?,
    val deviceIp: String,
    val deviceDescription: String?
)
