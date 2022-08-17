/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.wallpapers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.fenix.wallpapers.Wallpaper.Companion.getLocalPath
import java.io.File

class WallpaperFileManager(
    private val rootDirectory: File,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val scope = CoroutineScope(coroutineDispatcher)
    private val wallpapersDirectory = File(rootDirectory, "wallpapers")

    /**
     * Lookup all the files for a wallpaper name. This lookup will fail if there are not
     * files for each of a portrait and landscape orientation as well as a thumbnail.
     */
    suspend fun lookupExpiredWallpaper(name: String): Wallpaper? = withContext(Dispatchers.IO) {
        if (getAllLocalWallpaperPaths(name).all { File(rootDirectory, it).exists() }) {
            Wallpaper(
                name = name,
                collection = Wallpaper.DefaultCollection,
                textColor = null,
                cardColor = null,
            )
        } else null
    }

    private fun getAllLocalWallpaperPaths(name: String): List<String> =
        Wallpaper.ImageType.values().map { orientation ->
            getLocalPath(orientation, name)
        }

    /**
     * Remove all wallpapers that are not the [currentWallpaper] or in [availableWallpapers].
     */
    fun clean(currentWallpaper: Wallpaper, availableWallpapers: List<Wallpaper>) {
        scope.launch {
            val wallpapersToKeep = (listOf(currentWallpaper) + availableWallpapers).map { it.name }
            for (file in wallpapersDirectory.listFiles()?.toList() ?: listOf()) {
                if (file.isDirectory && !wallpapersToKeep.contains(file.name)) {
                    file.deleteRecursively()
                }
            }
        }
    }
}
