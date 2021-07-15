package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.unit_info_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.net.URL

class UnitInfoFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var currentUnitId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.unit_info_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(unitInfoToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_unit)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        unitInfoToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        arguments?.getString("ARG_ID_UNIT")?.let {
            viewModel.setIdUnitForSearch(it)
            currentUnitId = it

        }
        viewModel.unitLiveData.observe(viewLifecycleOwner) {
            it?.let { unitInfo ->
                if (currentUnitId == unitInfo.id) {
                    unitIdTextView.text = getString(R.string.unit_id_title, unitInfo.id)

                    if (unitInfo.description != unitDescriptionTextInputEdit.text.toString()) unitDescriptionTextInputEdit.setText(
                        unitInfo.description
                    )
                    if (unitInfo.name != unitNameTextInputEdit.text.toString()) unitNameTextInputEdit.setText(
                        unitInfo.name
                    )

                    unitUrlTextInputEdit.setText(unitInfo.url)

                    unitConnectButton.visibility = View.INVISIBLE
                    val stringStatus = when (unitInfo.wsConnectionStatus) {
                        is WSConnectionStatus.Connected -> "Устройство подключено"
                        is WSConnectionStatus.Connecting -> "Установка связи с устройством"
                        is WSConnectionStatus.Closing -> "Отключение устройства"
                        is WSConnectionStatus.Failed -> {
                            unitConnectButton.visibility = View.VISIBLE
                            "Ошибка подключения ${(unitInfo.wsConnectionStatus as WSConnectionStatus.Failed).reason}"
                        }
                        is WSConnectionStatus.Disconnected -> {
                            unitConnectButton.visibility = View.VISIBLE
                            "Устройство отключено"
                        }
                        else -> {
                            "Ожидаются данные..."
                        }
                    }
                    unitStatusTextView.text =
                        getString(R.string.status_title, stringStatus)
                    unitConnectButton.setOnClickListener { viewModel.connectToUnit(URL(unitInfo.url)) }
                }
            }
        }
        currentUnitId?.let { id ->
            unitNameTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.editUnitName(id, p0.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            unitDescriptionTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.editUnitDescription(id, p0.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            unitNameTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }
    }

    companion object {
        private const val ARG_ID_UNIT = "ARG_ID_UNIT"
        fun createInstance(message: String) = UnitInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_UNIT, message)
            }
        }
    }
}