package com.example.bluehits.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.layout.positionInRoot
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlin.math.min

@Composable
fun MainScreen() {
    val textMeasurer = rememberTextMeasurer()
    val blocksManager = remember { BlocksManager() }
    var isPanelVisible by remember { mutableStateOf(false) }
    var draggedBlock by remember { mutableStateOf<BlueBlock?>(null) }
    var trashBounds by remember { mutableStateOf<Rect?>(null) }
    var isBlockOverTrash by remember { mutableStateOf(false) }
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    val config = LocalConfiguration.current
    val baseDimension = min(config.screenWidthDp, config.screenHeightDp).dp
    val density = LocalDensity.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
    ) {
        val (canvas, panel, addButton, debugButton, runButton, trashButton, clearButton) = createRefs()

        Column(
            modifier = Modifier
                .constrainAs(canvas) {
                    start.linkTo(if (isPanelVisible) panel.end else parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .zIndex(0f)
        ) {
            CreateCanvas(
                blocks = blocksManager.uiBlocks,
                textMeasurer = textMeasurer,
                onDrag = { dragAmount ->
                    canvasOffset += dragAmount
                },
                onBlockDrag = { block, dragAmount, isDragging ->
                    if (isDragging) {
                        draggedBlock = block
                        blocksManager.moveBlock(block, dragAmount)

                        trashBounds?.let { bounds ->
                            val blockRect = Rect(
                                left = block.x + canvasOffset.x,
                                top = block.y + canvasOffset.y,
                                right = block.x + block.width + canvasOffset.x,
                                bottom = block.y + block.height + canvasOffset.y
                            )
                            isBlockOverTrash = bounds.overlaps(blockRect)
                        }
                    } else {
                        if (isBlockOverTrash) {
                            draggedBlock?.let { blocksManager.removeBlock(it) }
                        }
                        draggedBlock = null
                        isBlockOverTrash = false
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = isPanelVisible,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut(),
            modifier = Modifier.constrainAs(panel) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.value(180.dp)
                height = Dimension.fillToConstraints
            }
        ) {
            ControlPanel(
                blocksManager = blocksManager,
                modifier = Modifier
                    .background(
                        color = Color(0xFFF9F9FF),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
        }

        StyledButton(
            text = "Add",
            onClick = { isPanelVisible = !isPanelVisible },
            modifier = Modifier.constrainAs(addButton) {
                end.linkTo(parent.end, margin = baseDimension * 0.05f)
                bottom.linkTo(parent.bottom, margin = baseDimension * 0.05f)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        )

        StyledButton(
            text = "Debug",
            onClick = {},
            modifier = Modifier.constrainAs(debugButton) {
                end.linkTo(parent.end, margin = baseDimension * 0.05f)
                top.linkTo(parent.top, margin = baseDimension * 0.05f)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        )

        StyledButton(
            text = "Run",
            onClick = {},
            modifier = Modifier.constrainAs(runButton) {
                end.linkTo(debugButton.start, margin = baseDimension * 0.02f)
                top.linkTo(parent.top, margin = baseDimension * 0.05f)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        )

        StyledButton(
            text = "Trash",
            onClick = {},
            style = ButtonStyles.baseButtonStyle().copy(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isBlockOverTrash) Color.Red else Color.White,
                    contentColor = Color.Black
                )
            ),
            modifier = Modifier
                .constrainAs(trashButton) {
                    end.linkTo(clearButton.start, margin = baseDimension * 0.02f)
                    top.linkTo(debugButton.bottom, margin = baseDimension * 0.03f)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .onGloballyPositioned { coordinates ->
                    with(density) {
                        val position = coordinates.positionInRoot()
                        trashBounds = Rect(
                            left = position.x,
                            top = position.y,
                            right = position.x + coordinates.size.width.toFloat(),
                            bottom = position.y + coordinates.size.height.toFloat()
                        )
                    }
                }
        )

        StyledButton(
            text = "Clear",
            onClick = { blocksManager.clearAllBlocks() },
            style = ButtonStyles.baseButtonStyle().copy(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ),
            modifier = Modifier.constrainAs(clearButton) {
                end.linkTo(parent.end, margin = baseDimension * 0.05f)
                top.linkTo(debugButton.bottom, margin = baseDimension * 0.03f)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        )
    }
}

@Composable
fun ControlPanel(
    blocksManager: BlocksManager,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val buttons = listOf(
            "Int" to "Int",
            "Add" to "Add",
            "Sub" to "Sub",
            "Print" to "Print",
            "Bool" to "Bool",
            "IfElse" to "IfElse"
        )

        buttons.forEach { (blockType, label) ->
            StyledButton(
                text = label,
                onClick = { blocksManager.addNewBlock(blockType) },
                style = ButtonStyles.controlPanelButtonStyle()
            )
        }
    }
}