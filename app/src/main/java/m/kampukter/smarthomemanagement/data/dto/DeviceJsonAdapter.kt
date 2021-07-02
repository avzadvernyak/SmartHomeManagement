package m.kampukter.smarthomemanagement.data.dto

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import m.kampukter.smarthomemanagement.data.SensorData
import m.kampukter.smarthomemanagement.data.UnitData
import java.util.*

class DeviceJsonAdapter : TypeAdapter<UnitData>() {
    override fun read(reader: JsonReader): UnitData {
        var fieldName: String? = null
        var deviceIdValue = ""
        val receivedData = mutableListOf<SensorData>()
        reader.beginObject()
        while (reader.hasNext()) {
            if (JsonToken.NAME == reader.peek()) {
                fieldName = reader.nextName()
            }
            if (fieldName == "deviceId") {
                deviceIdValue = reader.nextString()
            }
            if (fieldName == "sensorsData") {
                reader.beginArray()
                while (reader.hasNext()) {

                    reader.beginObject()
                    var idValue = ""
                    var myField = ""
                    var myFieldValue = 0.0
                    var myStateFieldValue = false

                    while (reader.hasNext()) {
                        when (val name = reader.nextName()) {
                            "id" -> idValue = reader.nextString()
                            "state" -> {
                                myField = name
                                myStateFieldValue = reader.nextBoolean()
                            }
                            else -> {
                                myField = name
                                myFieldValue = reader.nextDouble()
                            }
                        }
                    }
                    when (myField) {

                        "state" -> receivedData.add(
                            SensorData.Relay(
                                deviceIdValue,
                                idValue,
                                myStateFieldValue,
                                Calendar.getInstance().time
                            )
                        )
                        else -> receivedData.add(
                            SensorData.Sensor(
                                deviceIdValue,
                                idValue,
                                myFieldValue.toFloat(),
                                Calendar.getInstance().time
                            )
                        )
                    }
                    reader.endObject()
                }
                reader.endArray()
            }
        }
        reader.endObject()
        return UnitData( receivedData )
    }

    override fun write(out: JsonWriter?, value: UnitData?) {
    }

}