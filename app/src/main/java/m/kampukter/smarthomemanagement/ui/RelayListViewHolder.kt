package m.kampukter.smarthomemanagement.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.RelayState
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.RelayItemBinding

class RelayListViewHolder(
    private val relayItemView: RelayItemBinding,
    private val clickEventDelegate: ClickEventDelegate<UnitView>
) : RecyclerView.ViewHolder(relayItemView.root) {
    fun bind(result: UnitView.RelayView) {

        with(relayItemView) {
            relayNameTextView.text = result.name
            imageItemImageView.setImageResource(R.drawable.ic_switch_24dp)

            lightingOnImageBottom.visibility = View.INVISIBLE
            lightingOffImageBottom.visibility = View.INVISIBLE
            relayProgressBar.visibility = View.INVISIBLE

            when (result.state) {
                RelayState.ON -> {
                    lightingOnImageBottom.visibility = View.VISIBLE
                    relayItemView.root.setOnClickListener {
                        clickEventDelegate.onClick(result)
                    }
                }
                RelayState.OFF -> {
                    lightingOffImageBottom.visibility = View.VISIBLE
                    relayItemView.root.setOnClickListener {
                        clickEventDelegate.onClick(result)
                    }
                }
                else -> {
                    relayProgressBar.visibility = View.VISIBLE
                    relayItemView.root.setOnClickListener(null)
                }
            }

        }
    }
}
