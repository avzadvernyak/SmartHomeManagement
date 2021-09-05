package m.kampukter.smarthomemanagement.ui.remotedata

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.DeviceType
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
            sensorRemoteMeasureTextView.text = sensorRemoteItemView.root.context.getString(
                R.string.sensor_measure, result.measure
            )
            when (result.deviceType) {
                DeviceType.RELAY -> {
                    sensorRemoteTypeTextView.text = "Тип: Реле"
                    sensorRemoteMeasureTextView.visibility = View.INVISIBLE
                }
                DeviceType.Device -> {
                    sensorRemoteTypeTextView.text = "Тип: Измеритель"
                    sensorRemoteMeasureTextView.visibility = View.VISIBLE

                }
            }

            if (result.isCandidate) {
                sensorRemoteItemView.root.setOnClickListener { clickUnitEventDelegate.onClick(result) }
                addSensorImageView.visibility = View.VISIBLE
                checkSensorImageView.visibility = View.INVISIBLE
            } else {
                addSensorImageView.visibility = View.INVISIBLE
                checkSensorImageView.visibility = View.VISIBLE
            }


        }

    }
}