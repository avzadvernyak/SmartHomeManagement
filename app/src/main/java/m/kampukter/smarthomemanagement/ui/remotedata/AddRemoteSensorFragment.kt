package m.kampukter.smarthomemanagement.ui.remotedata

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import m.kampukter.smarthomemanagement.data.SensorInfo
import m.kampukter.smarthomemanagement.data.DeviceType
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.databinding.AddRemoteSensorFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddRemoteSensorFragment : Fragment() {

    private var binding: AddRemoteSensorFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddRemoteSensorFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.addRemoteSensorToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.addRemoteSensorToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        viewModel.selectedSensorInfoLiveData.observe(viewLifecycleOwner) { (unit, sensor) ->

            if (unit.description != binding?.unitDescriptionInputEditText?.text.toString()) {
                binding?.unitDescriptionInputEditText?.setText(unit.description)
            }
            (activity as AppCompatActivity).title = sensor.name

            binding?.sensorNameTextView?.text = sensor.name
            when (sensor.deviceType) {
                DeviceType.RELAY -> binding?.sensorTypeTextView?.text = "Реле"
                DeviceType.Device -> binding?.sensorTypeTextView?.text = "Сенсор"
            }
            binding?.sensorMeasureTextView?.text = sensor.measure
            binding?.unitNameTextView?.text = unit.name
            binding?.unitUrlTextView?.text = unit.url

            binding?.saveButton?.setOnClickListener {
                val unitInfo = UnitInfo(unit.id, unit.name, unit.url, unit.description)
                val sensorInfo = SensorInfo(
                    id = sensor.id,
                    unitId = sensor.unitId,
                    unitSensorId = sensor.unitSensorId,
                    name = sensor.name,
                    measure = sensor.measure,
                    deviceType = sensor.deviceType,
                    icon = SensorType.THERMOMETER
                )
                viewModel.addNewSensor(unitInfo, sensorInfo)
                viewModel.changeCandidateStatus(sensor.id, false)
                activity?.supportFragmentManager?.popBackStack("MainFragment", 0)
                //activity?.supportFragmentManager?.popBackStack("RemoteUnits", POP_BACK_STACK_INCLUSIVE)

            }
        }
        binding?.unitDescriptionInputEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setUnitDescription(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
        //for(type in SensorType.values()) Log.d("blabla","type - ${type.name} / ${type.url}")
    }

    companion object {
        fun createInstance() = AddRemoteSensorFragment()
    }
}