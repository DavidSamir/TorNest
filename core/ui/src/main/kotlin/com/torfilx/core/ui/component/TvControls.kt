package com.torfilx.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.torfilx.core.common.log.TorfilxLog
import com.torfilx.core.ui.theme.TorfilxColors
import com.torfilx.core.ui.theme.TorfilxShapes
import com.torfilx.core.ui.theme.animateFocus
import com.torfilx.core.ui.theme.focusFrame
import com.torfilx.core.ui.theme.focusLift

/** Rounded, high-contrast action. Only the drawn child scales; the D-pad target never moves. */
@Composable
fun TvButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    focusRequester: FocusRequester? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val focus = animateFocus(focused && enabled, label = "buttonFocus")
    val requester = focusRequester ?: remember { FocusRequester() }
    AutoFocus(requester, autoFocus && enabled, text)
    val filled = enabled && (primary || focused)
    val fill = when {
        !enabled -> TorfilxColors.SurfaceLow
        filled -> TorfilxColors.Accent
        else -> TorfilxColors.SurfaceHigh
    }
    val content = when {
        !enabled -> TorfilxColors.TextDisabled
        filled -> TorfilxColors.TextOnAccent
        else -> TorfilxColors.TextPrimary
    }
    Box(
        modifier = modifier
            .focusRequester(requester)
            // clickable owns both focus and OK handling. A second focusable node would steal OK.
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onClick)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        Row(
            modifier = Modifier
                .focusLift(focus, 0.dp)
                .focusFrame(focus, 1.5.dp, 3.dp)
                .background(fill, TorfilxShapes.Control)
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            leadingIcon?.invoke()
            CapsText(text = text, color = content)
        }
    }
}

/** Shared action styling for heroes, detail screens, search history, and retry actions. */
@Composable
fun TvTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    arrow: Boolean = false,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    TvButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        primary = arrow,
        enabled = enabled,
        autoFocus = autoFocus,
        focusRequester = focusRequester,
    )
}

/** Selected and focused are separate: selection persists; a white pill marks the current D-pad target. */
@Composable
fun TvChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Box(
        modifier = modifier
            .background(
                when {
                    focused -> TorfilxColors.Accent
                    selected -> TorfilxColors.SurfaceHighest
                    else -> TorfilxColors.Transparent
                },
                TorfilxShapes.Pill,
            )
            .border(1.dp, if (focused) TorfilxColors.Focus else TorfilxColors.Transparent, TorfilxShapes.Pill)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .semantics { this.selected = selected; role = Role.Tab }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        CapsText(
            text = text,
            color = when {
                focused -> TorfilxColors.TextOnAccent
                selected -> TorfilxColors.TextPrimary
                else -> TorfilxColors.TextSecondary
            },
        )
    }
}

@Composable
private fun AutoFocus(requester: FocusRequester, enabled: Boolean, name: String) {
    LaunchedEffect(enabled) {
        if (enabled) {
            runCatching { requester.requestFocus() }
                .onFailure { TorfilxLog.w("TvControls", "autoFocus failed for '$name'") }
        }
    }
}
