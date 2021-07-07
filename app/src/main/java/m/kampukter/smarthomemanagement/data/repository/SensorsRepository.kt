package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import m.kampukter.smarthomemanagement.data.dto.SensorsDataApi
import retrofit2.Call
import retrofit2.Callback
import java.net.URL
import java.util.*

class SensorsRepository(private val webSocketDto: DeviceInteractionApi) {


    // For test - BEGIN
    private val listSensorInfo = listOf(
        SensorInfo(
            "1", "ESP8266-2", "1", "Thermometer", "C",
            SensorType.SENSOR
        ), SensorInfo(
            "2", "ESP8266-1", "2", "Термометр в тамбуре", "C",
            SensorType.SENSOR
        ), SensorInfo(
            "3", "ESP8266-1", "1", "Влажность", "%",
            SensorType.SENSOR
        )
    )
    private val sensorInfoListFlow: Flow<List<SensorInfo>> = flow { emit(listSensorInfo) }

    private val listUnitInfo = listOf(
        UnitInfoIp(
            "1", "ESP8266-1", "http://192.168.0.82:81/", "LAN", null
        ), UnitInfoIp(
            "2", "ESP8266-1", "http://109.254.66.131:81/", "WAN", null
        ), UnitInfoIp(
            "3", "ESP8266-2", "http://192.168.0.83:81/", "LAN", null
        ), UnitInfoIp(
            "4", "ESP8266-2", "http://109.254.66.131:83/", "wAN", null
        )
    )
    private val listURL = listUnitInfo.filter { it.typeIp == "LAN" }.map { URL(it.deviceIp) }
    private val sensorURLList: Flow<List<URL>> = flow { emit(listURL) }

    // For test - END
    private val sensorDataApi = SensorsDataApi.create()
    private val apiSensorsDataFlow = MutableStateFlow<List<SensorDataApi>?>(null)
    private fun initListSensorInfo(): MutableList<UnitView> {
        val sensorInfoList = mutableListOf<UnitView>()
        /*getSensorsLastData { apiData ->
            CoroutineScope(Dispatchers.IO + coroutineContext).launch {
                apiSensorsDataFlow.emit(apiData)
            }
        }*/
        listSensorInfo.forEach {
            sensorInfoList.add(
                when (it.type) {
                    SensorType.SENSOR -> {
                        UnitView.SensorView(
                            it.id,
                            it.name,
                            0F,
                            Calendar.getInstance().time,
                            false
                        )
                    }
                    SensorType.RELAY -> {
                        UnitView.RelayView(
                            it.id,
                            it.name,
                            false,
                            Calendar.getInstance().time,
                            false
                        )
                    }
                }
            )
        }
        return sensorInfoList
    }

    private val lastSensorInfoList = initListSensorInfo()

    private val unitDataFlow = webSocketDto.getUnitDataFlow()

    val sensorListFlow: Flow<List<UnitView>> =
        combine(
            unitDataFlow,
            sensorInfoListFlow,
            apiSensorsDataFlow
        ) { unitData, sensorInfoList, apiData ->

            apiData?.let {
                Log.w("blabla", "Emit apiDataFlow ${it.size}")
            }
            // Данные от учтройств по паре id устройства/id сенсора сопоставляем с id сенсора внутри проекта
            // и в случае нахождения меняем значение
            unitData?.sensorDataList?.forEach { sensorDate ->
                when (sensorDate) {
                    is SensorData.Sensor -> {
                        sensorInfoList.find { it.deviceId == sensorDate.deviceId && it.deviceSensorId == sensorDate.deviceSensorId }?.id?.let { foundId ->
                            lastSensorInfoList.find { sensor ->
                                (sensor as UnitView.SensorView).id == foundId
                            }?.let {
                                (it as UnitView.SensorView).value = sensorDate.value
                                it.lastUpdateDate = Calendar.getInstance().time
                            }

                        }
                    }
                    is SensorData.Relay -> {
                        sensorInfoList.find { it.deviceId == sensorDate.deviceId && it.deviceSensorId == sensorDate.deviceRelayId }?.id?.let { foundId ->
                            lastSensorInfoList.find { relay ->
                                if (relay is UnitView.RelayView) relay.id == foundId else false
                            }?.let {
                                (it as UnitView.RelayView).status = sensorDate.status
                                it.lastUpdateDate = Calendar.getInstance().time
                            }
                        }
                    }
                }
            }
            lastSensorInfoList
        }

    //Connect to WS Server
    suspend fun connectToWS() {
        sensorURLList.collect { value ->
            value.forEach { url ->
                webSocketDto.connect(url)
            }
        }
    }

    //Disconnect to WS Server
    suspend fun disconnectToWS() {
        sensorURLList.collect { value ->
            value.forEach { url ->
                webSocketDto.disconnect(url)
            }
        }
    }

    private suspend fun getSensorsLastData(): List<SensorDataApi> {
        var result = listOf<SensorDataApi>()
        val call = sensorDataApi.getLastDataSensor()
        call.enqueue(object : Callback<List<SensorDataApi>> {

            override fun onResponse(
                call: Call<List<SensorDataApi>>,
                response: retrofit2.Response<List<SensorDataApi>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.first()?.date != 0L) body?.let { result = it}
                    else Log.w("blabla", "API - EmptyResponse")
                } else Log.w("blabla", "API - isSuccessful is false")
            }

            override fun onFailure(call: Call<List<SensorDataApi>>, t: Throwable) {
                t.message?.let { Log.w("blabla", "API - OtherError- $it") }
            }
        }
        )
        return result
    }

    private suspend fun getSensorsLastData(event: ((List<SensorDataApi>) -> Unit)) {
        val call = sensorDataApi.getLastDataSensor()

        call.enqueue(object : Callback<List<SensorDataApi>> {

            override fun onResponse(
                call: Call<List<SensorDataApi>>,
                response: retrofit2.Response<List<SensorDataApi>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.first()?.date != 0L) body?.let { event.invoke(it) }
                    else Log.w("blabla", "API - EmptyResponse")
                } else Log.w("blabla", "API - isSuccessful is false")
            }

            override fun onFailure(call: Call<List<SensorDataApi>>, t: Throwable) {
                t.message?.let { Log.w("blabla", "API - OtherError- $it") }
            }
        }
        )
    }
}