package m.kampukter.smarthomemanagement.ui

import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.databinding.UnitItemBinding

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