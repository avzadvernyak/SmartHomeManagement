package m.kampukter.smarthomemanagement.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitInfoView

class UnitListAdapter :
    RecyclerView.Adapter<UnitListViewHolder>() {

    private var unitList = emptyList<UnitInfoView>()
    lateinit var clickUnitEventDelegate: ClickEventDelegate<UnitInfoView>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitListViewHolder =
        UnitListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.unit_item, parent, false),
            clickUnitEventDelegate
        )

    override fun getItemCount(): Int = unitList.size

    override fun onBindViewHolder(holder: UnitListViewHolder, position: Int) {

        holder.bind(unitList[position])
    }

    fun setList(list: List<UnitInfoView>) {
        this.unitList = list
        notifyDataSetChanged()
    }
}


