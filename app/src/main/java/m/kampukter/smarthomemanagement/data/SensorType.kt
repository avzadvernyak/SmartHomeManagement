package m.kampukter.smarthomemanagement.data

enum class SensorType (val uri: String, val title: String ) {
    DEFAULT("@drawable/ic_info_black","Прочие"),
    SWITCH("@drawable/ic_switch_24dp","Выключатель"),
    THERMOMETER("@drawable/ic_temperature","Термометр"),
    BAROMETER("@drawable/ic_pressure","Барометр"),
    HYGROMETER("@drawable/ic_humidity","Гигрометр"),
    AMMETER("@drawable/ic_ammeter","Амперметр"),
    VOLTMETER("@drawable/ic_voltmeter","Вольтметр")
}