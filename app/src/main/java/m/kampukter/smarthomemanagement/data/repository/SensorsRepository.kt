package m.kampukter.smarthomemanagement.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import m.kampukter.smarthomemanagement.data.SensorData
import m.kampukter.smarthomemanagement.data.SensorInfo
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import java.net.URL
import java.util.*

class SensorsRepository(private val webSocketDto: DeviceInteractionApi) {
    private val list = listOf(
        SensorInfo(
            "1", "ESP8266-2", "1", "Thermometer", "C",
            URL("http://192.168.0.82:81/"),
            URL("http://109.254.66.131:81/"),
            SensorType.SENSOR
        )
    )
    private val sensorList = listOf(
        UnitView.SensorView(
            "1",
            "Температура на улице",
            26.0F,
            Calendar.getInstance().time,
            false
        ),
        UnitView.SensorView(
            "2",
            "Температура в тамбуре",
            27.0F,
            Calendar.getInstance().time,
            false
        ),
        UnitView.SensorView(
            "3",
            "Температура в гостинной",
            25.0F,
            Calendar.getInstance().time,
            false
        ), UnitView.RelayView(
            "4",
            "Switch 1",
            false,
            Calendar.getInstance().time,
            true
        )

    )
    private val sensorInfoListFlow: Flow<List<SensorInfo>> = flow {
        emit(list)
    }

    private val listURL = listOf(URL("http://192.168.0.82:81/"), URL("http://192.168.0.83:81/"))
    private val sensorURLList: Flow<List<URL>> = flow {
        emit(listURL)
    }
    private val unitDataFlow = webSocketDto.getUnitDataFlow()
    val sensorListFlow: Flow<List<UnitView>> =
        combine(unitDataFlow, sensorInfoListFlow) { unitData, sensorInfoList ->
            val resultSensorList = mutableListOf<UnitView>()

            sensorInfoList.forEach { sensor ->
                Log.w("blabla", "sensor ${sensor.type}")
                val findItem = unitData?.sensorDataList?.find {
                    when (it) {
                        is SensorData.Sensor -> {
                            it.deviceId == sensor.deviceId && it.deviceSensorId == sensor.deviceSensorId
                        }
                        is SensorData.Relay -> {
                            it.deviceId == sensor.deviceId && it.deviceRelayId == sensor.deviceSensorId
                        }
                    }
                }
                Log.w("blabla", "findItem $findItem")
                if (findItem != null) {
                    when (findItem) {
                        is SensorData.Sensor -> resultSensorList.add(
                            UnitView.SensorView(
                                sensor.id,
                                sensor.name,
                                findItem.value,
                                Calendar.getInstance().time,
                                true
                            )
                        )

                        is SensorData.Relay -> resultSensorList.add(
                            UnitView.RelayView(
                                sensor.id,
                                sensor.name,
                                findItem.status,
                                Calendar.getInstance().time,
                                true
                            )
                        )
                    }
                } else {
                    Log.w("blabla", "sensor ${sensor.type}")
                    when (sensor.type) {
                        SensorType.SENSOR -> resultSensorList.add(
                            UnitView.SensorView(
                                sensor.id,
                                sensor.name,
                                0F,
                                Calendar.getInstance().time,
                                false
                            )
                        )
                        SensorType.RELAY -> resultSensorList.add(
                            UnitView.RelayView(
                                sensor.id,
                                sensor.name,
                                false,
                                Calendar.getInstance().time,
                                false
                            )
                        )
                    }
                }
            }
            resultSensorList
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
}