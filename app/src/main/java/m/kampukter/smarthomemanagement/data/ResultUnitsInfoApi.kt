package m.kampukter.smarthomemanagement.data

sealed class ResultUnitsInfoApi {
    data class Success(val infoApi: UnitInfoApi) : ResultUnitsInfoApi()
    object EmptyResponse : ResultUnitsInfoApi()
    data class OtherError(val tError: String) : ResultUnitsInfoApi()
}
