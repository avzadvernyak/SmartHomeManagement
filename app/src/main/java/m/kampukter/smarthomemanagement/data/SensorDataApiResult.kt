package m.kampukter.smarthomemanagement.data

sealed class SensorDataApiResult {
    data class Success(val sensorValue: List<SensorDataApi>) : SensorDataApiResult()
    object EmptyResponse: SensorDataApiResult()
    data class OtherError( val tError: String ) : SensorDataApiResult()
}