package m.kampukter.smarthomemanagement.ui.remotedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.UnitInfoRemote
import m.kampukter.smarthomemanagement.databinding.UnitRemoteItemBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate

class UnitRemoteListAdapter :
    RecyclerView.Adapter<UnitRemoteListViewHolder>() {

    private var unitRemoteList = emptyList<UnitInfoRemote>()
    lateinit var clickUnitEventDelegate: ClickEventDelegate<UnitInfoRemote>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitRemoteListViewHolder =
        UnitRemoteListViewHolder(
            UnitRemoteItemBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            ),
            clickUnitEventDelegate
        )

    override fun getItemCount(): Int = unitRemoteList.size

    override fun onBindViewHolder(holder: UnitRemoteListViewHolder, position: Int) {

        holder.bind(unitRemoteList[position])
    }

    fun setList(list: List<UnitInfoRemote>) {
        this.unitRemoteList = list
        notifyDataSetChanged()
    }
}