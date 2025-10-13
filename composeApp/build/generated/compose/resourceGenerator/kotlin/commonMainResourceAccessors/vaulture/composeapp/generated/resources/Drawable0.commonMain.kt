@file:OptIn(InternalResourceApi::class)

package vaulture.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/vaulture.composeapp.generated.resources/"

internal val Res.drawable.authentication: DrawableResource by lazy {
      DrawableResource("drawable:authentication", setOf(
        ResourceItem(setOf(), "${MD}drawable/authentication.png", -1, -1),
      ))
    }

internal val Res.drawable.compose_multiplatform: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform.xml", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("authentication", Res.drawable.authentication)
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
}
