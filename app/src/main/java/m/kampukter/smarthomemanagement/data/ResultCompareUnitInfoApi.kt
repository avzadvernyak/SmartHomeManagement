package m.kampukter.smarthomemanagement.data

sealed class ResultCompareUnitInfoApi{
    data class Success(val sensorValue: UnitApiView?) : ResultCompareUnitInfoApi()
    object EmptyResponse : ResultCompareUnitInfoApi()
    data class OtherError(val tError: String) : ResultCompareUnitInfoApi()
}
