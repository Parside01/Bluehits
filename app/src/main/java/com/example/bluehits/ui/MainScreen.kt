package com.example.bluehits.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.*
@Composable
fun MainScreen() {
    val textMeasurer = rememberTextMeasurer()
    val blocksManager = remember { BlocksManager() }
    var isPanelVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Add(
            onClick = { isPanelVisible = !isPanelVisible },
            modifier = Modifier
                .offset(x = 500.dp, y = 24.5.dp)
                .zIndex(1f)
        )

        DebugButton(
            onClick = { },
            modifier = Modifier
                .offset(x = 707.dp, y = 24.5.dp)
                .zIndex(1f)
        )
        RunButton(
            onClick = { },
            modifier = Modifier
                .offset(x = 635.dp, y = 24.5.dp)
                .zIndex(1f)
        )
        TrashButton(
            onClick = { },
            modifier = Modifier
                .offset(x = 713.dp, y = 284.5.dp)
                .zIndex(1f)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = isPanelVisible,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                ControlPanel(
                    blocksManager = blocksManager,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                createCanvas(
                    blocks = blocksManager.uiBlocks,
                    textMeasurer = textMeasurer,
                    onDrag = { },
                    onBlockDrag = { block, delta ->
                        blocksManager.moveBlock(block, delta)
                    }
                )
            }
        }
    }
}


@Composable
fun ControlPanel(
    blocksManager: BlocksManager,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(180.dp)
            .fillMaxHeight()
            .padding(16.dp)
            .background(
                color = Color(0xFFF9F9FF),
                shape = RoundedCornerShape(16.dp)
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )
    {
        val buttons = listOf(
            "Int" to "Int",
            "Add" to "Add",
            "Sub" to "Sub",
            "Print" to "Print",
            "Bool" to "Bool",
            "IfElse" to "IfElse"
        )

        buttons.forEach { (blockType, label) ->
            OutlinedButton(
                onClick = { blocksManager.addNewBlock(blockType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F3FF), shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF888888)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF3F3FF),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Black
                )
            }
        }
    }
}







