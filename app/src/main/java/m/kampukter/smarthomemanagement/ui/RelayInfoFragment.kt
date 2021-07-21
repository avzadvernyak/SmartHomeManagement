package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.RelayState
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.RelayInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class RelayInfoFragment : Fragment() {

    private var binding: RelayInfoFragmentBinding? = null

    private val viewModel by sharedViewModel<MainViewModel>()

    private var relayId: String? = null

    private var strDateBegin =
        DateFormat.format("yyyy-MM-dd", Date(Date().time - (1000 * 60 * 60 * 24))).toString()
    private var strDateEnd: String = DateFormat.format("yyyy-MM-dd", Date()).toString()

    private lateinit var pickerRange: MaterialDatePicker<Pair<Long, Long>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = RelayInfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        pickerRange =
            parentFragmentManager.findFragmentByTag("Picker") as? MaterialDatePicker<Pair<Long, Long>>
                ?: MaterialDatePicker.Builder.dateRangePicker().build()


        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.relayInfoToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.relayInfoToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        savedInstanceState?.let { bundle ->
            bundle.getStringArray(KEY_SELECTED_PERIOD)?.let { saveDate ->
                strDateBegin = saveDate[0]
                strDateEnd = saveDate[1]
            }
        }

        arguments?.getString("ARG_ID_RELAY")?.let {
            relayId = it
            viewModel.setIdSensorForSearch(it)
        }
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { relay ->

            (activity as AppCompatActivity).title = relay?.name
            binding?.relaySwitchMaterial?.text = relay?.name
        }
        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            relayId?.let { id ->
                val currentRelay =
                    sensors?.find {
                        (it is UnitView.RelayView) && it.id == id
                    } as UnitView.RelayView
                binding?.relaySwitchMaterial?.isChecked = (currentRelay.state == RelayState.ON)

                binding?.lastUpdateTextView?.text = getString(
                    R.string.last_value_title,
                    DateFormat.format("hh:mm dd-MM-yyyy", currentRelay.lastUpdateDate)
                )
                binding?.relaySwitchMaterial?.setOnCheckedChangeListener{_,_->
                    viewModel.sendCommandToRelay( currentRelay )
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray(KEY_SELECTED_PERIOD, arrayOf(strDateBegin, strDateEnd))
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        relayId?.let {
            viewModel.connectToUnit(it)
        }
    }

    override fun onPause() {
        super.onPause()
        relayId?.let {
            viewModel.disconnectToUnit(it)
        }
    }

    companion object {
        private const val KEY_SELECTED_PERIOD = "KEY_SELECTED_PERIOD"
        private const val ARG_ID_RELAY = "ARG_ID_RELAY"
        fun createInstance(message: String) = RelayInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_RELAY, message)
            }
        }
    }
}