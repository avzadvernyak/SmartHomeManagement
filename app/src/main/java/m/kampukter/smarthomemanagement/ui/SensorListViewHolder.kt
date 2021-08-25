package m.kampukter.smarthomemanagement.ui

import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.data.SensorView
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.SensorItemBinding

class SensorListViewHolder(
    private val sensorItemView: SensorItemBinding,
    private val clickEventDelegate: ClickEventDelegate<UnitView>
) : RecyclerView.ViewHolder(sensorItemView.root) {
    fun bind(result: SensorView) {

        with(sensorItemView) {
            sensorValueTextView.text = result.value.toString()
            sensorDimensionTextView.text = result.dimension
            sensorNameTextView.text = result.name
            lastDateTextView.text = sensorItemView.root.context.getString(
                    R.string.last_update,
                    DateFormat.format("dd/MM/yyyy HH:mm", result.lastUpdateDate)
                )

            when (result.icon) {
                SensorType.THERMOMETER -> imageItemImageView.setImageResource(R.drawable.ic_temperature)
                //2 -> imageItemImageView.setImageResource(R.drawable.ic_pressure)
                //3 -> imageItemImageView.setImageResource(R.drawable.ic_humidity)
                else -> imageItemImageView.setImageResource(R.drawable.ic_info_black)
            }
            sensorItemView.root.setOnClickListener {
                clickEventDelegate.onClick(result)
            }
            sensorItemView.root.setOnLongClickListener {
                clickEventDelegate.onLongClick(result)
                true
            }

        }
    }
}
