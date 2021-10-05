package club.cduestc.ui.contest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.R
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

        binding.segmented.addTab("竞赛和活动", binding.panelContest)
        binding.segmented.addTab("跳蚤市场", View(context))
        binding.segmented.addTab("校外兼职", View(context))

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
                AnimUtil.show( binding.contestMenu, 0f, 1f, 150)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}