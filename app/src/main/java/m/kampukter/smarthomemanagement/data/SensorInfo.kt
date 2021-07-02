package m.kampukter.smarthomemanagement.data

import java.net.URL

data class SensorInfo(
    val id: String,
    val deviceId: String,
    val deviceSensorId: String,
    val name: String,
    val dimension: String?,
    val lanURL: URL,
    val wanURL: URL,
    val type: SensorType
)
enum class SensorType { SENSOR, RELAY }
