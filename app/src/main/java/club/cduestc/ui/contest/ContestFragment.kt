package club.cduestc.ui.contest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentContestBinding
import club.cduestc.ui.contest.item.ContestLine
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import com.alibaba.fastjson.JSONObject

class ContestFragment : Fragment() {

    private lateinit var contestViewModel: ContestViewModel
    private var _binding: FragmentContestBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contestViewModel = ViewModelProvider(this).get(ContestViewModel::class.java)
        _binding = FragmentContestBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init(){
        val timeLine = binding.contestTimeLine
        timeLine.removeAllViews()
        NetManager.createTask{
            val list = NetManager.allContest()
            requireActivity().runOnUiThread{
                list?.forEach{
                    it as JSONObject
                    val line = ContestLine(requireActivity(), it)
                    timeLine.addView(line)
                }

                AnimUtil.hide(binding.contestLoad)
                binding.contestMenu.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}