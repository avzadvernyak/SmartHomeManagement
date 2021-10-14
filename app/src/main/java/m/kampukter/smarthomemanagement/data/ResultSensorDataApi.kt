package m.kampukter.smarthomemanagement.data

sealed class ResultSensorDataApi {
    data class Success(val sensorValue: List<SensorDataApi>) : ResultSensorDataApi()
    data class EmptyResponse(val sensorId: String) : ResultSensorDataApi()
    data class OtherError(val tError: String) : ResultSensorDataApi()
}

