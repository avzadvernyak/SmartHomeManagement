package m.kampukter.smarthomemanagement.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView

private const val TYPE_SENSOR: Int = 1
private const val TYPE_RELAY: Int = 2

class SensorListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var clickSensorEventDelegate: ClickEventDelegate<UnitView>
    lateinit var clickRelayEventDelegate: ClickEventDelegate<UnitView>

    private var sensorList = emptyList<UnitView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_SENSOR -> SensorListViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.sensor_item, parent, false),
                clickSensorEventDelegate
            )
            else -> RelayListViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.relay_item, parent, false),
                clickRelayEventDelegate
            )
        }

    override fun getItemCount(): Int = sensorList.size

    override fun getItemViewType(position: Int): Int = when (sensorList[position]) {
        is UnitView.SensorView -> TYPE_SENSOR
        is UnitView.RelayView -> TYPE_RELAY
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