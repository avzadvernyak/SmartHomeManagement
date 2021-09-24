package m.kampukter.smarthomemanagement.ui.searchsensor

import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.databinding.UnitItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class UnitListViewHolder (
    private val unitItemView: UnitItemBinding,
    private val clickUnitEventDelegate: ClickEventDelegate<UnitInfo>
) :
    RecyclerView.ViewHolder(unitItemView.root) {
    fun bind(result: UnitInfo) {

        with(unitItemView) {
            unitNameTextView.text = result.name
            unitDescriptionTextView.text = result.description
            unitItemView.root.setOnClickListener {
                clickUnitEventDelegate.onClick(result)
            }
        }

    }
}