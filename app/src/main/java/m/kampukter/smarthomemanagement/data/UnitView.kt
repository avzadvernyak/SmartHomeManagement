package m.kampukter.smarthomemanagement.data

import java.util.*

sealed class UnitView (open val id: String, open val name: String)

data class RelayView(
    override val id: String,
    override val name: String,
    var state: RelayState,
    var lastUpdateDate: Date
) : UnitView(id, name)

data class SensorView(
    override val id: String,
    override val name: String,
    var value: Float,
    val dimension: String?,
    var lastUpdateDate: Date,
    val icon: SensorType
) : UnitView(id, name)