package m.kampukter.smarthomemanagement.ui

import android.text.format.DateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sensor_item.view.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView

class SensorListViewHolder(
    itemView: View,
    private val clickEventDelegate: ClickEventDelegate<UnitView>
) : RecyclerView.ViewHolder(itemView) {
    fun bind(result: UnitView.SensorView) {

        with(itemView) {
            val valueString = "${result.value} ${result.dimension}"
            sensorValueTextView.text = valueString
            sensorNameTextView.text = result.name
            lastDateTextView.text =
                context.getString(
                    R.string.last_update,
                    DateFormat.format("dd/MM/yyyy HH:mm", result.lastUpdateDate)
                )
            when (result.icon) {
                1 -> imageItemImageView.setImageResource(R.drawable.ic_temperature)
                2 -> imageItemImageView.setImageResource(R.drawable.ic_pressure)
                3 -> imageItemImageView.setImageResource(R.drawable.ic_humidity)
                else -> imageItemImageView.setImageResource(R.drawable.ic_info_black)
            }
            setOnClickListener {
                clickEventDelegate.onClick(result)
            }

        }
    }
}
