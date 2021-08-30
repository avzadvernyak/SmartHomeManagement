package m.kampukter.smarthomemanagement.data

data class SensorFullInfo(
    val id: String,
    val unitId: String,
    val unitSensorId: String,
    val unitName: String?,
    val unitUrl: String,
    val unitDescription: String?,
    var sensorName: String,
    val sensorMeasure: String?,
    val sensorDeviceType: DeviceType,
    var sensorType: SensorType
)
