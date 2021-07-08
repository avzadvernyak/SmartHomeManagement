package m.kampukter.smarthomemanagement.data

sealed class ResultSensorDataApi {
    data class Success(val sensorValue: List<SensorDataApi>) : ResultSensorDataApi()
    object EmptyResponse : ResultSensorDataApi()
    data class OtherError(val tError: String) : ResultSensorDataApi()
}

