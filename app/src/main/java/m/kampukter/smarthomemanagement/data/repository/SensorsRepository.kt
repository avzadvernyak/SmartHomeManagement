package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dao.SensorInfoDao
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import m.kampukter.smarthomemanagement.data.dto.SensorsDataApiInterface
import retrofit2.Response
import java.io.IOException
import java.net.URL
import java.util.*

class SensorsRepository(
    private val sensorInfoDao: SensorInfoDao,
    private val webSocketDto: DeviceInteractionApi,
    private val sensorApiInterface: SensorsDataApiInterface
) {

    private val apiSensorsDataFlow: Flow<List<SensorDataApi>> = flow {
        emit(getSensorsLastData())
    }

    private val initListSensorInfoFlow: Flow<List<UnitView>> =
        combine(getSensorsInfo(), apiSensorsDataFlow) { sensorInfo, apiSensorInfo ->
            val sensorInfoList = mutableListOf<UnitView>()
            sensorInfo.forEach { sensor ->

                val apiSensor =
                    apiSensorInfo.find { it.unit == "${sensor.deviceId}${sensor.deviceSensorId}" }
                val dateSensor =
                    if (apiSensor != null) Date(apiSensor.date * 1000L) else Calendar.getInstance().time

                sensorInfoList.add(
                    when (sensor.type) {
                        SensorType.SENSOR -> {
                            UnitView.SensorView(
                                sensor.id,
                                sensor.name,
                                apiSensor?.value ?: 0F,
                                sensor.measure,
                                dateSensor, sensor.icon
                            )
                        }
                        SensorType.RELAY -> {
                            UnitView.RelayView(
                                sensor.id,
                                sensor.name,
                                when (apiSensor?.value) {
                                    0F -> RelayState.OFF
                                    1F -> RelayState.ON
                                    else -> RelayState.OFFLINE
                                },
                                dateSensor
                            )
                        }
                    }
                )
            }
            sensorInfoList
        }

    private val unitDataFlow = webSocketDto.getUnitDataFlow()

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

    private fun getSensorsInfo(): Flow<List<SensorInfo>> = sensorInfoDao.getAllSensorsFlow()
    fun getUnitsInfo(): Flow<List<UnitInfo>> = sensorInfoDao.getAllUnitsFlow()

    fun getSearchSensorInfo(searchId: String): Flow<SensorInfo?> =
        sensorInfoDao.getSensorFlow(searchId)

    private suspend fun getSensorsLastData(): List<SensorDataApi> {
        var response: Response<List<SensorDataApi>>? = null
        try {
            response = sensorApiInterface.getLastDataSensor()
        } catch (e: IOException) {
            Log.e("blablabla", " Error in API $e")
        }

        if (response?.code() != 200) return emptyList()

        val sensorDataList = response.body()

        return if (sensorDataList.isNullOrEmpty()) emptyList()
        else sensorDataList

    }

    private suspend fun getSensorData(query: Triple<String, String, String>): ResultSensorDataApi {
        val (nameSensor, b_date, e_date) = query
        var response: Response<List<SensorDataApi>>? = null
        try {
            response = sensorApiInterface.getInfoSensorPeriod(nameSensor, b_date, e_date)
        } catch (e: IOException) {
            Log.e("blablabla", "API Unknown Error")
            ResultSensorDataApi.OtherError("API Unknown Error")
        }
        if (response?.code() != 200) return ResultSensorDataApi.OtherError("Error HTTP")
        val sensorDataList = response.body()
        return if (sensorDataList.isNullOrEmpty()) ResultSensorDataApi.EmptyResponse
        else ResultSensorDataApi.Success(sensorDataList)
    }

    fun getResultSensorDataApi(query: Triple<String, String, String>): Flow<ResultSensorDataApi> =
        flow {
            emit(getSensorData(query))
        }

    suspend fun sendCommand(relayInfo: UnitView.RelayView) {
        webSocketDto.commandSend(sensorInfoDao.getRelayById(relayInfo.id))
    }
    //Connect to WS Server
    fun connectToUnit(urlUnit: URL) {
        webSocketDto.connect( urlUnit )
    }
    //Disconnect to WS Server
    fun disconnectToUnit(urlUnit: URL) {
        webSocketDto.disconnect( urlUnit )
    }
}