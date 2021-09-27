package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.flow.*
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
        //combine(getSensorsInfo(), apiSensorsDataFlow) { sensorInfo, apiSensorInfo ->
        combine(sensorsInfoDao, apiSensorsDataFlow) { sensorInfo, apiSensorInfo ->
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
            //getSensorsInfo()
            sensorsInfoDao
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

    private val sensorsInfoDao: Flow<List<SensorInfo>>
        get() = sensorInfoDao.getAllSensorsFlow()

    val unitStatusFlow: Flow<Pair<URL, WSConnectionStatus>?> =
        webSocketDto.getWSStatusFlow()

    fun getSensorListByUnitId(searchId: String): Flow<List<SensorInfoRemote>> =
        combine(resultUnitInfoApiFlow, sensorsInfoDao) { unitInfoApi, sensors ->
            var sensorList = emptyList<SensorInfoRemote>()
            if (unitInfoApi is ResultUnitsInfoApi.Success) {
                unitInfoApi.infoApi.units.find { it.name == searchId }?.sensors?.map {
                    SensorInfoRemote(
                        id = UUID.randomUUID().toString(),
                        unitId = searchId,
                        unitSensorId = it.unitSensorId,
                        name = it.name,
                        measure = it.measure,
                        deviceType = it.deviceType,
                        isCandidate = sensors.find { sensor -> it.unitSensorId == sensor.unitSensorId && sensor.unitId == searchId } == null
                    )
                }?.let { sensorList = it }
            }
            sensorList
        }

    fun getSearchSensorInfo(searchId: String): Flow<SensorInfo?> =
        sensorInfoDao.getSensorFlow(searchId)

    fun getSearchUnitInfo(searchId: String): Flow<UnitInfo> = sensorInfoDao.getUnitFlow(searchId)

    private val resultUnitInfoApiFlow =
        MutableStateFlow<ResultUnitsInfoApi>(ResultUnitsInfoApi.EmptyResponse)

    suspend fun getUnitInfoApi() {
        resultUnitInfoApiFlow.value = getResultUnitInfoApi()
    }

    val unitsAllDao: Flow<List<UnitInfo>>
        get() = sensorInfoDao.getAllUnitsFlow()

    val unitInfoRemoteFlow: Flow<ResultUnitInfoRemote> =
        combine(resultUnitInfoApiFlow, unitsAllDao) { resultApi, units ->
            when (resultApi) {
                is ResultUnitsInfoApi.Success -> {
                    val unitList = resultApi.infoApi.units.map {
                        UnitInfoRemote(
                            id = it.name,
                            name = units.find { unit -> it.name == unit.id }?.name ?: it.name,
                            url = it.url,
                            description = it.description
                        )
                    }
                    ResultUnitInfoRemote.Success(unitList)
                }
                is ResultUnitsInfoApi.EmptyResponse -> {
                    ResultUnitInfoRemote.EmptyResponse
                }
                is ResultUnitsInfoApi.OtherError -> {
                    ResultUnitInfoRemote.OtherError(resultApi.tError)
                }
            }
        }

    private suspend fun getResultUnitInfoApi(): ResultUnitsInfoApi {
        var response: Response<UnitInfoApi>? = null
        try {
            response = sensorApiInterface.getUnitInfoApi()
        } catch (e: IOException) {
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
        sensorInfoDao.getSensorById(relayId)?.let { webSocketDto.commandSend(it) }
    }

    //Connect to WS Server (sensor or relay Id)
    suspend fun connectToUnit(sensorId: String) {
        sensorInfoDao.getSensorById(sensorId)?.let { webSocketDto.connect(it.unitIp) }
    }

    suspend fun disconnectToUnit(sensorId: String) {
        sensorInfoDao.getSensorById(sensorId)?.let { webSocketDto.disconnect(it.unitIp) }
    }

    //Connect to WS Server (unit Id)
    suspend fun connectByIdUnit(unitId: String) {
        webSocketDto.connect(sensorInfoDao.getUnitById(unitId).url)
    }


    suspend fun editUnitDescription(unitId: String, description: String) {
        sensorInfoDao.editUnitDescription(unitId, description)
    }

    suspend fun editUnitName(unitId: String, name: String) {
        sensorInfoDao.editUnitName(unitId, name)
    }

    suspend fun editUnitUrl(unitId: String, url: String) {
        sensorInfoDao.editUnitUrl(unitId, url)
    }

    suspend fun insertUnit(unit: UnitInfo) {
        sensorInfoDao.insertUnit(unit)
    }

    suspend fun insertSensor(sensor: SensorInfo) {
        sensorInfoDao.insertSensor(sensor)
    }

    suspend fun deleteSensorById(sensorId: String) {
        sensorInfoDao.getSensorById(sensorId)?.let { webSocketDto.disconnect(it.unitIp) }
        sensorInfoDao.deleteSensorById(sensorId)
    }

    fun getSearchSensorList(search: Any?): Flow<List<UnitView>> =
        sensorListFlow.combine(sensorsInfoDao) { list, sensorsInfo ->
            when (search) {
                is SensorType -> {
                    list.filter {
                        when (it) {
                            is RelayView -> search == SensorType.SWITCH
                            is SensorView -> search == it.icon
                        }
                    }
                }
                is UnitInfo -> {
                    val sensorsInSearch =
                        sensorsInfo.filter { it.unitId == search.id }.map { it.id }
                    list.filter { it.id in sensorsInSearch }
                }
                else -> {
                    emptyList()
                }
            }
        }

    fun compareUnitInfoApi() {
        // На входе две коллекции, одна полученная от Api(UnitInfoApi), вторая локальная (List<SensorInfo>)

        /*val updatedDataApi = getDataApi.updated
        val unitsDataApi = getDataApi.units
        Log.d(
            "blabla",
            "-> $unitsDataApi"
        )
        unitsDataApi.forEach { unit ->
            val unitRemote = sensorRemoteDao.getUnitRemoteByName(unit.name)
            if (unitRemote == null) Log.d(
                "blabla",
                "Появилось новое устройство -> ${unit.name}"
            )
            else {
                if (unit.url != unitRemote.url) Log.d(
                    "blabla",
                    "Изменился URL у устройства -> ${unit.name}"
                )
            }
            unit.sensors.forEach { sensor ->
                val sensorRemote =
                    sensorRemoteDao.getSensorRemoteByName(unit.name, sensor.unitSensorId)
                if (sensorRemote == null) Log.d(
                    "blabla",
                    "Появилось новый сенсор -> ${unit.name}${sensor.unitSensorId} (${sensor.name})"
                )
                else {
                    *//*Log.d("blabla","${unit.name}${sensor.unitSensorId} ${sensor.name}  ${sensorRemote.measure} ${sensor.measure}")
                    Log.d("blabla","${sensorRemote.measure != sensor.measure}")*//*
                    if (sensorRemote.measure != sensor.measure) Log.d(
                        "blabla",
                        "У сенсор изменилась размерность -> ${unit.name}${sensor.unitSensorId} (было ${sensorRemote.measure}стало ${sensor.measure})"
                    )
                    if (sensorRemote.deviceType != sensor.deviceType) Log.d(
                        "blabla",
                        "Тип сенсора изменился -> ${unit.name}${sensor.unitSensorId} (было ${sensorRemote.deviceType}стало ${sensor.deviceType})"
                    )
                }
            }
        }

        val allLocalUnit = sensorRemoteDao.getAllUnits()
        allLocalUnit.forEach { localUnit ->
            if (unitsDataApi.find { localUnit.id == it.name } == null) {
                Log.d(
                    "blabla", "Устройство удалено из системы -> ${localUnit.name}"
                )
            }
        }
        val allLocalSensor = sensorRemoteDao.getAllSensors()
        allLocalSensor.forEach { sensorLocal ->
            var isSensorFound = false
            unitsDataApi.forEach { unitApi ->
                if (sensorLocal.unitId == unitApi.name) {
                    for (sensorApi in unitApi.sensors) {
                        if (sensorApi.unitSensorId == sensorLocal.unitSensorId && unitApi.name == sensorLocal.unitId) {
                            isSensorFound = true
                            break
                        }
                    }
                }
            }
            if (!isSensorFound) {
                Log.d(
                    "blabla",
                    "Сенсор удален из системы ->${sensorLocal.unitId} ${sensorLocal.unitSensorId}"
                )
            }
        }*/
    }
}