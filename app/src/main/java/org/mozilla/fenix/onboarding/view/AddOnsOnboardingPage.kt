/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.LinkText
import org.mozilla.fenix.compose.LinkTextState
import org.mozilla.fenix.compose.annotation.FlexibleWindowLightDarkPreview
import org.mozilla.fenix.compose.button.PrimaryButton
import org.mozilla.fenix.theme.FirefoxTheme

private const val MINIMUM_SCREEN_HEIGHT_FOR_IMAGE = 640

/**
 * A Composable for displaying Add-on onboarding page content.
 *
 * @param pageState The page content that's displayed.
 */
@Composable
fun AddOnsOnboardingPage(pageState: OnboardingAddOnsPageState) {
    // Base
    Column(
        modifier = Modifier
            .background(FirefoxTheme.colors.layer1)
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        with(pageState) {
            // Main content group
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Header(imageRes, title, description)

                Spacer(Modifier.height(16.dp))

                AddOns(addOnsUiData)

                Spacer(Modifier.height(5.dp))

                MoreExtensionsLink()
            }

            PrimaryButton(
                text = primaryButton.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .semantics { testTag = title + "onboarding_card.positive_button" },
                onClick = primaryButton.onClick,
            )
        }
    }

    LaunchedEffect(pageState) {
        pageState.onRecordImpressionEvent()
    }
}

@Composable
private fun MoreExtensionsLink() {
    LinkText(
        text = stringResource(R.string.onboarding_add_on_explore_more_extensions_2),
        linkTextStates = listOf(
            LinkTextState(
                text = stringResource(R.string.onboarding_add_on_explore_more_extensions_2),
                url = "",
                onClick = {},
            ),
        ),
    )
}

@Composable
private fun Header(@DrawableRes imageRes: Int, title: String, description: String) {
    val showHeaderImage = LocalConfiguration.current.screenHeightDp > MINIMUM_SCREEN_HEIGHT_FOR_IMAGE

    if (showHeaderImage) {
        HeaderImage(imageRes)
    } else {
        Spacer(Modifier.height(2.dp))
    }

    Spacer(Modifier.height(34.dp))

    Text(
        text = title,
        color = FirefoxTheme.colors.textPrimary,
        textAlign = TextAlign.Center,
        style = FirefoxTheme.typography.headline5,
    )

    Spacer(Modifier.height(16.dp))

    Text(
        text = description,
        color = FirefoxTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
        style = FirefoxTheme.typography.body2,
    )
}

@Composable
private fun HeaderImage(@DrawableRes imageRes: Int) {
    Spacer(Modifier.height(4.dp))

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .height(110.dp)
            .width(140.dp),
    )
}

@Composable
private fun AddOns(addOnUiData: List<OnboardingAddOn>) = addOnUiData.forEach { AddOnItem(it) }

@Composable
private fun AddOnItem(addOnUiData: OnboardingAddOn) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        with(addOnUiData) {
            AddOnIcon(iconRes)

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) { AddOnDetails(name, description, averageRating, numberOfReviews) }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically),
        ) { AddAddOnButton() }
    }
}

@Composable
private fun AddOnDetails(
    name: String,
    description: String,
    averageRating: String,
    numberOfReviews: String,
) {
    Text(
        color = FirefoxTheme.colors.textSecondary,
        style = FirefoxTheme.typography.headline7,
        maxLines = 1,
        text = name,
    )

    Text(
        color = FirefoxTheme.colors.textSecondary,
        style = FirefoxTheme.typography.body2,
        maxLines = 2,
        text = description,
    )

    Spacer(Modifier.height(8.dp))

    RatingAndReviewRow(averageRating, numberOfReviews)
}

@Composable
private fun AddOnIcon(iconRes: Int) {
    Image(
        painter = painterResource(id = iconRes),
        modifier = Modifier
            .width(38.dp)
            .height(38.dp)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = colorResource(id = R.color.fx_mobile_border_color_primary),
                ),
                shape = MaterialTheme.shapes.small,
            )
            .padding(7.dp),
        contentDescription = stringResource(R.string.onboarding_add_on_icon_content_description),
    )
}

@Composable
private fun AddAddOnButton() {
    IconButton(onClick = {}) {
        Icon(
            painter = painterResource(R.drawable.mozac_ic_plus_24),
            contentDescription = stringResource(R.string.onboarding_add_on_add_button_content_description),
            tint = FirefoxTheme.colors.iconPrimary,
        )
    }
}

@Composable
private fun RatingAndReviewRow(rating: String, numberOfReviews: String) {
    Row {
        AverageRatingRow(rating)

        Spacer(Modifier.width(8.dp))

        ReviewCountRow(numberOfReviews)
    }
}

@Composable
private fun AverageRatingRow(rating: String) {
    val ratingContentDescription = stringResource(R.string.onboarding_add_on_star_rating_content_description)
    Row(
        Modifier
            .wrapContentWidth()
            .semantics { contentDescription = ratingContentDescription },
    ) {
        val ratingAsFloat = rating.toFloat()

        // Set the full icons.
        @Suppress("UnusedPrivateProperty") // required for index
        for (index in 0 until ratingAsFloat.toInt()) {
            Image(
                painter = painterResource(R.drawable.ic_bookmark_filled),
                contentDescription = null, // covered by the Row content description.
                modifier = Modifier.size(14.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(FirefoxTheme.colors.actionPrimary),
            )
        }

        // If rating has decimal, set the half filled icon.
        val isWholeNumber = ratingAsFloat % 1.0f == 0.0f
        if (!isWholeNumber) {
            Image(
                painter = painterResource(R.drawable.ic_bookmark_half_fill_20),
                contentDescription = null, // covered by the Row content description.
                modifier = Modifier.size(14.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(FirefoxTheme.colors.actionPrimary),
            )
        }
    }
}

@Composable
private fun ReviewCountRow(numberOfReviews: String) {
    Row(Modifier.wrapContentWidth()) {
        Text(
            color = FirefoxTheme.colors.textSecondary,
            style = FirefoxTheme.typography.caption,
            maxLines = 1,
            text = stringResource(R.string.onboarding_add_on_reviews_label, numberOfReviews),
        )
    }
}

// *** Code below used for previews only *** //

@FlexibleWindowLightDarkPreview
@Composable
private fun OnboardingPagePreview() {
    FirefoxTheme {
        AddOnsOnboardingPage(
            pageState = OnboardingAddOnsPageState(
                imageRes = R.drawable.ic_onboarding_add_ons,
                title = stringResource(id = R.string.onboarding_add_on_header),
                description = stringResource(id = R.string.onboarding_add_on_sub_header),
                primaryButton = Action(
                    text = stringResource(
                        id = R.string.onboarding_add_on_start_browsing_button_2,
                    ),
                    onClick = {},
                ),
                addOnsUiData = with(LocalContext.current) {
                    listOf(
                        addOnItemUblock(this),
                        addOnItemPrivacyBadger(this),
                        addOnItemSearchByImage(this),
                    )
                },
                onRecordImpressionEvent = {},
            ),
        )
    }
}

private fun addOnItemUblock(context: Context) = OnboardingAddOn(
    R.drawable.ic_add_on_ublock,
    context.getString(R.string.onboarding_add_on_ublock_name),
    context.getString(R.string.onboarding_add_on_ublock_description),
    "5",
    "18,347",
)

private fun addOnItemPrivacyBadger(context: Context) = OnboardingAddOn(
    R.drawable.ic_add_on_privacy_badger,
    context.getString(R.string.onboarding_add_on_privacy_badger_name),
    context.getString(R.string.onboarding_add_on_privacy_badger_description),
    "5",
    "2,500",
)

private fun addOnItemSearchByImage(context: Context) = OnboardingAddOn(
    R.drawable.ic_add_on_search_by_image,
    context.getString(R.string.onboarding_add_on_ublock_name),
    context.getString(R.string.onboarding_add_on_ublock_description),
    "4.5",
    "1,533",
)