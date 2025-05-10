package com.example.bluehits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.bluehits.ui.BlocksManager
import com.example.bluehits.ui.createCanvas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val textMeasurer = rememberTextMeasurer()
            val blocksManager = remember { BlocksManager() }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ControlPanel(
                    blocksManager = blocksManager,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                createCanvas(
                    blocks = blocksManager.uiBlocks,
                    textMeasurer = textMeasurer,
                    onDrag = {  },
                    onBlockDrag = { block, delta ->
                        blocksManager.moveBlock(block, delta)
                    }
                )
            }
        }
    }
}

@Composable
private fun ControlPanel(
    blocksManager: BlocksManager,
    modifier: Modifier = Modifier
) {
    HorizontalScrollableRow(
        modifier = modifier,
    ) {
        Button(onClick = { blocksManager.addNewBlock("Int") }) {
            Text("Добавить Int")
        }
        Button(onClick = { blocksManager.addNewBlock("Add") }) {
            Text("Добавить Add")
        }
        Button(onClick = { blocksManager.addNewBlock("Sub") }) {
            Text("Добавить Sub")
        }
        Button(onClick = { blocksManager.addNewBlock("Print") }) {
            Text("Добавить Print")
        }
        Button(onClick = { blocksManager.addNewBlock("Bool") }) {
            Text("Добавить Bool")
        }
        Button(onClick = { blocksManager.addNewBlock("IfElse") }) {
            Text("Добавить IfElse")
        }
    }
}

@Composable
fun HorizontalScrollableRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.horizontalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}