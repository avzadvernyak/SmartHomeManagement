package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
import m.kampukter.smarthomemanagement.databinding.UnitInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitInfoFragment : Fragment() {

    private var binding: UnitInfoFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitInfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.unitInfoToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_unit)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.unitInfoToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        arguments?.getString("ARG_ID_SENSOR")?.let { sensorId ->
            viewModel.setIdSensorForSearch(sensorId)
            viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) {
                if (it != null && it.id == sensorId) {
                    viewModel.setIdUnitForSearch(it.unitId)
                }
            }
            viewModel.unitLiveData.observe(viewLifecycleOwner) {
                it?.let { unitInfo ->
                    binding?.unitIdTextView?.text = getString(R.string.unit_id_title, unitInfo.id)

                    if (unitInfo.description != binding?.unitDescriptionTextInputEdit?.text.toString())
                        binding?.unitDescriptionTextInputEdit?.setText(unitInfo.description)
                    if (unitInfo.name != binding?.unitNameTextInputEdit?.text.toString())
                        binding?.unitNameTextInputEdit?.setText(unitInfo.name)
                    /*if (unitInfo.url != binding?.unitUrlTextInputEdit?.text.toString())
                        binding?.unitUrlTextInputEdit?.setText(unitInfo.url)*/

                    //binding?.unitUrlTextInputEdit?.setText(unitInfo.url)

                    binding?.unitConnectButton?.visibility = View.INVISIBLE
                    val stringStatus = when (unitInfo.wsConnectionStatus) {
                        is WSConnectionStatus.Connected -> "Устройство подключено"
                        is WSConnectionStatus.Connecting -> "Установка связи с устройством"
                        is WSConnectionStatus.Closing -> "Отключение устройства"
                        is WSConnectionStatus.Failed -> {
                            binding?.unitConnectButton?.visibility = View.VISIBLE
                            "Ошибка подключения ${(unitInfo.wsConnectionStatus as WSConnectionStatus.Failed).reason}"
                        }
                        is WSConnectionStatus.Disconnected -> {
                            binding?.unitConnectButton?.visibility = View.VISIBLE
                            "Устройство отключено"
                        }
                        else -> {
                            "Ожидаются данные..."
                        }
                    }
                    binding?.unitStatusTextView?.text =
                        getString(R.string.status_title, stringStatus)
                    binding?.unitConnectButton?.setOnClickListener {
                        viewModel.connectByIdUnit(unitInfo.id)
                    }
                }
            }
        }
        binding?.unitNameTextInputEdit?.addTextChangedListener(object :
            TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.editUnitName(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding?.unitDescriptionTextInputEdit?.addTextChangedListener(object :
            TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.editUnitDescription(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
/*
        binding?.unitUrlTextInputEdit?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                viewModel.editUnitUrl(it.unitId, p0.toString())
            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
*/

    }

    override fun onResume() {
        super.onResume()
        arguments?.getString("ARG_ID_SENSOR")?.let { id ->
            id.let { viewModel.connectToUnit(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        arguments?.getString("ARG_ID_SENSOR")?.let { id ->
            id.let { viewModel.connectToUnit(it) }
        }
    }


    companion object {
        private const val ARG_ID_SENSOR = "ARG_ID_SENSOR"
        fun createInstance(message: String) = UnitInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_SENSOR, message)

            }
        }
    }
}