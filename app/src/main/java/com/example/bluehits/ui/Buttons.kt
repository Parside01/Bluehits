package com.example.bluehits.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun DebugButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .size(width = 75.dp, height = 35.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White // фон
        ),
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 1.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = "Debug",
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun RunButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .size(width = 62.dp, height = 35.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 1.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = "Run",
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun TrashButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .size(width = 69.dp, height = 35.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 1.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = "Trash",
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun Add(
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .size(width = 75.dp, height = 35.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White // фон
        ),
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 1.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = "add",
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}