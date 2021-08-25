package m.kampukter.smarthomemanagement.ui.remotedata

import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.UnitInfoRemote
import m.kampukter.smarthomemanagement.databinding.UnitRemoteItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class UnitRemoteListViewHolder(
    private val unitRemoteItemView: UnitRemoteItemBinding,
    private val clickUnitEventDelegate: ClickEventDelegate<UnitInfoRemote>
) :
    RecyclerView.ViewHolder(unitRemoteItemView.root) {
    fun bind(result: UnitInfoRemote) {

        with(unitRemoteItemView) {
            unitRemoteNameTextView.text = result.name
            unitRemoteDescriptionTextView.text = result.description
            unitRemoteItemView.root.setOnClickListener {
                clickUnitEventDelegate.onClick(result)
            }
        }

    }
}