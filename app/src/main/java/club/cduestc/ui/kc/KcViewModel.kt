package club.cduestc.ui.kc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KcViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is 教务 Fragment"
    }

    val text: LiveData<String> = _text
}