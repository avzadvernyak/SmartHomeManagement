package m.kampukter.smarthomemanagement.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.relay_item.view.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.RelayState
import m.kampukter.smarthomemanagement.data.UnitView

class RelayListViewHolder(
    itemView: View,
    private val clickEventDelegate: ClickEventDelegate<UnitView>
) : RecyclerView.ViewHolder(itemView) {
    fun bind(result: UnitView.RelayView) {

        with(itemView) {
            relayNameTextView.text = result.name
            imageItemImageView.setImageResource(R.drawable.ic_switch_24dp)

            lightingOnImageBottom.visibility = View.INVISIBLE
            lightingOffImageBottom.visibility = View.INVISIBLE
            relayProgressBar.visibility = View.INVISIBLE

            when (result.status) {
                RelayState.ON -> {
                    lightingOnImageBottom.visibility = View.VISIBLE
                    setOnClickListener {
                        clickEventDelegate.onClick(result)
                    }
                }
                RelayState.OFF -> {
                    lightingOffImageBottom.visibility = View.VISIBLE
                    setOnClickListener {
                        clickEventDelegate.onClick(result)
                    }
                }
                else -> {
                    relayProgressBar?.visibility = View.VISIBLE
                    setOnClickListener(null)
                }
            }

        }
    }
}
