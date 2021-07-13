package m.kampukter.smarthomemanagement.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.unit_item.view.*
import m.kampukter.smarthomemanagement.data.UnitInfoView
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus

class UnitListViewHolder(
    itemView: View,
    private val clickUnitEventDelegate: ClickEventDelegate<UnitInfoView>
) :
    RecyclerView.ViewHolder(itemView) {
    fun bind(result: UnitInfoView) {

        with(itemView) {
            unitNameTextView.text = result.deviceName
            unitDescriptionTextView.text = result.deviceDescription
            linkOnImageView.visibility = View.INVISIBLE
            linkOffImageView.visibility = View.INVISIBLE
            when (result.wsConnectionStatus) {
                is WSConnectionStatus.Connected -> linkOnImageView.visibility = View.VISIBLE
                else -> linkOffImageView.visibility = View.VISIBLE
            }
            setOnClickListener {
                clickUnitEventDelegate.onClick(result)
            }
        }

    }
}
