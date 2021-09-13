package m.kampukter.smarthomemanagement.ui

import android.text.format.DateFormat
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorDataApi
import m.kampukter.smarthomemanagement.databinding.RelayHistoryStateItemBinding

class RelayHistoryStateViewHolder (private val relayStateView: RelayHistoryStateItemBinding) :
    RecyclerView.ViewHolder(relayStateView.root) {
    fun bind(result: SensorDataApi) {
        with(relayStateView) {
            dateTimeTextView.text = DateFormat.format("dd/MM/yyyy HH:mm", result.date * 1000L)
            stateTextView.text = if (result.value == 0F ) "Off" else "On"
        }
    }

}
