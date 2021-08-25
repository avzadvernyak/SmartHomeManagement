package m.kampukter.smarthomemanagement.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "unit_remote",
    indices = [(androidx.room.Index(value = ["id"], name = "unit_remote_idx_id"))]
)
data class UnitInfoRemote(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    val name: String?,
    val url: String,
    var description: String?
)
