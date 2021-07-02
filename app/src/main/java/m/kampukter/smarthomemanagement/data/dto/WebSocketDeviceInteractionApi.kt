package m.kampukter.smarthomemanagement.data.dto

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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

    private val webSocketListener = object : WebSocketListener() {

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)

            webSockets.remove(webSocket.getUrl())

        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)

            webSockets.remove(webSocket.getUrl())
            Log.w("blabla", "Failure from ${webSocket.getUrl()}")
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

            } else {
                Log.i("blabla", "Get Connected string from ${webSocket.getUrl()}")
            }

        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.w("blabla", "onOpen from ${webSocket.getUrl()}")
        }
    }


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
}