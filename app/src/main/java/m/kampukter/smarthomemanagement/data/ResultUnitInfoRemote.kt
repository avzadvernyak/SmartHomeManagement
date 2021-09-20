package m.kampukter.smarthomemanagement.data

sealed class ResultUnitInfoRemote{
    data class Success(val infoApi: List<UnitInfoRemote>) : ResultUnitInfoRemote()
    object EmptyResponse : ResultUnitInfoRemote()
    data class OtherError(val tError: String) : ResultUnitInfoRemote()
}
