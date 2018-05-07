package de.metzgore.beansplan.about

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.IconicsDrawable
import de.metzgore.beansplan.R

class AboutActivity : MaterialAboutActivity() {

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        return createMaterialAboutList(context, R.color.colorAccent)
    }

    override fun getActivityTitle(): CharSequence? {
        return getString(R.string.activity_about_title)
    }

    private fun createMaterialAboutList(c: Context, colorIcon: Int): MaterialAboutList {
        val appCardBuilder = appCardBuilder(c, colorIcon)

        val meCardBuilder = meCardBuilder(c, colorIcon)

        val rbtvCardBuilder = rbtvCardBuilder(c, colorIcon)

        return MaterialAboutList(appCardBuilder.build(), meCardBuilder.build(), rbtvCardBuilder.build())
    }

    private fun appCardBuilder(c: Context, colorIcon: Int): MaterialAboutCard.Builder {
        val appCardBuilder = MaterialAboutCard.Builder()

        appCardBuilder.addItem(MaterialAboutTitleItem.Builder().apply {
            text(R.string.app_name)
            desc(R.string.activity_about_description)
            icon(R.mipmap.ic_launcher)
        }.build())

        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_information)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_version),
                true))

        appCardBuilder.addItem(ConvenienceBuilder.createRateActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_star)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_me_playstore),
                null
        ))

        /*appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Changelog")
                .icon(IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_history)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebViewDialogOnClickAction(c, "Releases", "https://github.com/daniel-stoneuk/material-about-library/releases", true, false))
                .build())*/

        appCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_earth)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_misc_data_url),
                true,
                Uri.parse("http://api.rbtv.rodney.io/")
        ))

        appCardBuilder.addItem(MaterialAboutActionItem.Builder().apply {
            text(R.string.activity_about_open_source_licenses_title)
            icon(IconicsDrawable(c).apply {
                icon(CommunityMaterial.Icon.cmd_book)
                color(ContextCompat.getColor(c, colorIcon))
                sizeDp(18)
            })
            setOnClickAction({
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                val prev = fm.findFragmentByTag(OssLicenseDialog.TAG)
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)

                OssLicenseDialog().show(ft, OssLicenseDialog.TAG)
            })
        }.build())

        return appCardBuilder
    }

    private fun meCardBuilder(c: Context, colorIcon: Int): MaterialAboutCard.Builder {
        val meCardBuilder = MaterialAboutCard.Builder().apply {
            title(R.string.activity_about_me_title)
        }

        meCardBuilder.addItem(MaterialAboutActionItem.Builder().apply {
            text("Nico Riemer")
            icon(IconicsDrawable(c).apply {
                icon(CommunityMaterial.Icon.cmd_account)
                color(ContextCompat.getColor(c, colorIcon))
                sizeDp(18)
            })
        }.build())

        meCardBuilder.addItem(ConvenienceBuilder.createEmailItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_email)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_me_mail_title),
                true,
                getString(R.string.activity_about_me_mail),
                getString(R.string.activity_about_me_mail_subject)))

        meCardBuilder.addItem(MaterialAboutActionItem.Builder().apply {
            text(R.string.activity_about_me_github)
            icon(IconicsDrawable(c).apply {
                icon(CommunityMaterial.Icon.cmd_github_circle)
                color(ContextCompat.getColor(c, colorIcon))
                sizeDp(18)
            })
            setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(c, Uri.parse("https://github.com/metzgore")))
        }.build())

        meCardBuilder.addItem(MaterialAboutActionItem.Builder().apply {
            text(R.string.activity_about_me_twitter)
            icon(IconicsDrawable(c).apply {
                icon(CommunityMaterial.Icon.cmd_twitter)
                color(ContextCompat.getColor(c, colorIcon))
                sizeDp(18)
            })
            setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(c, Uri.parse("https://twitter.com/metzgor3")))
        }.build())

        return meCardBuilder
    }

    private fun rbtvCardBuilder(c: Context, colorIcon: Int): MaterialAboutCard.Builder {
        val rbtvCardBuilder = MaterialAboutCard.Builder().apply {
            title(R.string.activity_about_rbtv_title)
        }

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_earth)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_website),
                true,
                Uri.parse("https://www.rocketbeans.tv")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_youtube_play)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_youtube),
                false,
                Uri.parse("https://www.youtube.com/channel/UCQvTDmHza8erxZqDkjQ4bQQ")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_youtube_play)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_gaming_youtube),
                false,
                Uri.parse("https://www.youtube.com/channel/UCtSP1OA6jO4quIGLae7Fb4g")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_youtube_play)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_haengi_youtube),
                false,
                Uri.parse("https://www.youtube.com/channel/UCkfDws3roWo1GaA3pZUzfIQ")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_twitch)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_twitch),
                false,
                Uri.parse("https://www.twitch.tv/rocketbeanstv")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_facebook)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_facebook),
                false,
                Uri.parse("https://www.facebook.com/RocketBeansTV/")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_twitter)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_twitter),
                false,
                Uri.parse("https://twitter.com/therocketbeans")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_instagram)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_instagram),
                false,
                Uri.parse("https://www.instagram.com/rocketbeans.tv/?hl=de")))
                .build()

        rbtvCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                IconicsDrawable(c).apply {
                    icon(CommunityMaterial.Icon.cmd_google_play)
                    color(ContextCompat.getColor(c, colorIcon))
                    sizeDp(18)
                },
                getString(R.string.activity_about_rbtv_playstore),
                false,
                Uri.parse("https://play.google.com/store/apps/details?id=tv.rocketbeans.pocketbeans")))
                .build()

        return rbtvCardBuilder
    }

}
