package m.kampukter.smarthomemanagement.ui.remotedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.databinding.SensorTypeItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class SensorTypesAdapter  :
    RecyclerView.Adapter<SensorImageListViewHolder>() {

    private var types = SensorType.values()
    lateinit var clickImageEventDelegate: ClickEventDelegate<SensorType>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorImageListViewHolder =
        SensorImageListViewHolder(
            SensorTypeItemBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            ),
            clickImageEventDelegate
        )

    override fun getItemCount(): Int = types.size

    override fun onBindViewHolder(holder: SensorImageListViewHolder, position: Int) {
        holder.bind(types[position])
    }
}
