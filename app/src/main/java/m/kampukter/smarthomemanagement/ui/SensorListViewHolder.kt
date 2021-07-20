package m.kampukter.smarthomemanagement.ui

import android.text.format.DateFormat
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.SensorItemBinding

class SensorListViewHolder(
    private val myItemView: SensorItemBinding,
    private val clickEventDelegate: ClickEventDelegate<UnitView>
) : RecyclerView.ViewHolder(myItemView.root) {
    fun bind(result: UnitView.SensorView) {

        with(myItemView) {
            val valueString = "${result.value} ${result.dimension}"
            sensorValueTextView.text = valueString
            sensorNameTextView.text = result.name
            lastDateTextView.text = myItemView.root.context.getString(
                    R.string.last_update,
                    DateFormat.format("dd/MM/yyyy HH:mm", result.lastUpdateDate)
                )
            when (result.icon) {
                1 -> imageItemImageView.setImageResource(R.drawable.ic_temperature)
                2 -> imageItemImageView.setImageResource(R.drawable.ic_pressure)
                3 -> imageItemImageView.setImageResource(R.drawable.ic_humidity)
                else -> imageItemImageView.setImageResource(R.drawable.ic_info_black)
            }
            myItemView.root.setOnClickListener {
                clickEventDelegate.onClick(result)
            }
            myItemView.root.setOnLongClickListener {
                clickEventDelegate.onLongClick(result)
                true
            }

        }
    }
}
