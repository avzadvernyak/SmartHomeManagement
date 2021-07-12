package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URL

class MainActivity: AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(android.R.id.content, MainFragment.createInstance())
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.unitListLiveData.observe(this){ unit->
            unit.map { it.deviceIp }.forEach { unitIp->
                viewModel.connectToUnit(URL(unitIp))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.unitListLiveData.observe(this){ unit->
            unit.map { it.deviceIp }.forEach { unitIp->
                viewModel.disconnectToUnit(URL(unitIp))
            }
        }
    }
}