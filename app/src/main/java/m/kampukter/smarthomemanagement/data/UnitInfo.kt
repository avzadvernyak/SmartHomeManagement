package m.kampukter.smarthomemanagement.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "unit",
    indices = [(androidx.room.Index(value = ["id"], name = "idx_id"))]
)
data class UnitInfo(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    val name: String?,
    val url: String,
    val description: String?
)
