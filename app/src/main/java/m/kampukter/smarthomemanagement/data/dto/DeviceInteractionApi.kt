package m.kampukter.smarthomemanagement.data.dto

import kotlinx.coroutines.flow.MutableStateFlow
import m.kampukter.smarthomemanagement.data.SensorInfoWithIp
import m.kampukter.smarthomemanagement.data.UnitData
import java.net.URL

interface DeviceInteractionApi {

    fun connect(urlUnit: String)
    fun disconnect(urlUnit: String)

    suspend fun commandSend(sensorInfo: SensorInfoWithIp)
    fun getUnitDataFlow(): MutableStateFlow<UnitData?>
    fun getWSStatusFlow(): MutableStateFlow<Pair<URL, WSConnectionStatus>?>
}