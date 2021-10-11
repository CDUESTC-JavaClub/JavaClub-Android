package club.cduestc.ui.contest

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.R
import club.cduestc.databinding.FragmentContestBinding
import club.cduestc.ui.contest.item.ContestLine
import club.cduestc.ui.contest.item.JobLine
import club.cduestc.ui.contest.item.MarketCard
import club.cduestc.ui.contest.sub.MyMarketActivity
import club.cduestc.ui.kc.sub.KcStudentActivity
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSONObject
import java.util.concurrent.CountDownLatch

class ContestFragment : Fragment() {

    private lateinit var contestViewModel: ContestViewModel
    private var _binding: FragmentContestBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contestViewModel = ViewModelProvider(this).get(ContestViewModel::class.java)
        _binding = FragmentContestBinding.inflate(inflater, container, false)

        binding.segmented.addTab("竞赛和活动", binding.panelContest)
        binding.segmented.addTab("跳蚤市场", binding.panelMarket)
        binding.segmented.addTab("招聘兼职", binding.panelJob)

        binding.marketMyBtn.setOnClickListener {
            if(UserManager.getBindId() == null || UserManager.getBindId()!!.isEmpty()){
                Toast.makeText(requireContext(), "此功能需要绑定学号后才能使用！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivityForResult(Intent(context, MyMarketActivity::class.java), 10)
        }

        init()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && data != null){
            val reload = data.getBooleanExtra("reload", false)
            if(reload) NetManager.createTask{ loadMarketPanel(null) }
        }
    }

    private fun init(){
        NetManager.createTask{
            val latch = CountDownLatch(binding.segmented.tabCount() + 1)
            NetManager.createTask{ loadContestPanel(latch) }
            NetManager.createTask{ loadMarketPanel(latch) }
            NetManager.createTask{ loadJobs(latch) }
            NetManager.createTask{ loadSupplier(latch) }
            latch.await()
            requireActivity().runOnUiThread {
                AnimUtil.hide(binding.contestLoad)
                AnimUtil.show( binding.panelContest, 0f, 1f, 150)
                AnimUtil.show( binding.contestMenu, 0f, 1f, 150)
            }
        }
    }

    private fun loadSupplier(latch: CountDownLatch){
        val obj = NetManager.getSupplier()
        requireActivity().runOnUiThread {
            if(obj != null){
                binding.supplierContest.text = getString(R.string.model_contest_contest,
                    obj.getJSONObject("contest").getString("supplier"))
                binding.supplierMarket.text = getString(R.string.model_contest_market,
                    obj.getJSONObject("market").getString("supplier"))
                binding.supplierJobs.text = getString(R.string.model_contest_job,
                    obj.getJSONObject("jobs").getString("supplier"))

                binding.contactMarket.setOnClickListener {
                    toWeb(obj.getJSONObject("market").getString("contact"))
                }
                binding.jobJoinBtn.setOnClickListener {
                    toWeb(obj.getJSONObject("jobs").getString("contact"))
                }
            }
            latch.countDown()
        }
    }

    private fun toWeb(url : String?){
        if(url == null || url.isEmpty()) return
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            startActivity(intent)
        }catch (e : ActivityNotFoundException){
            if(url.startsWith("mqqwpa:")) Toast.makeText(requireContext()
                , getString(R.string.driect_no_app), Toast.LENGTH_LONG).show()
        }
    }

    private fun loadJobs(latch: CountDownLatch){
        val list = NetManager.getNews("jobs")
        requireActivity().runOnUiThread {
            list?.forEach {
                it as JSONObject
                binding.jobsList.addView(JobLine(requireActivity(), it))
            }
            latch.countDown()
        }
    }

    private fun loadMarketPanel(latch: CountDownLatch?){
        val marketLeft = binding.marketWaterfallLeft
        val marketRight = binding.marketWaterfallRight
        requireActivity().runOnUiThread{
            marketLeft.removeAllViews()
            marketRight.removeAllViews()
        }
        val list = NetManager.getNews("market")
        requireActivity().runOnUiThread{
            var o = 0
            list?.forEach {
                it as JSONObject
                val card = MarketCard(requireActivity(), it)
                if(o == 0) marketLeft.addView(card)
                else marketRight.addView(card)
                o = (o + 1) % 2
            }
            latch?.countDown()
        }
    }

    private fun loadContestPanel(latch: CountDownLatch){
        val timeLine = binding.contestTimeLine
        requireActivity().runOnUiThread{ timeLine.removeAllViews() }
        val list = NetManager.getNews("contest")
        requireActivity().runOnUiThread{
            list?.forEach{
                it as JSONObject
                val line = ContestLine(requireActivity(), it)
                timeLine.addView(line)
            }
            latch.countDown()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}