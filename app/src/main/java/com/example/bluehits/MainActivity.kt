package com.example.bluehits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.text.rememberTextMeasurer
import com.example.bluehits.ui.BlocksManager
import com.example.bluehits.ui.createCanvas
import com.example.bluehits.ui.createSampleBlocks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val textMeasurer = rememberTextMeasurer()
            val blocksManager = remember { BlocksManager() }

            createSampleBlocks(blocksManager)

            createCanvas(
                blocks = blocksManager.blocks,
                textMeasurer = textMeasurer,
                onDrag = { },
                onBlockDrag = { block, delta ->
                    blocksManager.moveBlock(block, delta)
                }
            )
        }
    }
}