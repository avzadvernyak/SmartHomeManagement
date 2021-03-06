package m.kampukter.smarthomemanagement.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorDataApi
import m.kampukter.smarthomemanagement.databinding.SensorDataHistoryItemBinding

class SensorDataHistoryAdapter : RecyclerView.Adapter<SensorDataHistoryViewHolder>() {

    private var sensorValue: List<SensorDataApi>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorDataHistoryViewHolder {
        return SensorDataHistoryViewHolder(
            SensorDataHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = sensorValue?.size ?: 0

    override fun onBindViewHolder(holder: SensorDataHistoryViewHolder, position: Int) {
        sensorValue?.get(position)?.let { item ->
            holder.bind(item)
        }
    }

    fun setList(list: List<SensorDataApi>) {
        this.sensorValue = list
        notifyDataSetChanged()
    }

}
