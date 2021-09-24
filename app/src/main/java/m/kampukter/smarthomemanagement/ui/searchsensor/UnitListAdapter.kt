package m.kampukter.smarthomemanagement.ui.searchsensor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.databinding.UnitItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class UnitListAdapter :
    RecyclerView.Adapter<UnitListViewHolder>() {

    private var unitList = emptyList<UnitInfo>()
    lateinit var clickUnitEventDelegate: ClickEventDelegate<UnitInfo>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitListViewHolder =
        UnitListViewHolder(
            UnitItemBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            ),
            clickUnitEventDelegate
        )

    override fun getItemCount(): Int = unitList.size

    override fun onBindViewHolder(holder: UnitListViewHolder, position: Int) {

        holder.bind(unitList[position])
    }

    fun setList(list: List<UnitInfo>) {
        this.unitList = list
        notifyDataSetChanged()
    }
}