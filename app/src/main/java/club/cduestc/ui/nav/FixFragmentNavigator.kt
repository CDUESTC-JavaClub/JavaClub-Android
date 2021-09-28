package club.cduestc.ui.nav

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import club.cduestc.util.AnimUtil
import java.lang.Exception
import java.lang.reflect.Field
import java.util.ArrayDeque


@Navigator.Name("fixFragment")
class FixFragmentNavigator(context: Context, manager: FragmentManager, containerId: Int) :
    FragmentNavigator(context, manager, containerId) {

    private val mContext: Context = context
    private val mContainerId = containerId
    private var mFragmentManager: FragmentManager = manager
    private val _tag = "FragmentNavigator"

    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            Log.i(_tag, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }
        var frag = mFragmentManager.findFragmentByTag(className)
        if(frag == null) frag = instantiateFragment(mContext, mFragmentManager, className, args)

        frag.arguments = args
        val ft = mFragmentManager.beginTransaction()

        val fragments: List<Fragment> = mFragmentManager.fragments
        for (fragment in fragments) {
            ft.hide(fragment)
        }
        if (!frag.isAdded) {
            ft.add(mContainerId, frag, className)
        }
        ft.show(frag)
        if(frag.view != null) AnimUtil.show(frag.requireView(), 0.6f, 1.0f)
        ft.setPrimaryNavigationFragment(frag)

        val mBackStack : ArrayDeque<Int> = try {
            val field: Field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            field.get(this) as ArrayDeque<Int>
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        @IdRes val destId = destination.id
        val initialNavigation = mBackStack.isEmpty()
        // TODO Build first class singleTop behavior for fragments
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)

        val isAdded: Boolean = when {
            initialNavigation -> {
                true
            }
            isSingleTopReplacement -> {
                // Single Top means we only want one instance on the back stack
                if (mBackStack.size > 1) {
                    // If the Fragment to be replaced is on the FragmentManager's
                    // back stack, a simple replace() isn't enough so we
                    // remove it from the back stack and put our replacement
                    // on the back stack in its place
                    mFragmentManager.popBackStack(
                        generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
                }
                false
            }
            else -> {
                ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
                true
            }
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key!!, value!!)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }
}