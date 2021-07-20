package m.kampukter.smarthomemanagement.ui

import android.text.format.DateFormat
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorDataApi
import m.kampukter.smarthomemanagement.databinding.SensorDataHistoryItemBinding

class SensorDataHistoryViewHolder(private val sensorDataView: SensorDataHistoryItemBinding) :
    RecyclerView.ViewHolder(sensorDataView.root) {
    fun bind(result: SensorDataApi) {
        with(sensorDataView) {
            dateTimeTextView.text = DateFormat.format("dd/MM/yyyy HH:mm", result.date * 1000L)
            valueTextView.text = result.value.toString()
        }
    }

}
