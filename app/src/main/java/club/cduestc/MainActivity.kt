package club.cduestc

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import club.cduestc.databinding.ActivityMainBinding
import club.cduestc.ui.dashboard.BaiFragment
import club.cduestc.ui.home.HomeFragment
import club.cduestc.ui.kc.KcFragment
import club.cduestc.ui.nav.FixFragmentNavigator
import club.cduestc.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //自定义导航栏复用Fragment处理
        val navView: BottomNavigationView = binding.navView
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val navController = NavHostFragment.findNavController(fragment!!)
        val fragmentNavigator = FixFragmentNavigator(this, fragment.childFragmentManager, fragment.id)
        val provider = navController.navigatorProvider
        provider.addNavigator(fragmentNavigator)
        val navGraph = initNavGraph(provider, fragmentNavigator)
        navController.graph = navGraph
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            navController.navigate(item.itemId)
            true
        }
        window.navigationBarColor = Color.TRANSPARENT
    }

    /**
     * 返回时，直接结束
     */
    override fun onBackPressed() {
        finish()
    }

    /**
     * 自定义导航图
     */
    private fun initNavGraph(provider: NavigatorProvider, fragmentNavigator: FixFragmentNavigator): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))

        val destination1 = fragmentNavigator.createDestination()
        destination1.id = R.id.navigation_home
        destination1.className = HomeFragment::class.java.canonicalName ?: ""
        destination1.label = resources.getString(R.string.title_home)
        navGraph.addDestination(destination1)

        val destination2 = fragmentNavigator.createDestination()
        destination2.id = R.id.navigation_kc
        destination2.className = KcFragment::class.java.canonicalName ?: ""
        destination2.label = resources.getString(R.string.title_kc)
        navGraph.addDestination(destination2)

        val destination3 = fragmentNavigator.createDestination()
        destination3.id = R.id.navigation_dashboard
        destination3.className = BaiFragment::class.java.canonicalName ?: ""
        destination3.label = resources.getString(R.string.title_bai)
        navGraph.addDestination(destination3)

        val destination4 = fragmentNavigator.createDestination()
        destination4.id = R.id.navigation_notifications
        destination4.className = SettingsFragment::class.java.canonicalName ?: ""
        destination4.label = resources.getString(R.string.title_settings)
        navGraph.addDestination(destination4)

        navGraph.startDestination = R.id.navigation_home
        return navGraph
    }
}