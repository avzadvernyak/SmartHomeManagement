package m.kampukter.smarthomemanagement.data.dto

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.UnitData
import okhttp3.*
import java.net.URL
import java.util.concurrent.TimeUnit

class WebSocketDeviceInteractionApi : DeviceInteractionApi {

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

            CoroutineScope(Dispatchers.IO + coroutineContext).launch {
                connectionStatusFlow.emit(Pair(webSocket.getUrl(), WSConnectionStatus.Disconnected))
            }
            webSockets.remove(webSocket.getUrl())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            CoroutineScope(Dispatchers.IO + coroutineContext).launch {
                connectionStatusFlow.emit(Pair(webSocket.getUrl(), WSConnectionStatus.Disconnected))
            }
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

            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.w("blabla", "onOpen from ${webSocket.getUrl()}")
            CoroutineScope(Dispatchers.IO + coroutineContext).launch {
                connectionStatusFlow.emit(Pair(webSocket.getUrl(), WSConnectionStatus.Connected))
            }
        }
    }


    @DelicateCoroutinesApi
    override fun connect(url: URL) {
        if (!webSockets.containsKey(url)) {
            webSockets[url] = okHttpClient.newWebSocket(
                Request.Builder().url(url).build(),
                webSocketListener
            )
        }
    }

    override fun disconnect(url: URL) {
        webSockets[url]?.close(1000, null)
        webSockets.remove(url)
    }

    fun WebSocket.getUrl(): URL = request().url().url()
    override fun getUnitDataFlow(): MutableStateFlow<UnitData?> = unitDataFlow
    override fun getWSStatusFlow(): MutableStateFlow<Pair<URL, WSConnectionStatus>?> = connectionStatusFlow
    override fun commandSend(url: URL, command: String) {
        webSockets[url]?.send(command)
    }
}