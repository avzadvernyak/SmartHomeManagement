package m.kampukter.smarthomemanagement.data

data class SensorInfo(
    val id: String,
    val deviceId: String,
    val deviceSensorId: String,
    val name: String,
    val dimension: String?,
    val type: SensorType
)
enum class SensorType { SENSOR, RELAY }
