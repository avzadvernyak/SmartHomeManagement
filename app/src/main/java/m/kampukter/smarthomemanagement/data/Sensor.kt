package m.kampukter.smarthomemanagement.data

import java.util.*

data class Sensor(
    val deviceId: String,
    val deviceSensorId: String,
    val value: Float,
    val lastUpdateDate: Date,
    val lastStatus: Boolean
)