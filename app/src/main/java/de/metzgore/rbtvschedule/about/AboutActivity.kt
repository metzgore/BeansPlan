package de.metzgore.rbtvschedule.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import de.metzgore.rbtvschedule.BuildConfig
import de.metzgore.rbtvschedule.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aboutPage = AboutPage(this).apply {
            isRTL(false)
            setDescription(getString(R.string.activity_about_description))
            setImage(R.mipmap.ic_launcher)
            addGroup(getString(R.string.about_activity_me_title))
            addEmail("nicoriemer@gmail.com", getString(R.string.about_activity_me_mail))
            addTwitter("metzgor3", getString(R.string.about_activity_me_twitter))
            addGitHub("metzgore", getString(R.string.about_activity_me_github))
            addPlayStore(BuildConfig.APPLICATION_ID, getString(R.string.about_activity_me_playstore))
            addGroup(getString(R.string.about_activity_rbtv_title))
            addWebsite("https://www.rocketbeans.tv", getString(R.string.about_activity_rbtv_website))
            addItem(getTwitchElement())
            addYoutube("UCQvTDmHza8erxZqDkjQ4bQQ", getString(R.string.about_activity_rbtv_youtube))
            addYoutube("UCtSP1OA6jO4quIGLae7Fb4g", getString(R.string.about_activity_rbtv_gaming_youtube))
            addYoutube("UCkfDws3roWo1GaA3pZUzfIQ", getString(R.string.about_activity_rbtv_haengi_youtube))
            addFacebook("RocketBeansTV", getString(R.string.about_activity_rbtv_facebook))
            addTwitter("therocketbeans", getString(R.string.about_activity_rbtv_twitter))
            addInstagram("rocketbeans.tv", getString(R.string.about_activity_rbtv_instagram))
            addPlayStore("tv.rocketbeans.pocketbeans", getString(R.string.about_activity_rbtv_playstore))
            addGroup("Sonstiges")
            addItem(getOpenSourceElement())
            addWebsite("http://api.rbtv.rodney.io", getString(R.string.about_activity_misc_data_url))
            addItem(getVersionElement())
        }.create()

        setContentView(aboutPage)
    }

    private fun getVersionElement(): Element? {
        val versionElement = Element()
        versionElement.title = getString(R.string.activity_about_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        versionElement.gravity = Gravity.CENTER
        return versionElement
    }

    private fun getTwitchElement(): Element {
        val twitchElement = Element()
        twitchElement.title = getString(R.string.about_activity_rbtv_twitch)
        twitchElement.iconDrawable = R.drawable.ic_twitch
        twitchElement.gravity = Gravity.LEFT
        twitchElement.intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/rocketbeanstv"))
        return twitchElement
    }

    private fun getOpenSourceElement(): Element {
        val openSourceElement = Element()
        openSourceElement.title = getString(R.string.activity_about_open_source_licenses_title)
        openSourceElement.iconDrawable = R.drawable.ic_copyright
        openSourceElement.gravity = Gravity.LEFT
        openSourceElement.onClickListener = View.OnClickListener {
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag(OssLicenseDialog.TAG)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            OssLicenseDialog().show(ft, OssLicenseDialog.TAG)
        }
        return openSourceElement
    }
}