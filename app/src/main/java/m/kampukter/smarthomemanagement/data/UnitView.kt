package m.kampukter.smarthomemanagement.data

import java.util.*

sealed class UnitView {
    data class RelayView(
        val id: String,
        val name: String,
        var status: Boolean,
        var lastUpdateDate: Date
    ) : UnitView()

    data class SensorView(
        val id: String,
        val name: String,
        var value: Float,
        val dimension: String?,
        var lastUpdateDate: Date,
        val icon: Int
    ) : UnitView()
}
