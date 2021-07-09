package m.kampukter.smarthomemanagement.data

data class SensorInfo(
    val id: String,
    val deviceId: String,
    val deviceSensorId: String,
    val name: String,
    val measure: String?,
    val type: SensorType,
    val icon: Int
)
enum class SensorType { SENSOR, RELAY }
