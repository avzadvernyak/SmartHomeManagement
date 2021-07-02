package m.kampukter.smarthomemanagement.data

import java.util.*

data class UnitData(
    val sensorDataList: List<SensorData>
)

sealed class SensorData {
    data class Relay(
        val deviceId: String,
        val deviceRelayId: String,
        val status: Boolean,
        val lastUpdateDate: Date
    ) : SensorData()

    data class Sensor(
        val deviceId: String,
        val deviceSensorId: String,
        val value: Float,
        val lastUpdateDate: Date

    ) : SensorData()
}


