package m.kampukter.smarthomemanagement.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.relay_item.view.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView

class RelayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(result: UnitView) {

        with(itemView) {
            relayNameTextView.text = (result as UnitView.RelayView).name
            imageItemImageView.setImageResource(R.drawable.ic_switch_24dp)
        }
    }
}
