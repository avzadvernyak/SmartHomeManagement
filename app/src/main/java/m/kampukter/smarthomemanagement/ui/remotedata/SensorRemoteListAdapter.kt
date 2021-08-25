package m.kampukter.smarthomemanagement.ui.remotedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorInfoRemote
import m.kampukter.smarthomemanagement.databinding.SensorRemoteItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class SensorRemoteListAdapter  :
    RecyclerView.Adapter<SensorRemoteListViewHolder>() {

    private var sensorRemoteList = emptyList<SensorInfoRemote>()
    lateinit var clickSensorEventDelegate: ClickEventDelegate<SensorInfoRemote>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorRemoteListViewHolder =
        SensorRemoteListViewHolder(
            SensorRemoteItemBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            ),
            clickSensorEventDelegate
        )

    override fun getItemCount(): Int = sensorRemoteList.size

    override fun onBindViewHolder(holder: SensorRemoteListViewHolder, position: Int) {

        holder.bind(sensorRemoteList[position])
    }

    fun setList(list: List<SensorInfoRemote>) {
        this.sensorRemoteList = list
        notifyDataSetChanged()
    }
}