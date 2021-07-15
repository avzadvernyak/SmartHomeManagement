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
            unitIdTextView.text = result.name
            unitDescriptionTextView.text = result.description
            linkOnImageView.visibility = View.INVISIBLE
            linkOffImageView.visibility = View.INVISIBLE
            linkErrorImageView.visibility = View.INVISIBLE
            linkProgressBar.visibility = View.INVISIBLE
            when (result.wsConnectionStatus) {
                is WSConnectionStatus.Connecting -> linkProgressBar.visibility = View.VISIBLE
                is WSConnectionStatus.Failed -> linkErrorImageView.visibility = View.VISIBLE
                is WSConnectionStatus.Connected -> linkOnImageView.visibility = View.VISIBLE
                is WSConnectionStatus.Disconnected -> linkOffImageView.visibility = View.VISIBLE
                else -> linkProgressBar.visibility = View.VISIBLE
            }
            setOnClickListener {
                clickUnitEventDelegate.onClick(result)
            }
        }

    }
}
