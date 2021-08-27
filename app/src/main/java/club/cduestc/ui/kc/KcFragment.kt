package club.cduestc.ui.kc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentKcBinding

class KcFragment : Fragment() {

    private lateinit var kcViewModel: KcViewModel
    private var _binding: FragmentKcBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        kcViewModel =
            ViewModelProvider(this).get(KcViewModel::class.java)

        _binding = FragmentKcBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}