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

//добавил еще импортов были ошибки

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import com.example.bluehits.ui.DebugButton
import com.example.bluehits.ui.RunButton
import com.example.bluehits.ui.TrashButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val textMeasurer = rememberTextMeasurer()
            val blocksManager = remember { BlocksManager() }

            Box(modifier = Modifier.fillMaxSize()) {
               DebugButton(
                    onClick = { /* ... */ },
                    modifier = Modifier
                        .offset(x = 707.dp, y = 24.5.dp) // ← Прямо как в Figma
                        .zIndex(1f)
                )
                RunButton(
                    onClick = { /* ... */ },
                    modifier = Modifier
                        .offset(x = 635.dp, y = 24.5.dp)
                        .zIndex(1f)
                )
                TrashButton(
                    onClick = { /* ... */ },
                    modifier = Modifier
                        .offset(x = 713.dp, y = 284.5.dp)

                        .zIndex(1f)
                )
                Column(modifier = Modifier.fillMaxSize()) {
                    createCanvas(
                        blocks = blocksManager.uiBlocks,
                        textMeasurer = textMeasurer,
                        onDrag = { /* Обработка перетаскивания */ },
                        onBlockDrag = { block, delta -> blocksManager.moveBlock(block, delta) }
                    )
                }
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