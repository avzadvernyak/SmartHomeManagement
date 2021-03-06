package m.kampukter.smarthomemanagement.data.dto

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import m.kampukter.smarthomemanagement.data.RelayState
import m.kampukter.smarthomemanagement.data.SensorData
import m.kampukter.smarthomemanagement.data.SensorInfoWithIp
import m.kampukter.smarthomemanagement.data.UnitData
import okhttp3.*
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class WebSocketDeviceInteractionApi : DeviceInteractionApi {

    private var isDisconnect = mutableMapOf<URL, Boolean>()

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private var webSockets = mutableMapOf<URL, WebSocket>()
    private val deviceJsonAdapter = DeviceJsonAdapter()

    private val unitDataFlow: MutableStateFlow<UnitData?> = MutableStateFlow(null)
    private val connectionStatusFlow: MutableStateFlow<Pair<URL, WSConnectionStatus>?> =
        MutableStateFlow(null)

    @DelicateCoroutinesApi
    private val webSocketListener = object : WebSocketListener() {

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)

            emitStatus(Pair(webSocket.getUrl(), WSConnectionStatus.Closing))
            webSockets.remove(webSocket.getUrl())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.w("blabla", "onClosed from ${webSocket.getUrl()}")
            emitStatus(Pair(webSocket.getUrl(), WSConnectionStatus.Disconnected))
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)

            emitStatus(Pair(webSocket.getUrl(), WSConnectionStatus.Failed(t.toString())))
            webSockets.remove(webSocket.getUrl())
            Log.w("blabla", "Failure from ${webSocket.getUrl()}>$t")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {

            if (text != "Connected") {
                val unitInfo =
                    GsonBuilder().registerTypeAdapter(UnitData::class.java, deviceJsonAdapter)
                        .create()
                        .fromJson(text, UnitData::class.java)

                CoroutineScope(Dispatchers.IO + coroutineContext).launch {
                    unitDataFlow.emit(unitInfo)
                }
                emitStatus(Pair(webSocket.getUrl(), WSConnectionStatus.Connected))

            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.w("blabla", "onOpen from ${webSocket.getUrl()}")
            emitStatus(Pair(webSocket.getUrl(), WSConnectionStatus.Connected))
        }
    }


    @DelicateCoroutinesApi
    override fun connect(urlUnit: String) {
        val url = URL(urlUnit)
        isDisconnect[url] = false
        if (!webSockets.containsKey(url)) {
            emitStatus(Pair(url, WSConnectionStatus.Connecting))
            webSockets[url] = okHttpClient.newWebSocket(
                Request.Builder().url(url).build(),
                webSocketListener
            )
        }
    }

    @DelicateCoroutinesApi
    override fun disconnect(urlUnit: String) {
        val url = URL(urlUnit)
        isDisconnect[url] = true
        GlobalScope.launch(context = Dispatchers.IO) {
            delay(10000)
            isDisconnect[url]?.let {
                if (it) {
                    webSockets[url]?.close(1000, null)
                    webSockets.remove(url)
                    emitStatus(Pair(url, WSConnectionStatus.Disconnected))
                }
            }
        }
    }

    fun WebSocket.getUrl(): URL = request().url().url()
    override fun getUnitDataFlow(): MutableStateFlow<UnitData?> = unitDataFlow
    override fun getWSStatusFlow(): MutableStateFlow<Pair<URL, WSConnectionStatus>?> =
        connectionStatusFlow

    override suspend fun commandSend(sensorInfo: SensorInfoWithIp) {
        val url = URL(sensorInfo.unitIp)
        webSockets[url]

        if (webSockets.containsKey(url)) {
            val relay = UnitData(
                sensorDataList = listOf(
                    SensorData.Relay(
                        deviceId = sensorInfo.unitId,
                        deviceRelayId = sensorInfo.unitSensorId,
                        status = RelayState.OFFLINE,
                        Calendar.getInstance().time
                    )
                )
            )
            unitDataFlow.emit(relay)
            sendToWs(sensorInfo)
        }

    }

    private fun sendToWs(sensorInfo: SensorInfoWithIp) {
        webSockets[URL(sensorInfo.unitIp)]?.send("${sensorInfo.unitId}${sensorInfo.unitSensorId}")
    }

    @DelicateCoroutinesApi
    private fun emitStatus(status: Pair<URL, WSConnectionStatus>?) {
        CoroutineScope(Dispatchers.IO + coroutineContext).launch {
            connectionStatusFlow.emit(status)
        }
    }
}