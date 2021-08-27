package m.kampukter.smarthomemanagement.data

enum class SensorType (val uri: String) {
    DEFAULT("@drawable/ic_info_black"),
    SWITCH("@drawable/ic_switch_24dp"),
    THERMOMETER("@drawable/ic_temperature"),
    BAROMETER("@drawable/ic_pressure"),
    HYGROMETER("@drawable/ic_humidity"),
    AMMETER("@drawable/ic_ammeter"),
    VOLTMETER("@drawable/ic_voltmeter")
}