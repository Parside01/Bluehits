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
    private var currentLineBuffer = StringBuilder()
    init {
        _lines.add(commandPrompt)
    }
    fun append(text: String) {
        val parts = text.split("\n", "\r\n")
        if (parts.isNotEmpty()) {
            currentLineBuffer.append(parts[0])
            _lines[_lines.lastIndex] = currentLineBuffer.toString()
            for (i in 1 until parts.size) {
                if (currentLineBuffer.isNotEmpty()) {
                    _lines.add(currentLineBuffer.toString())
                }
                currentLineBuffer = StringBuilder()
                if (i < parts.size - 1) {
                    _lines.add(commandPrompt)
                } else {
                    currentLineBuffer.append(parts[i])
                    _lines.add(commandPrompt + currentLineBuffer.toString())
                }
            }
            if (parts.last().isEmpty() && parts.size > 1) {
                _lines.add(commandPrompt)
                currentLineBuffer = StringBuilder()
            }
        }
    }
    fun clear() {
        _lines.clear()
        currentLineBuffer = StringBuilder()
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
        targetValue = if (isConsoleVisible.value) 0.dp else 400.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val listState = rememberLazyListState()
    LaunchedEffect(consoleBuffer.lines.size) {
        if (consoleBuffer.lines.isNotEmpty()) {
            listState.scrollToItem(consoleBuffer.lines.lastIndex)
        }
    }
    val cornerRadius = 8.dp
    val backgroundColor = Color(0xFFE0E0E0)
    val textColor = Color(0xFFFFFFFF)
    val shadowElevation = 4.dp
    val padding = 16.dp
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(400.dp)
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
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
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