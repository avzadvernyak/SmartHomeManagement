package m.kampukter.smarthomemanagement.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sensor_item.view.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView

class SensorListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(result: UnitView) {

        with(itemView) {
            sensorValueTextView.text = (result as UnitView.SensorView).value.toString()
            sensorNameTextView.text = result.name
            imageItemImageView.setImageResource(R.drawable.ic_info_black)
        }
    }
}
