package m.kampukter.smarthomemanagement.ui.remotedata

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.databinding.SensorTypeItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class SensorImageListViewHolder  (
    private val sensorImageItemView: SensorTypeItemBinding,
    private val clickUnitEventDelegate: ClickEventDelegate<SensorType>
) :
    RecyclerView.ViewHolder(sensorImageItemView.root) {
    fun bind(result: SensorType) {

        with(sensorImageItemView) {
            val imageResource =
                root.context.resources.getIdentifier(result.uri, null, root.context.packageName)
            ResourcesCompat.getDrawable(root.context.resources, imageResource, null)?.let {
                sensorImageView.setImageDrawable(it)
            }
            sensorTypeTitleTextView.text = result.title
            sensorImageItemView.root.setOnClickListener {
                clickUnitEventDelegate.onClick(result)
            }
        }
    }
}