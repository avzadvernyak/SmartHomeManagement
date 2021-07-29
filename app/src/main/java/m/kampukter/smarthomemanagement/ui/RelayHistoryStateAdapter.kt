package m.kampukter.smarthomemanagement.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorDataApi
import m.kampukter.smarthomemanagement.databinding.RelayHistoryStateItemBinding

class RelayHistoryStateAdapter: RecyclerView.Adapter<RelayHistoryStateViewHolder>() {

    private var relayState: List<SensorDataApi>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelayHistoryStateViewHolder {
        return RelayHistoryStateViewHolder(
            RelayHistoryStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = relayState?.size ?: 0

    override fun onBindViewHolder(holder: RelayHistoryStateViewHolder, position: Int) {
        relayState?.get(position)?.let { item ->
            holder.bind(item)
        }
    }

    fun setList(list: List<SensorDataApi>) {
        this.relayState = list
        notifyDataSetChanged()
    }

}
