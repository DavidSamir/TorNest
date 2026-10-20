package com.torfilx.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Typography
import com.torfilx.core.ui.R

/** One locally bundled sans family across the entire TV interface, including dialogs and playback. */
object TorfilxFonts {
    // OptionalLocal preserves the system-font fallback if a legacy device cannot load its font cache.
    val Sans = FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal, loadingStrategy = FontLoadingStrategy.OptionalLocal),
        Font(R.font.inter_semibold, FontWeight.SemiBold, loadingStrategy = FontLoadingStrategy.OptionalLocal),
    )
}

/** Screen-role names stay stable so every feature receives the same typography without local overrides. */
object TorfilxType {
    private val body = TextStyle(
        fontFamily = TorfilxFonts.Sans,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.em,
    )
    private val strong = body.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.02).em)

    val Masthead = strong.copy(fontSize = 26.sp, lineHeight = 32.sp)
    val Display = strong.copy(fontSize = 48.sp, lineHeight = 54.sp)
    val DisplayCompact = Display.copy(fontSize = 38.sp, lineHeight = 44.sp)
    fun displayFor(title: String): TextStyle = if (title.length > 26) DisplayCompact else Display
    val Headline = strong.copy(fontSize = 32.sp, lineHeight = 38.sp)
    val Deck = body.copy(fontSize = 18.sp, lineHeight = 25.sp)
    val Reading = body.copy(fontSize = 18.sp, lineHeight = 27.sp)
    val KickerCaps = strong.copy(fontSize = 13.sp, lineHeight = 19.sp, letterSpacing = 0.em)
    val LabelCaps = strong.copy(fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = (-0.01).em)
    val MetaCaps = body.copy(fontSize = 12.sp, lineHeight = 18.sp)
    val SectionTitle = strong.copy(fontSize = 24.sp, lineHeight = 30.sp)
    val SectionNumeral = strong.copy(fontSize = 18.sp, lineHeight = 24.sp)
    val CardTitle = strong.copy(fontSize = 16.sp, lineHeight = 22.sp)
    val Numeral = strong.copy(fontSize = 48.sp, lineHeight = 52.sp)
    val Caption = body.copy(fontSize = 14.sp, lineHeight = 21.sp)
    val ListTitle = strong.copy(fontSize = 19.sp, lineHeight = 26.sp)
    val Small = body.copy(fontSize = 14.sp, lineHeight = 21.sp)
}

internal val TorfilxTypography = Typography(
    displayLarge = TorfilxType.Display,
    displayMedium = TorfilxType.DisplayCompact,
    displaySmall = TorfilxType.Headline,
    headlineLarge = TorfilxType.Headline,
    headlineMedium = TorfilxType.Headline.copy(fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TorfilxType.SectionTitle,
    titleLarge = TorfilxType.SectionTitle,
    titleMedium = TorfilxType.ListTitle,
    titleSmall = TorfilxType.CardTitle,
    bodyLarge = TorfilxType.Reading,
    bodyMedium = TorfilxType.Reading.copy(fontSize = 17.sp, lineHeight = 25.sp),
    bodySmall = TorfilxType.Small,
    labelLarge = TorfilxType.LabelCaps,
    labelMedium = TorfilxType.Caption,
    labelSmall = TorfilxType.MetaCaps,
)
