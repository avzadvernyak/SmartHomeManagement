package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.RelayState
import m.kampukter.smarthomemanagement.data.RelayView
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
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

    private lateinit var listRelayStateAdapter: RelayHistoryStateAdapter

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

        listRelayStateAdapter = RelayHistoryStateAdapter()
        binding?.let {
            with(it.relayHistoryRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = listRelayStateAdapter
            }
        }

        visibilityRelayState(RelayState.OFFLINE)
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { relay ->
            (activity as AppCompatActivity).title = relay?.name
            relay?.unitId?.let { viewModel.setIdUnitForSearch(it) }
            val relayFullId = "${relay?.unitId}${relay?.unitSensorId}"
            viewModel.setQuestionSensorsData(Triple(relayFullId, strDateBegin, strDateEnd))
        }

        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            visibilityRelayState(RelayState.OFFLINE)
            relayId?.let { id ->
                val currentRelay =
                    sensors?.find { it.id == id } as RelayView


                binding?.lastUpdateTextView?.text = getString(
                    R.string.last_value_title,
                    DateFormat.format("HH:mm dd-MM-yyyy", currentRelay.lastUpdateDate)
                )
                visibilityRelayState(currentRelay.state)
            }
        }

        viewModel.resultSensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->

            var begTime = DateFormat.format(
                getString(R.string.formatDT),
                Date().time - (1000 * 60 * 60 * 24)
            )
            var endTime = DateFormat.format(
                getString(R.string.formatDT),
                Date().time
            )

            if (resultSensorData is ResultSensorDataApi.Success) {
                val value = resultSensorData.sensorValue
                listRelayStateAdapter.setList(value.sortedByDescending { it.date })

                begTime =
                    DateFormat.format(
                        getString(R.string.formatDT),
                        resultSensorData.sensorValue.first().date * 1000L
                    )
                endTime =
                    DateFormat.format(
                        getString(R.string.formatDT),
                        resultSensorData.sensorValue.last().date * 1000L
                    )
            } else if (resultSensorData is ResultSensorDataApi.EmptyResponse) {
                listRelayStateAdapter.setList(emptyList())

                Snackbar.make(
                    view,
                    getString(R.string.noDataMessage, strDateBegin, strDateEnd),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            binding?.intervalTextView?.text =
                getString(R.string.dateInfoView, begTime.toString(), endTime.toString())

        }

        viewModel.unitLiveData.observe(viewLifecycleOwner) {
            if (it?.wsConnectionStatus is WSConnectionStatus.Connected) {
                binding?.lightingOnImageBottom?.setOnClickListener {
                    relayId?.let { id -> viewModel.sendCommandToRelay(id) }
                    visibilityRelayState(RelayState.OFFLINE)
                }
                binding?.lightingOffImageBottom?.setOnClickListener {
                    relayId?.let { id -> viewModel.sendCommandToRelay(id) }
                    visibilityRelayState(RelayState.OFFLINE)
                }
                binding?.relayStateTextView?.text = getString(R.string.relay_online)

            } else {
                binding?.lightingOnImageBottom?.setOnClickListener(null)
                binding?.lightingOffImageBottom?.setOnClickListener(null)
                binding?.relayStateTextView?.text = getString(R.string.relay_offline)
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

    private fun visibilityRelayState(state: RelayState) {
        when (state) {
            RelayState.ON -> {
                binding?.lightingOnImageBottom?.visibility = View.VISIBLE
                binding?.lightingOffImageBottom?.visibility = View.INVISIBLE
                binding?.relayProgressBar?.visibility = View.INVISIBLE
            }
            RelayState.OFF -> {
                binding?.lightingOnImageBottom?.visibility = View.INVISIBLE
                binding?.lightingOffImageBottom?.visibility = View.VISIBLE
                binding?.relayProgressBar?.visibility = View.INVISIBLE
            }
            RelayState.OFFLINE -> {
                binding?.lightingOnImageBottom?.visibility = View.INVISIBLE
                binding?.lightingOffImageBottom?.visibility = View.INVISIBLE
                binding?.relayProgressBar?.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sensor_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteSensorFromList) {
            relayId?.let {
                HideSensorDialogFragment.createInstance(it)
                    .show(childFragmentManager, "RelayFragmentDialog")
            }
        }

        return super.onOptionsItemSelected(item)
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