package com.torfilx.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.Text
import com.torfilx.core.ui.theme.TorfilxType

/**
 * A shared label in its authored case. The legacy function name is retained for feature callers.
 * Visible and accessible labels match, with no uppercase transformation or expanded letter spacing.
 */
@Composable
fun CapsText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = TorfilxType.LabelCaps,
    maxLines: Int = 1,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        modifier = modifier.clearAndSetSemantics { this.text = AnnotatedString(text) },
    )
}
