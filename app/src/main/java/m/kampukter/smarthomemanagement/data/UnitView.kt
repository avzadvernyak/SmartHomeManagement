package m.kampukter.smarthomemanagement.data

import java.util.*

sealed class UnitView {
    data class RelayView(
        val id: String,
        val name: String,
        val status: Boolean,
        val lastUpdateDate: Date,
        val connectedStatus: Boolean
    ) : UnitView()

    data class SensorView(
        val id: String,
        val name: String,
        val value: Float,
        val lastUpdateDate: Date,
        val connectedStatus: Boolean
    ) : UnitView()
}
