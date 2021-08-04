package m.kampukter.smarthomemanagement.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.RelayItemBinding
import m.kampukter.smarthomemanagement.databinding.SensorItemBinding

private const val TYPE_SENSOR: Int = 1
private const val TYPE_RELAY: Int = 2

class SensorListAdapter :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var clickSensorEventDelegate: ClickEventDelegate<UnitView>
    lateinit var clickRelayEventDelegate: ClickEventDelegate<UnitView>

    private var sensorList = emptyList<UnitView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =

        when (viewType) {
            TYPE_SENSOR ->
                SensorListViewHolder(
                    SensorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    clickSensorEventDelegate
                )

            else -> RelayListViewHolder(
                RelayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                clickRelayEventDelegate
            )
        }


    override fun getItemCount(): Int = sensorList.size

    override fun getItemViewType(position: Int): Int = when (sensorList[position]) {
        is UnitView.SensorView -> TYPE_SENSOR
        is UnitView.RelayView -> TYPE_RELAY
        else -> TYPE_RELAY
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SensorListViewHolder -> sensorList[position].let { item ->
                holder.bind(item as UnitView.SensorView)
            }
            is RelayListViewHolder -> sensorList[position].let { item ->
                holder.bind(item as UnitView.RelayView)
            }
        }
    }

    fun setList(list: List<UnitView>) {
        this.sensorList = list
        notifyDataSetChanged()
    }
}