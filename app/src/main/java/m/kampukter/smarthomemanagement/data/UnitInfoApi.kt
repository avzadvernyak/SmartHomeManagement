package m.kampukter.smarthomemanagement.data


data class UnitInfoApi(
    val updated: Long,
    val units: List<UnitApi>
)

data class UnitApi(
    val name: String,
    val url: String,
    val description: String,
    val sensors: List<SensorApi>
)

data class SensorApi(
    val unitSensorId: String,
    val name: String,
    val measure: String,
    val deviceType: DeviceType
)

data class UnitApiView(
    val name: String,
    val url: String,
    val description: String,
    val sensors: List<SensorApiView>
)

data class SensorApiView(
    val unitSensorId: String,
    val name: String,
    val measure: String,
    val deviceType: DeviceType,
    val compareStatus: CompareStatus
)

enum class CompareStatus {
    OK, DELETED, NEW, CHANGE_MEASURE, CHANGE_TYPE
}