package m.kampukter.smarthomemanagement.data

import java.util.*

sealed class UnitView {
    data class RelayView(
        val id: String,
        val name: String,
        var status: Boolean,
        var lastUpdateDate: Date,
        var connectedStatus: Boolean
    ) : UnitView()

    data class SensorView(
        val id: String,
        val name: String,
        var value: Float,
        var lastUpdateDate: Date,
        var connectedStatus: Boolean
    ) : UnitView()
}
