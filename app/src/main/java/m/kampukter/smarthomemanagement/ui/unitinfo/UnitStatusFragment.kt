package m.kampukter.smarthomemanagement.ui.unitinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
import m.kampukter.smarthomemanagement.databinding.UnitStatusFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitStatusFragment : Fragment() {
    private var binding: UnitStatusFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitStatusFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.unitLiveData.observe(viewLifecycleOwner) {
            it?.let { unitInfo ->
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
}