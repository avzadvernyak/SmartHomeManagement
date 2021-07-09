package m.kampukter.smarthomemanagement.ui

import android.text.format.DateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sensor_data_history_item.view.*
import m.kampukter.smarthomemanagement.data.SensorDataApi

class SensorDataHistoryViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(result: SensorDataApi) {
        with(itemView) {
            dateTimeTextView.text = DateFormat.format("dd/MM/yyyy HH:mm", result.date * 1000L)
            valueTextView.text = result.value.toString()
        }
    }

}
