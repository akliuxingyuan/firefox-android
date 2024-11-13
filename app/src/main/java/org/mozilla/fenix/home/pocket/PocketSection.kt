/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.pocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.home.HomeSectionHeader
import org.mozilla.fenix.home.fake.FakeHomepagePreview
import org.mozilla.fenix.home.pocket.interactor.PocketStoriesInteractor
import org.mozilla.fenix.home.pocket.ui.PocketStories
import org.mozilla.fenix.home.pocket.ui.PocketStoriesCategories
import org.mozilla.fenix.home.pocket.ui.PoweredByPocketHeader
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.wallpapers.WallpaperState

/**
 * Pocket section for the homepage.
 *
 * @param horizontalPadding Horizontal padding to apply to outermost column.
 * @param state The [PocketState] representing the UI state.
 * @param cardBackgroundColor The [Color] of the card backgrounds.
 * @param interactor [PocketStoriesInteractor] for interactions with the UI.
 */
@Composable
fun PocketSection(
    horizontalPadding: Dp = dimensionResource(R.dimen.home_item_horizontal_margin),
    state: PocketState,
    cardBackgroundColor: Color,
    interactor: PocketStoriesInteractor,
) {
    Column(modifier = Modifier.padding(top = 72.dp)) {
        // Simple wrapper to add horizontal padding to just the header while the stories have none.
        Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
            HomeSectionHeader(
                headerText = stringResource(R.string.pocket_stories_header_1),
            )
        }

        Spacer(Modifier.height(16.dp))

        PocketStories(
            stories = state.stories,
            contentPadding = horizontalPadding,
            backgroundColor = cardBackgroundColor,
            onStoryShown = interactor::onStoryShown,
            onStoryClicked = interactor::onStoryClicked,
            onDiscoverMoreClicked = interactor::onDiscoverMoreClicked,
        )

        Spacer(Modifier.height(24.dp))

        HomeSectionHeader(
            headerText = stringResource(R.string.pocket_stories_categories_header),
        )

        Spacer(Modifier.height(16.dp))

        if (state.categories.isNotEmpty()) {
            PocketStoriesCategories(
                categories = state.categories,
                selections = state.categoriesSelections,
                modifier = Modifier.fillMaxWidth(),
                categoryColors = state.categoryColors,
                onCategoryClick = interactor::onCategoryClicked,
            )
        }

        Spacer(Modifier.height(24.dp))

        PoweredByPocketHeader(
            onLearnMoreClicked = interactor::onLearnMoreClicked,
            modifier = Modifier.fillMaxWidth(),
            textColor = state.textColor,
            linkTextColor = state.linkTextColor,
        )
    }
}

@Preview
@Composable
private fun PocketSectionPreview() {
    FirefoxTheme {
        Box(Modifier.background(FirefoxTheme.colors.layer2)) {
            PocketSection(
                horizontalPadding = 0.dp,
                state = FakeHomepagePreview.pocketState(),
                cardBackgroundColor = WallpaperState.default.cardBackgroundColor,
                interactor = FakeHomepagePreview.homepageInteractor,
            )
        }
    }
}
