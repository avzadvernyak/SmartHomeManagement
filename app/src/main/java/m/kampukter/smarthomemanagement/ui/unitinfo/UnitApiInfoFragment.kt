package m.kampukter.smarthomemanagement.ui.unitinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.ResultCompareUnitInfoApi
import m.kampukter.smarthomemanagement.databinding.UnitApiInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitApiInfoFragment: Fragment() {

    private var binding: UnitApiInfoFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var unitCompareListAdapter: UnitCompareListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitApiInfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unitCompareListAdapter = UnitCompareListAdapter()
        binding?.let {
            with(it.unitCompareRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = unitCompareListAdapter
            }
        }
        viewModel.getUnitInfoApi()
        viewModel.compareUnitApiViewLiveData.observe(viewLifecycleOwner) { resultCompare ->
            when (resultCompare) {
                is ResultCompareUnitInfoApi.Success -> {
                    //Log.d("blabla", "unitApiView -> ${resultCompare.sensorValue?.sensors}")
                    resultCompare.sensorValue?.sensors?.let { unitCompareListAdapter.setList(it) }
                }
                is ResultCompareUnitInfoApi.EmptyResponse -> {
                    Log.d("blabla", "ResultCompareUnitInfoApi.EmptyResponse")
                }

                is ResultCompareUnitInfoApi.OtherError -> {
                    Log.d(
                        "blabla",
                        "ResultCompareUnitInfoApi.OtherError ${resultCompare.tError}"
                    )
                }
            }

        }

    }

        companion object {
        private const val ARG_ID_UNIT = "ARG_ID_UNIT"
        fun createInstance(message: String) = UnitApiInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_UNIT, message)

            }
        }
    }
}