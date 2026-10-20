package com.torfilx.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Shared rounded geometry. Clip artwork at its edge; surfaces and controls draw their shape directly.
 * No blur, dynamic shadows, or runtime path generation is needed for these surfaces.
 */
object TorfilxShapes {
    /** Posters, stills, plates. */
    val Card: Shape = RoundedCornerShape(12.dp)

    /** Buttons, keys, fields. */
    val Control: Shape = RoundedCornerShape(10.dp)

    /** Dialogs, menus, overlays. */
    val Panel: Shape = RoundedCornerShape(20.dp)

    val Pill: Shape = RoundedCornerShape(50)
}
