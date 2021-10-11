package m.kampukter.smarthomemanagement.ui.unitinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultCompareUnitInfoApi
import m.kampukter.smarthomemanagement.databinding.UnitInfoApiFragmentBinding
import m.kampukter.smarthomemanagement.databinding.UnitInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitInfoApiFragment: Fragment() {

    private var binding: UnitInfoApiFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var unitCompareListAdapter: UnitCompareListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitInfoApiFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.unitInfoApiToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_unit)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.unitInfoApiToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

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
                    Log.d("blabla", "unitApiView -> ${resultCompare.sensorValue?.sensors}")
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
        fun createInstance(message: String) = UnitInfoApiFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_UNIT, message)

            }
        }
    }
}