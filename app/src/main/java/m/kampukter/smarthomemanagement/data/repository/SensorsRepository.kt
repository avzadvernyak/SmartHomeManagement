package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import m.kampukter.smarthomemanagement.data.dto.SensorsDataApi
import retrofit2.Call
import retrofit2.Callback
import java.net.URL
import java.util.*

class SensorsRepository(private val webSocketDto: DeviceInteractionApi) {

    // For test - BEGIN
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
    // For test - END

    private val sensorDataApi = SensorsDataApi.create()

    @DelicateCoroutinesApi
    private val apiSensorsDataFlow = MutableStateFlow<List<SensorDataApi>?>(null).apply {
        getSensorsLastData { apiData ->
            CoroutineScope(Dispatchers.IO + GlobalScope.coroutineContext).launch {
                emit(apiData)
            }
        }
    }

    @DelicateCoroutinesApi
    private val initListSensorInfoFlow: Flow<List<UnitView>> =
        combine(getSensorsInfo(), apiSensorsDataFlow) { sensorInfo, apiSensorInfo ->
            val sensorInfoList = mutableListOf<UnitView>()
            sensorInfo.forEach { sensor ->

                val apiSensor =
                    apiSensorInfo?.find { it.unit == "${sensor.deviceId}${sensor.deviceSensorId}" }
                val dateSensor =
                    if (apiSensor != null) Date(apiSensor.date * 1000L) else Calendar.getInstance().time

                sensorInfoList.add(
                    when (sensor.type) {
                        SensorType.SENSOR -> {
                            UnitView.SensorView(
                                sensor.id,
                                sensor.name,
                                apiSensor?.value ?: 0F,
                                sensor.dimension,
                                dateSensor, sensor.icon
                            )
                        }
                        SensorType.RELAY -> {
                            UnitView.RelayView(
                                sensor.id,
                                sensor.name,
                                apiSensor != null && apiSensor.value != 0F,
                                dateSensor
                            )
                        }
                    }
                )
            }

            sensorInfoList
        }

    private val unitDataFlow = webSocketDto.getUnitDataFlow()

    @DelicateCoroutinesApi
    val sensorListFlow: Flow<List<UnitView>> =
        combine(
            initListSensorInfoFlow,
            unitDataFlow,
            getSensorsInfo()
        ) { initListSensorInfo, unitData, sensorInfoList ->

            // Данные от учтройств по паре id устройства/id сенсора сопоставляем с id сенсора внутри проекта
            // и в случае нахождения меняем значение
            unitData?.sensorDataList?.forEach { sensorData ->
                when (sensorData) {
                    is SensorData.Sensor -> {
                        sensorInfoList.find { it.deviceId == sensorData.deviceId && it.deviceSensorId == sensorData.deviceSensorId }?.id?.let { foundId ->
                            initListSensorInfo.find { sensor ->
                                (sensor as UnitView.SensorView).id == foundId
                            }?.let {
                                (it as UnitView.SensorView).value = sensorData.value
                                it.lastUpdateDate = Calendar.getInstance().time
                            }

                        }
                    }
                    is SensorData.Relay -> {
                        sensorInfoList.find { it.deviceId == sensorData.deviceId && it.deviceSensorId == sensorData.deviceRelayId }?.id?.let { foundId ->
                            initListSensorInfo.find { relay ->
                                if (relay is UnitView.RelayView) relay.id == foundId else false
                            }?.let {
                                (it as UnitView.RelayView).status = sensorData.status
                                it.lastUpdateDate = Calendar.getInstance().time
                            }
                        }
                    }
                }
            }
            initListSensorInfo
        }

    // For test, а потом получение из таблицы рума
    private val sensorURLList: Flow<List<URL>> = flow { emit(listURL) }

    // For test, а потом получение из таблицы рума
    private val sensorInfo = listOf(
        SensorInfo(
            "1", "ESP8266-2", "1", "Thermometer", "°C",
            SensorType.SENSOR, 1
        ), SensorInfo(
            "2", "ESP8266-1", "0", "Температура на улице", "°C",
            SensorType.SENSOR, 1
        ), SensorInfo(
            "3", "ESP8266-1", "1", "Термометр в тамбуре", "°C",
            SensorType.SENSOR, 1
        ), SensorInfo(
            "4", "ESP8266-1", "2", "Атмосферное давление", "mm Hg",
            SensorType.SENSOR, 2
        ), SensorInfo(
            "5", "ESP8266-1", "3", "Влажность", "%",
            SensorType.SENSOR, 3
        )
    )

    private fun getSensorsInfo(): Flow<List<SensorInfo>> = flow {
        // For test, а потом получение из таблицы рума
        emit(sensorInfo)
    }

    fun getSearchSensorInfo(searchId: String): Flow<SensorInfo?> = flow {
        // For test, а потом получение из таблицы рума
        emit(sensorInfo.find { it.id == searchId })
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

    private fun getSensorsLastData(result: ((List<SensorDataApi>) -> Unit)) {
        val call = sensorDataApi.getLastDataSensor()
        call.enqueue(object : Callback<List<SensorDataApi>> {

            override fun onResponse(
                call: Call<List<SensorDataApi>>,
                response: retrofit2.Response<List<SensorDataApi>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.first()?.date != 0L) body?.let { result.invoke(it) }
                    else Log.w("blabla", "API - EmptyResponse")
                } else Log.w("blabla", "API - isSuccessful is false")
            }

            override fun onFailure(call: Call<List<SensorDataApi>>, t: Throwable) {
                t.message?.let { Log.w("blabla", "API - OtherError- $it") }
            }
        }
        )
    }

    fun getResultSensorDataApi(query: Triple<String, String, String>): Flow<ResultSensorDataApi> =
        flow {
            emit(getSensorData(query))
        }


    private fun getSensorData(query: Triple<String, String, String>): ResultSensorDataApi {
        val (name, b_date, e_date) = query
        var result: ResultSensorDataApi = ResultSensorDataApi.EmptyResponse
        val call = sensorDataApi.getInfoSensorPeriod(name, b_date, e_date)
        call.enqueue(object : Callback<List<SensorDataApi>> {

            override fun onResponse(
                call: Call<List<SensorDataApi>>,
                response: retrofit2.Response<List<SensorDataApi>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.first()?.date != 0L) body?.let {
                        result = ResultSensorDataApi.Success(it)
                    }
                    else result = ResultSensorDataApi.EmptyResponse
                } else result =  ResultSensorDataApi.OtherError("isSuccessful is false")
            }

            override fun onFailure(call: Call<List<SensorDataApi>>, t: Throwable) {
                t.message?.let { result = ResultSensorDataApi.OtherError(t.toString()) }
            }
        }
        )
        return result
    }
}