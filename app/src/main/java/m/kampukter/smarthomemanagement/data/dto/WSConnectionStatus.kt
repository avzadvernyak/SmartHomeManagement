package m.kampukter.smarthomemanagement.data.dto

sealed class WSConnectionStatus {
    object Connecting : WSConnectionStatus()
    object Connected : WSConnectionStatus()
    object Closing : WSConnectionStatus()
    object Disconnected : WSConnectionStatus()
    data class Failed(val reason: String?) : WSConnectionStatus()
}
