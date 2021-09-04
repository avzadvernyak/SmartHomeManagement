package m.kampukter.smarthomemanagement.ui.remotedata

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorInfoRemote
import m.kampukter.smarthomemanagement.databinding.SensorRemoteItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class SensorRemoteListViewHolder(
    private val sensorRemoteItemView: SensorRemoteItemBinding,
    private val clickUnitEventDelegate: ClickEventDelegate<SensorInfoRemote>
) :
    RecyclerView.ViewHolder(sensorRemoteItemView.root) {
    fun bind(result: SensorInfoRemote) {

        with(sensorRemoteItemView) {
            sensorRemoteNameTextView.text = result.name
            sensorRemoteDescriptionTextView.text = result.measure
            if (result.isCandidate) {
                sensorRemoteItemView.root.setOnClickListener {
                    clickUnitEventDelegate.onClick(result)
                }
                addSensorImageView.visibility = View.VISIBLE
            } else addSensorImageView.visibility = View.INVISIBLE
        }

    }
}