package com.example.bluehits.ui


import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun StyledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyles.ButtonStyle = ButtonStyles.baseButtonStyle()
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.then(style.modifier),
        shape = style.shape,
        colors = style.colors,
        contentPadding = style.contentPadding,
        border = style.border
    ) {
        Text(text = text, style = style.textStyle)
    }
}
