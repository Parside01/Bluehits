package com.example.bluehits.ui.console

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Writer

class ConsoleBuffer {
    private val _lines = mutableStateListOf<String>()
    val lines: List<String> get() = _lines
    private val commandPrompt = "> "
    private var currentTextInPrompt = StringBuilder()

    init {
        _lines.add(commandPrompt)
    }

    fun append(text: String) {
        if (text.isEmpty()) return

        val textSegments = text.split("\n", "\r\n")

        currentTextInPrompt.append(textSegments[0])
        _lines[_lines.lastIndex] = commandPrompt + currentTextInPrompt.toString()

        for (i in 1 until textSegments.size) {
            currentTextInPrompt = StringBuilder()
            currentTextInPrompt.append(textSegments[i])
            _lines.add(commandPrompt + currentTextInPrompt.toString())
        }
    }

    fun clear() {
        _lines.clear()
        currentTextInPrompt = StringBuilder()
        _lines.add(commandPrompt)
    }
}

class ConsoleWriter(private val consoleBuffer: ConsoleBuffer) : Writer() {
    override fun write(cbuf: CharArray, off: Int, len: Int) {
        val text = String(cbuf, off, len)
        consoleBuffer.append(text)
    }
    override fun flush() {}
    override fun close() {}
}

@Composable
fun ConsoleUI(
    consoleBuffer: ConsoleBuffer,
    isConsoleVisible: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onConsoleBoundsChange: (Rect?) -> Unit
) {
    val offsetX by animateDpAsState(
        targetValue = if (isConsoleVisible.value) 0.dp else (-400).dp,
        animationSpec = tween(durationMillis = 300)
    )
    val listState = rememberLazyListState()
    LaunchedEffect(consoleBuffer.lines.size) {
        if (consoleBuffer.lines.isNotEmpty()) {
            listState.scrollToItem(consoleBuffer.lines.lastIndex)
        }
    }
    val cornerRadius = 8.dp
    val backgroundColor = Color.Gray
    val textColor = Color.White
    val shadowElevation = 4.dp
    val padding = 16.dp
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .offset(x = offsetX)
            .shadow(elevation = shadowElevation, shape = RoundedCornerShape(cornerRadius))
            .onGloballyPositioned { layoutCoordinates ->
                onConsoleBoundsChange(layoutCoordinates.boundsInRoot())
            },
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                state = listState,
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(consoleBuffer.lines) { line ->
                    Text(
                        text = line,
                        color = textColor,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}