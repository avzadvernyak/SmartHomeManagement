package m.kampukter.smarthomemanagement.data

enum class SensorType (val url: String) {
    DEFAULT("@drawable/ic_info_black"),
    SWITCH("@drawable/ic_switch24dp"),
    THERMOMETER("@drawable/ic_temperature"),
    HYGROMETER("@drawable/ic_pressure"),
    BAROMETER("@drawable/ic_humidity"),
    AMMETER("@drawable/ic_ammeter"),
    VOLTMETER("@drawable/voltmeter")
}