package m.kampukter.smarthomemanagement.data.dto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import m.kampukter.smarthomemanagement.data.Sensor
import m.kampukter.smarthomemanagement.data.UnitData
import java.net.URL

interface DeviceInteractionApi {
    fun connect(url: URL)

    fun disconnect(url: URL)
/*
    fun commandSend(url: URL, command: String)
*/
    fun getUnitDataFlow(): MutableStateFlow<UnitData?>
}