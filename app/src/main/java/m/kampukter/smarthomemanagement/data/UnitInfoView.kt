package m.kampukter.smarthomemanagement.data

import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus

data class UnitInfoView(
    val deviceId: String,
    val deviceName: String?,
    val deviceIp: String,
    val deviceDescription: String?,
    var wsConnectionStatus: WSConnectionStatus?
)
