package m.kampukter.smarthomemanagement.data

import com.google.gson.annotations.SerializedName

enum class DeviceType(val value: Int) {
    @SerializedName("Device")
    Device(0),
    @SerializedName("RELAY")
    RELAY(1)
}
