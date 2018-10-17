package com.bernaferrari.emojislidersample

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutFragment
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.IconicsDrawable


class AboutFragment : MaterialAboutFragment() {

    override fun getMaterialAboutList(activityContext: Context): MaterialAboutList {
        return createMaterialAboutList(activityContext) // This creates an empty screen, add cards with .addCard()
    }

    override fun getTheme(): Int = R.style.About

    private fun createMaterialAboutList(c: Context): MaterialAboutList {
        val grey = ContextCompat.getColor(c, R.color.md_grey_800)

        val appCardBuilder = MaterialAboutCard.Builder()

        appCardBuilder.addItem(
            MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .desc("© 2018 Bernardo Ferrari")
                .icon(R.mipmap.ic_launcher)
                .build()
        )

        appCardBuilder.addItem(
            ConvenienceBuilder.createVersionActionItem(
                c,
                IconicsDrawable(c)
                    .icon(CommunityMaterial.Icon.cmd_update)
                    .color(grey)
                    .sizeDp(ICON_SIZE),
                c.getText(R.string.version),
                false
            )
        )

        val author = MaterialAboutCard.Builder()

        author.title(R.string.author)

        author.addItem(
            MaterialAboutActionItem.Builder()
                .text("Bernardo Ferrari")
                .subText("bernaferrari")
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_reddit)
                        .color(grey)
                        .sizeDp(ICON_SIZE)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebsiteOnClickAction(
                        c,
                        "https://www.reddit.com/user/bernaferrari".toUri()
                    )
                )
                .build()
        )

        author.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.github)
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .color(grey)
                        .sizeDp(ICON_SIZE)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createWebsiteOnClickAction(
                        c,
                        "https://github.com/bernaferrari/EmojiSlider".toUri()
                    )
                )
                .build()
        )

        val email = "bernaferrari2@gmail.com"

        author.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.email)
                .subText(email)
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .color(grey)
                        .sizeDp(ICON_SIZE)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createEmailOnClickAction(
                        c,
                        email,
                        getString(R.string.email_subject)
                    )
                )
                .build()
        )

        val otherCardBuilder = MaterialAboutCard.Builder()
        otherCardBuilder.title(R.string.help)

        otherCardBuilder.addItem(
            MaterialAboutActionItem.Builder()
                .text(R.string.bugs)
                .subText(email)
                .icon(
                    IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_bug)
                        .color(grey)
                        .sizeDp(ICON_SIZE)
                )
                .setOnClickAction(
                    ConvenienceBuilder.createEmailOnClickAction(
                        c,
                        email,
                        getString(R.string.email_subject)
                    )
                )
                .build()
        )

        return MaterialAboutList(
            appCardBuilder.build(),
            author.build(),
            otherCardBuilder.build()
        )
    }

    private companion object {
        private const val ICON_SIZE = 18
    }
}
