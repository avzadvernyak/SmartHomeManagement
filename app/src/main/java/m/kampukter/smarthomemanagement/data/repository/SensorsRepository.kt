package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dao.SensorInfoDao
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import m.kampukter.smarthomemanagement.data.dto.SensorsDataApiInterface
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
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
                    apiSensorInfo.find { it.unit == "${sensor.unitId}${sensor.unitSensorId}" }
                val dateSensor =
                    if (apiSensor != null) Date(apiSensor.date * 1000L) else Calendar.getInstance().time

                sensorInfoList.add(
                    when (sensor.deviceType) {
                        DeviceType.Device -> {
                            SensorView(
                                sensor.id,
                                sensor.name,
                                apiSensor?.value ?: 0F,
                                sensor.measure,
                                dateSensor, sensor.icon
                            )
                        }
                        DeviceType.RELAY -> {
                            RelayView(
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
                        sensorInfoList.find { it.unitId == sensorData.deviceId && it.unitSensorId == sensorData.deviceSensorId }?.id?.let { foundId ->
                            initListSensorInfo.find { sensor ->
                                sensor.id == foundId
                            }?.let {
                                (it as SensorView).value = sensorData.value
                                it.lastUpdateDate = Calendar.getInstance().time
                            }

                        }
                    }
                    is SensorData.Relay -> {
                        sensorInfoList.find { it.unitId == sensorData.deviceId && it.unitSensorId == sensorData.deviceRelayId }?.id?.let { foundId ->
                            initListSensorInfo.find { relay ->
                                relay.id == foundId
                            }?.let {
                                (it as RelayView).state = sensorData.status
                                it.lastUpdateDate = Calendar.getInstance().time
                            }
                        }
                    }
                }
            }
            initListSensorInfo
        }

    fun getSensorsInfo(): Flow<List<SensorInfo>> = sensorInfoDao.getAllSensorsFlow()

    val unitStatusFlow: Flow<Pair<URL, WSConnectionStatus>?> =
        webSocketDto.getWSStatusFlow()

    val unitListFlow: Flow<List<UnitInfo>> = sensorInfoDao.getAllUnitsFlow()

    val unitInfoApiFlow: Flow<ResultUnitsInfoApi> = flow {
        emit(getResultUnitInfoApi())
    }

    fun getSearchSensorInfo(searchId: String): Flow<SensorInfo?> =
        sensorInfoDao.getSensorFlow(searchId)

    fun getSearchUnitInfo(searchId: String): Flow<UnitInfo> = sensorInfoDao.getUnitFlow(searchId)

    private suspend fun getResultUnitInfoApi(): ResultUnitsInfoApi {
        var response: Response<UnitInfoApi>? = null
        try {
            response = sensorApiInterface.getUnitInfoApi()
        } catch (e: IOException) {
            Log.e("blablabla", " Error in API $e")
            ResultUnitsInfoApi.OtherError("Error $e")
        }
        if (response?.code() == 204) return ResultUnitsInfoApi.EmptyResponse
        if (response?.code() != 200) return ResultUnitsInfoApi.OtherError("Error HTTP ${response?.code()}")

        val body = response.body()
        return if (body != null) ResultUnitsInfoApi.Success(body)
        else ResultUnitsInfoApi.OtherError("Response is null")

    }

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
        if (response?.code() == 204) return ResultSensorDataApi.EmptyResponse
        if (response?.code() != 200) return ResultSensorDataApi.OtherError("Error HTTP")
        val sensorDataList = response.body()
        return if (sensorDataList != null) ResultSensorDataApi.Success(sensorDataList)
        else ResultSensorDataApi.OtherError("Response is null")

    }

    fun getResultSensorDataApi(query: Triple<String, String, String>): Flow<ResultSensorDataApi> =
        flow {
            emit(getSensorData(query))
        }

    suspend fun sendCommand(relayId: String) {
        webSocketDto.commandSend(sensorInfoDao.getSensorById(relayId))
    }

    //Connect to WS Server
    suspend fun connectToUnit(sensorId: String) {
        webSocketDto.connect(sensorInfoDao.getSensorById(sensorId).unitIp)
    }

    suspend fun disconnectToUnit(sensorId: String) {
        webSocketDto.disconnect(sensorInfoDao.getSensorById(sensorId).unitIp)
    }

    suspend fun connectByIdUnit(unitId: String) {
        webSocketDto.connect(sensorInfoDao.getUnitById(unitId).url)
    }

    suspend fun editUnitDescription(unitId: String, description: String) {
        sensorInfoDao.editUnitDescription(unitId, description)
    }

    suspend fun editUnitName(unitId: String, name: String) {
        sensorInfoDao.editUnitName(unitId, name)
    }


    suspend fun insertUnit(unit: UnitInfo) {
        sensorInfoDao.insertUnit(unit)
    }

    suspend fun insertSensor(sensor: SensorInfo) {
        sensorInfoDao.insertSensor(sensor)
    }

    suspend fun deleteSensorById(sensorId: String) {
        sensorInfoDao.deleteSensorById(sensorId)
    }
}