package m.kampukter.smarthomemanagement.data

data class SensorInfoRemote(
    val id: String,
    val unitId: String,
    val unitSensorId: String,
    var name: String,
    val measure: String?,
    val deviceType: DeviceType,
    val isCandidate: Boolean
)
