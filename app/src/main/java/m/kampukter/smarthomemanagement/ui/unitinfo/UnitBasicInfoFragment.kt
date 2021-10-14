package m.kampukter.smarthomemanagement.ui.unitinfo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.databinding.UnitBasicInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitBasicInfoFragment : Fragment() {

    private var binding: UnitBasicInfoFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitBasicInfoFragmentBinding.inflate(inflater, container, false)
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
                binding?.unitIdTextView?.text = getString(R.string.unit_id_title, unitInfo.id)
                binding?.unitDescriptionTextView?.text = unitInfo.description

                if (unitInfo.name != binding?.unitNameTextInputEdit?.text.toString())
                    binding?.unitNameTextInputEdit?.setText(unitInfo.name)
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
}