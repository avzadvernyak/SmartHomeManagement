package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dao.SensorInfoDao
import m.kampukter.smarthomemanagement.data.dao.UnitInfoDao
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import m.kampukter.smarthomemanagement.data.dto.SensorsDataApiInterface
import retrofit2.Response
import java.io.IOException
import java.net.URL
import java.util.*

class SensorsRepository(
    private val sensorInfoDao: SensorInfoDao,
    private val unitInfoDao: UnitInfoDao,
    private val webSocketDto: DeviceInteractionApi,
    private val sensorApiInterface: SensorsDataApiInterface
) {

    // For test - BEGIN
    private val listUnitInfo = listOf(
        UnitInfo(
            "ESP8266-1", "Name ESP8266-1", "http://192.168.0.82:81/",null
        ), UnitInfo(
            "ESP8266-2", "Name ESP8266-2", "http://192.168.0.83:81/",null
        )
    )
    private val listURL = listUnitInfo.map { URL(it.deviceIp) }
    // For test - END


    private val apiSensorsDataFlow: Flow<List<SensorDataApi>> = flow {
        // For test, а потом получение из таблицы рума
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

}