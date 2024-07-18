/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

@file:Suppress("TooManyFunctions")

package org.mozilla.fenix.ui.robots

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers.allOf
import org.mozilla.fenix.R
import org.mozilla.fenix.helpers.AppAndSystemHelper.assertExternalAppOpens
import org.mozilla.fenix.helpers.AppAndSystemHelper.getPermissionAllowID
import org.mozilla.fenix.helpers.Constants.PackageName.GOOGLE_APPS_PHOTOS
import org.mozilla.fenix.helpers.Constants.TAG
import org.mozilla.fenix.helpers.DataGenerationHelper.getStringResource
import org.mozilla.fenix.helpers.HomeActivityComposeTestRule
import org.mozilla.fenix.helpers.MatcherHelper.assertUIObjectExists
import org.mozilla.fenix.helpers.MatcherHelper.itemContainingText
import org.mozilla.fenix.helpers.MatcherHelper.itemWithDescription
import org.mozilla.fenix.helpers.MatcherHelper.itemWithResId
import org.mozilla.fenix.helpers.MatcherHelper.itemWithResIdAndText
import org.mozilla.fenix.helpers.MatcherHelper.itemWithResIdContainingText
import org.mozilla.fenix.helpers.TestAssetHelper.waitingTime
import org.mozilla.fenix.helpers.TestAssetHelper.waitingTimeLong
import org.mozilla.fenix.helpers.TestHelper.mDevice
import org.mozilla.fenix.helpers.TestHelper.packageName
import org.mozilla.fenix.helpers.click
import org.mozilla.fenix.helpers.ext.waitNotNull
import org.mozilla.fenix.library.downloads.DownloadsListTestTag

/**
 * Implementation of Robot Pattern for download UI handling.
 */

class DownloadRobot {

    fun verifyDownloadPrompt(fileName: String) {
        var currentTries = 0
        while (currentTries++ < 3) {
            Log.i(TAG, "verifyDownloadPrompt: Started try #$currentTries")
            try {
                assertUIObjectExists(
                    itemWithResId("$packageName:id/download_button"),
                    itemContainingText(fileName),
                )

                break
            } catch (e: AssertionError) {
                Log.i(TAG, "verifyDownloadPrompt: AssertionError caught, executing fallback methods")
                Log.e("DOWNLOAD_ROBOT", "Failed to find locator: ${e.localizedMessage}")

                browserScreen {
                }.clickDownloadLink(fileName) {
                }
            }
        }
    }

    fun verifyDownloadCompleteNotificationPopup() =
        assertUIObjectExists(
            itemContainingText(getStringResource(R.string.mozac_feature_downloads_button_open)),
            itemContainingText(getStringResource(R.string.mozac_feature_downloads_completed_notification_text2)),
            itemWithResId("$packageName:id/download_dialog_filename"),
        )

    fun verifyDownloadFailedPrompt(fileName: String) =
        assertUIObjectExists(
            itemWithResId("$packageName:id/download_dialog_icon"),
            itemWithResIdContainingText(
                "$packageName:id/download_dialog_title",
                getStringResource(R.string.mozac_feature_downloads_failed_notification_text2),
            ),
            itemWithResIdContainingText(
                "$packageName:id/download_dialog_filename",
                fileName,
            ),
            itemWithResIdContainingText(
                "$packageName:id/download_dialog_action_button",
                getStringResource(R.string.mozac_feature_downloads_button_try_again),
            ),
            waitingTime = waitingTimeLong,
        )

    fun clickTryAgainButton() {
        Log.i(TAG, "clickTryAgainButton: Trying to click the \"TRY AGAIN\" in app prompt button")
        itemWithResIdAndText(
            "$packageName:id/download_dialog_action_button",
            "Try Again",
        ).click()
        Log.i(TAG, "clickTryAgainButton: Clicked the \"TRY AGAIN\" in app prompt button")
    }

    fun verifyPhotosAppOpens() = assertExternalAppOpens(GOOGLE_APPS_PHOTOS)

    fun verifyDownloadedFileName(fileName: String) =
        assertUIObjectExists(itemContainingText(fileName))

    fun openMultiSelectMoreOptionsMenu() {
        Log.i(TAG, "openMultiSelectMoreOptionsMenu: Trying to click multi-select more options button")
        itemWithDescription(getStringResource(R.string.content_description_menu)).click()
        Log.i(TAG, "openMultiSelectMoreOptionsMenu: Clicked multi-select more options button")
    }

    fun clickMultiSelectRemoveButton() {
        Log.i(TAG, "clickMultiSelectRemoveButton: Trying to click multi-select remove button")
        itemWithResIdContainingText("$packageName:id/title", "Remove").click()
        Log.i(TAG, "clickMultiSelectRemoveButton: Clicked multi-select remove button")
    }

    fun openPageAndDownloadFile(url: Uri, downloadFile: String) {
        navigationToolbar {
        }.enterURLAndEnterToBrowser(url) {
            waitForPageToLoad(pageLoadWaitingTime = waitingTimeLong)
        }.clickDownloadLink(downloadFile) {
            verifyDownloadPrompt(downloadFile)
        }.clickDownload {
        }
    }

    @OptIn(ExperimentalTestApi::class)
    fun verifyDownloadedFileExistsInDownloadsList(testRule: HomeActivityComposeTestRule, fileName: String) {
        Log.i(TAG, "verifyDownloadedFileName: Trying to verify that the downloaded file: $fileName is displayed")
        testRule.waitUntilAtLeastOneExists(
            hasTestTag("${DownloadsListTestTag.DOWNLOADS_LIST_ITEM}.$fileName"),
        )
        testRule.onNodeWithTag("${DownloadsListTestTag.DOWNLOADS_LIST_ITEM}.$fileName")
            .assertIsDisplayed()
        Log.i(TAG, "verifyDownloadedFileName: Trying to verify that the downloaded file: $fileName is displayed")
    }

    fun verifyEmptyDownloadsList(testRule: HomeActivityComposeTestRule) {
        Log.i(TAG, "verifyEmptyDownloadsList: Trying to verify that the \"No downloaded files\" list message is displayed")
        testRule.onNodeWithText(text = testRule.activity.getString(R.string.download_empty_message_1))
            .assertIsDisplayed()
        Log.i(TAG, "verifyEmptyDownloadsList: Verified that the \"No downloaded files\" list message is displayed")
    }

    fun deleteDownloadedItem(testRule: HomeActivityComposeTestRule, fileName: String) {
        Log.i(TAG, "deleteDownloadedItem: Trying to click the trash bin icon to delete downloaded file: $fileName")
        testRule.onNodeWithTag("${DownloadsListTestTag.DOWNLOADS_LIST_ITEM}.$fileName")
            .onChildren()
            .filter(hasContentDescription(testRule.activity.getString(R.string.download_delete_item_1)))
            .onFirst()
            .performClick()
        Log.i(TAG, "deleteDownloadedItem: Clicked the trash bin icon to delete downloaded file: $fileName")
    }

    fun clickDownloadedItem(testRule: HomeActivityComposeTestRule, fileName: String) {
        Log.i(TAG, "clickDownloadedItem: Trying to click downloaded file: $fileName")
        testRule.onNodeWithTag("${DownloadsListTestTag.DOWNLOADS_LIST_ITEM}.$fileName")
            .performClick()
        Log.i(TAG, "clickDownloadedItem: Clicked downloaded file: $fileName")
    }

    fun longClickDownloadedItem(testRule: HomeActivityComposeTestRule, title: String) {
        Log.i(TAG, "longClickDownloadedItem: Trying to long click downloaded file: $title")
        testRule.onNodeWithTag("${DownloadsListTestTag.DOWNLOADS_LIST_ITEM}.$title")
            .performTouchInput {
                longClick()
            }
        Log.i(TAG, "longClickDownloadedItem: Long clicked downloaded file: $title")
    }

    class Transition {
        fun clickDownload(interact: DownloadRobot.() -> Unit): Transition {
            Log.i(TAG, "clickDownload: Trying to click the \"Download\" download prompt button")
            downloadButton().click()
            Log.i(TAG, "clickDownload: Clicked the \"Download\" download prompt button")

            DownloadRobot().interact()
            return Transition()
        }

        fun closeDownloadPrompt(interact: BrowserRobot.() -> Unit): BrowserRobot.Transition {
            Log.i(TAG, "closeDownloadPrompt: Trying to click the close download prompt button")
            itemWithResId("$packageName:id/download_dialog_close_button").click()
            Log.i(TAG, "closeDownloadPrompt: Clicked the close download prompt button")

            BrowserRobot().interact()
            return BrowserRobot.Transition()
        }

        fun clickOpen(type: String, interact: BrowserRobot.() -> Unit): BrowserRobot.Transition {
            Log.i(TAG, "clickOpen: Waiting for $waitingTime ms for the for \"OPEN\" download prompt button to exist")
            openDownloadButton().waitForExists(waitingTime)
            Log.i(TAG, "clickOpen: Waited for $waitingTime ms for the for \"OPEN\" download prompt button to exist")
            Log.i(TAG, "clickOpen: Trying to click the \"OPEN\" download prompt button")
            openDownloadButton().click()
            Log.i(TAG, "clickOpen: Clicked the \"OPEN\" download prompt button")
            Log.i(TAG, "clickOpen: Trying to verify that the open intent is matched with associated data type")
            // verify open intent is matched with associated data type
            Intents.intended(
                allOf(
                    IntentMatchers.hasAction(Intent.ACTION_VIEW),
                    IntentMatchers.hasType(type),
                ),
            )
            Log.i(TAG, "clickOpen: Verified that the open intent is matched with associated data type")

            BrowserRobot().interact()
            return BrowserRobot.Transition()
        }

        fun clickAllowPermission(interact: DownloadRobot.() -> Unit): Transition {
            mDevice.waitNotNull(
                Until.findObject(By.res(getPermissionAllowID() + ":id/permission_allow_button")),
                waitingTime,
            )
            Log.i(TAG, "clickAllowPermission: Trying to click the \"ALLOW\" permission button")
            mDevice.findObject(By.res(getPermissionAllowID() + ":id/permission_allow_button")).click()
            Log.i(TAG, "clickAllowPermission: Clicked the \"ALLOW\" permission button")

            DownloadRobot().interact()
            return Transition()
        }

        fun exitDownloadsManagerToBrowser(interact: BrowserRobot.() -> Unit): BrowserRobot.Transition {
            Log.i(TAG, "exitDownloadsManagerToBrowser: Trying to click the navigate up toolbar button")
            onView(withContentDescription("Navigate up")).click()
            Log.i(TAG, "exitDownloadsManagerToBrowser: Clicked the navigate up toolbar button")

            BrowserRobot().interact()
            return BrowserRobot.Transition()
        }

        fun goBack(interact: HomeScreenRobot.() -> Unit): HomeScreenRobot.Transition {
            Log.i(TAG, "goBack: Trying to click the navigate up toolbar button")
            goBackButton().click()
            Log.i(TAG, "goBack: Clicked the navigate up toolbar button")

            HomeScreenRobot().interact()
            return HomeScreenRobot.Transition()
        }
    }
}

fun downloadRobot(interact: DownloadRobot.() -> Unit): DownloadRobot.Transition {
    DownloadRobot().interact()
    return DownloadRobot.Transition()
}

private fun downloadButton() =
    onView(withId(R.id.download_button))
        .check(matches(isDisplayed()))

private fun openDownloadButton() =
    mDevice.findObject(UiSelector().resourceId("$packageName:id/download_dialog_action_button"))

private fun goBackButton() = onView(withContentDescription("Navigate up"))
