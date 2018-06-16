package de.metzgore.beansplan.util

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.matcher.ViewMatchers
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.schibsted.spain.barista.internal.matcher.DisplayedMatchers
import org.hamcrest.CoreMatchers

class ViewPagerItemCountAssertion {
    companion object {
        fun assertViewPagerViewItemCount(@IdRes viewPagerId: Int, expectedItemCount: Int) {
            Espresso.onView(DisplayedMatchers.displayedWithId(viewPagerId)).check(ViewPagerItemCountAssertion(expectedItemCount))
        }
    }

    private class ViewPagerItemCountAssertion(private val count: Int) : ViewAssertion {

        override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            if (view !is ViewPager) {
                throw IllegalStateException("The asserted view is not ViewPager")
            }

            if (view.adapter == null) {
                throw IllegalStateException("No adapter is assigned to ViewPager")
            }

            val pagerAdapter = view.adapter as PagerAdapter
            ViewMatchers.assertThat("RecyclerView item count", pagerAdapter.count, CoreMatchers.equalTo(count))
        }
    }
}