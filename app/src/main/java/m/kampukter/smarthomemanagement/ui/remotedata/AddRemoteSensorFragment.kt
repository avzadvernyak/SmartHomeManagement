package m.kampukter.smarthomemanagement.ui.remotedata

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import m.kampukter.smarthomemanagement.data.DeviceType
import m.kampukter.smarthomemanagement.data.SensorInfo
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

        viewModel.selectedSensorInfoLiveData.observe(viewLifecycleOwner) { sensor ->

            if (sensor.unitDescription != binding?.unitDescriptionInputEditText?.text.toString()) {
                binding?.unitDescriptionInputEditText?.setText(sensor.unitDescription)
            }

            if (sensor.sensorName != binding?.sensorNameInputEditText?.text.toString())
                binding?.sensorNameInputEditText?.setText(sensor.sensorName)

            (activity as AppCompatActivity).title = sensor.sensorName

            when (sensor.sensorDeviceType) {
                DeviceType.RELAY -> {
                    binding?.sensorTypeTextView?.text = "Тип: Реле"
                    binding?.sensorMeasureTextView?.visibility = View.INVISIBLE
                }
                DeviceType.Device -> {
                    binding?.sensorTypeTextView?.text = "Тип: Измеритель"
                    binding?.sensorMeasureTextView?.visibility = View.VISIBLE
                    binding?.sensorMeasureTextView?.text =
                        "Единица измерения ${sensor.sensorMeasure}"
                }
            }

            binding?.unitNameTextView?.text = sensor.unitName
            binding?.unitUrlTextView?.text = sensor.unitUrl

            context?.let { context ->
                val imageResource =
                    context.resources.getIdentifier(
                        sensor.sensorType.uri,
                        null,
                        context.packageName
                    )
                ResourcesCompat.getDrawable(context.resources, imageResource, null)?.let {
                    binding?.imageItemImageView?.setImageDrawable(it)
                }
            }

            binding?.saveButton?.setOnClickListener {
                val unitInfo =
                    UnitInfo(sensor.unitId, sensor.unitName, sensor.unitUrl, sensor.unitDescription)
                val sensorInfo = SensorInfo(
                    id = sensor.id,
                    unitId = sensor.unitId,
                    unitSensorId = sensor.unitSensorId,
                    name = sensor.sensorName,
                    measure = sensor.sensorMeasure,
                    deviceType = sensor.sensorDeviceType,
                    icon = sensor.sensorType
                )
                viewModel.addNewSensor(unitInfo, sensorInfo)
                activity?.supportFragmentManager?.popBackStack("MainFragment", 0)
                //activity?.supportFragmentManager?.popBackStack("RemoteUnits", POP_BACK_STACK_INCLUSIVE)

            }
        }
        binding?.changeSensorImageButton?.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    ChangeSensorImageFragment.createInstance()
                )
                setReorderingAllowed(true)
                addToBackStack("ChangeSensorImage")
            }
        }
        binding?.sensorNameInputEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setSensorName(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding?.unitDescriptionInputEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setUnitDescription(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    companion object {
        fun createInstance() = AddRemoteSensorFragment()
    }
}