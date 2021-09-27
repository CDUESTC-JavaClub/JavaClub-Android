package club.cduestc.fragment.contest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentContestBinding

class ContestFragment : Fragment() {

    private lateinit var contestViewModel: ContestViewModel
    private lateinit var binding: FragmentContestBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contestViewModel = ViewModelProvider(this).get(ContestViewModel::class.java)
        binding = FragmentContestBinding.inflate(inflater, container, false)

        return binding.root
    }
}