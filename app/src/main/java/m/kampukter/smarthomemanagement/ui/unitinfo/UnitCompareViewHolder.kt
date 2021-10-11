package m.kampukter.smarthomemanagement.ui.unitinfo

import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.CompareStatus
import m.kampukter.smarthomemanagement.data.SensorApiView
import m.kampukter.smarthomemanagement.databinding.UnitCompareItemBinding

class UnitCompareViewHolder(private val unitCompareView: UnitCompareItemBinding) :
    RecyclerView.ViewHolder(unitCompareView.root) {
    fun bind(result: SensorApiView) {
        with(unitCompareView) {
            unitNameTextView.text = result.name
            compareStatusNameTextView.text = when(result.compareStatus){
                CompareStatus.OK ->  "Мониторится"
                CompareStatus.NEW ->  "Не мониторится"
                CompareStatus.DELETED ->  "Удален"
                CompareStatus.CHANGE_MEASURE -> "Изменена размерность"
                CompareStatus.CHANGE_TYPE ->  "Изменен тип"
            }

        }
    }

}