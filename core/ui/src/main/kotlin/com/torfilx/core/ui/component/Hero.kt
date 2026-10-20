package com.torfilx.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.torfilx.core.model.MediaCard
import com.torfilx.core.model.MediaItem
import com.torfilx.core.ui.focus.bringIntoViewAsWhole
import com.torfilx.core.ui.image.Artwork
import com.torfilx.core.ui.theme.LocalTorfilxDimens
import com.torfilx.core.ui.theme.TorfilxColors
import com.torfilx.core.ui.theme.TorfilxType
import com.torfilx.core.ui.util.Format

private val HERO_HEIGHT = 350.dp

/** A cinematic feature with manually selected artwork: idle screens do not keep decoding new images. */
@Composable
fun HeroSection(
    items: List<MediaCard>,
    primaryActionLabel: (MediaCard) -> String,
    onPlay: (MediaCard) -> Unit,
    onMoreInfo: (MediaCard) -> Unit,
    onToggleMyList: (MediaCard) -> Unit,
    modifier: Modifier = Modifier,
    playFocusRequester: FocusRequester? = null,
) {
    if (items.isEmpty()) return
    val dimens = LocalTorfilxDimens.current
    var index by remember(items.size) { mutableIntStateOf(0) }
    val position = index.coerceIn(items.indices)
    val current = items[position]
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HERO_HEIGHT)
            .bringIntoViewAsWhole()
            .focusGroup(),
    ) {
        CinematicBackdrop(item = current.item, height = HERO_HEIGHT)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = dimens.overscanHorizontal)
                .width(540.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Kicker(text = "Featured · " + kindAndYear(current.item))
            Text(
                text = current.item.title,
                style = TorfilxType.displayFor(current.item.title),
                color = TorfilxColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            deckFor(current).takeIf { it.isNotEmpty() }?.let { deck ->
                Text(text = deck, style = TorfilxType.Deck, color = TorfilxColors.TextSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            (current.episode?.overview ?: current.item.overview)?.takeIf { it.isNotBlank() }?.let { overview ->
                Text(text = overview, style = TorfilxType.Reading, color = TorfilxColors.TextSecondary,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 10.dp),
            ) {
                TvButton(text = primaryActionLabel(current), onClick = { onPlay(current) },
                    focusRequester = playFocusRequester)
                TvButton(text = "Details", onClick = { onMoreInfo(current) }, primary = false)
                TvButton(text = if (current.inMyList) "✓ My List" else "+ My List",
                    onClick = { onToggleMyList(current) }, primary = false)
            }
        }
        if (items.size > 1) {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(end = dimens.overscanHorizontal, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "${position + 1} / ${items.size}", style = TorfilxType.MetaCaps,
                    color = TorfilxColors.TextSecondary)
                TvButton(text = "Next feature", primary = false, onClick = { index = (position + 1) % items.size })
            }
        }
    }
}

/** One bounded image plus two cached gradients. No blur, video preview, or offscreen image carousel. */
@Composable
fun CinematicBackdrop(item: MediaItem, height: Dp, modifier: Modifier = Modifier) {
    val scrim = remember {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            0f to TorfilxColors.Background,
            0.50f to TorfilxColors.Background.copy(alpha = 0.94f),
            0.80f to TorfilxColors.Background.copy(alpha = 0.60f),
            1f to TorfilxColors.Background.copy(alpha = 0.12f),
        )
    }
    val floor = remember {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            0f to TorfilxColors.Transparent,
            0.7f to TorfilxColors.Transparent,
            1f to TorfilxColors.Background,
        )
    }
    Box(modifier.fillMaxWidth().height(height)) {
        Artwork(
            url = item.images.backdrop ?: item.images.thumb ?: item.images.poster,
            title = item.title,
            seed = item.id,
            showGeneratedLabel = false,
            widthDp = 640.dp,
            heightDp = height,
            modifier = Modifier.fillMaxWidth(0.78f).height(height).align(Alignment.CenterEnd),
        )
        Box(Modifier.matchParentSize().background(scrim))
        Box(Modifier.matchParentSize().background(floor))
    }
}

/** The spaced line above a title, led by a short rule. */
@Composable
fun Kicker(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CapsText(text = text, style = TorfilxType.KickerCaps, color = TorfilxColors.TextTertiary)
    }
}

/** "Series, 2008", "Film, 1921", or just the kind when the year is unknown. */
fun kindAndYear(item: MediaItem): String {
    val kind = if (item.isShow) "Series" else "Film"
    return item.year?.let { "$kind, $it" } ?: kind
}

/**
 * The italic line under the hero's title: for an episode, which one; otherwise the genres and how long
 * it runs — "Crime, Drama, Thriller — 5 seasons".
 */
internal fun deckFor(card: MediaCard): String {
    card.episode?.let { return "${it.code} — ${it.displayName}" }
    val item = card.item
    val length = if (item.isShow) Format.showLength(item) else Format.runtime(item.runtimeMs)
    return listOf(item.genres.take(3).joinToString(", "), length)
        .filter { it.isNotEmpty() }
        .joinToString(" — ")
}
