package m.kampukter.smarthomemanagement.ui.unitinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorApiView
import m.kampukter.smarthomemanagement.databinding.UnitCompareItemBinding

class UnitCompareListAdapter: RecyclerView.Adapter<UnitCompareViewHolder>() {

    private var sensorList: List<SensorApiView>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitCompareViewHolder {
        return UnitCompareViewHolder(
            UnitCompareItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = sensorList?.size ?: 0

    override fun onBindViewHolder(holder: UnitCompareViewHolder, position: Int) {
        sensorList?.get(position)?.let { item ->
            holder.bind(item)
        }
    }

    fun setList(list: List<SensorApiView>) {
        this.sensorList = list
        notifyDataSetChanged()
    }

}
