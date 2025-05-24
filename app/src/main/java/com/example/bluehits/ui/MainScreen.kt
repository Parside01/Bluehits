package com.example.bluehits.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.bluehits.ui.console.ConsoleBuffer
import com.example.bluehits.ui.console.ConsoleUI
import com.example.bluehits.ui.console.ConsoleWriteAdapter
import com.example.bluehits.ui.console.ConsoleWriter
import com.example.bluehits.ui.editPanel.BlockEditManager
import com.example.bluehits.ui.editPanel.BlockEditPanel
import com.example.interpreter.models.Id
import com.example.interpreter.models.Program
import java.io.PrintStream
import kotlin.math.min

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val blocksManager = remember { BlocksManager() }
    var isPanelVisible by remember { mutableStateOf(false) }
    var draggedBlock by remember { mutableStateOf<BlueBlock?>(null) }
    var trashBounds by remember { mutableStateOf<Rect?>(null) }
    var isBlockOverTrash by remember { mutableStateOf(false) }
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedBlockId by remember { mutableStateOf<Id?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val config = LocalConfiguration.current
    val baseDimension = min(config.screenWidthDp, config.screenHeightDp).dp
    val density = LocalDensity.current

    val consoleBuffer = remember { ConsoleBuffer() }
    val isConsoleVisible = remember { mutableStateOf(false) }
    var consoleBounds by remember { mutableStateOf<Rect?>(null) }
    var panelBounds by remember { mutableStateOf<Rect?>(null) }

    DisposableEffect(Unit) {
        val oldOut = System.out
        val oldErr = System.err
        val consoleWriter = ConsoleWriter(consoleBuffer)
        val outputStream = ConsoleWriteAdapter(consoleWriter)
        System.setOut(PrintStream(outputStream, true))
        System.setErr(PrintStream(outputStream, true))

        onDispose {
            System.setOut(oldOut)
            System.setErr(oldErr)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .pointerInput(isConsoleVisible.value || isPanelVisible) {
                detectTapGestures { tapOffset ->
                    if (isConsoleVisible.value && consoleBounds?.contains(tapOffset) == false) {
                        isConsoleVisible.value = false
                    }
                    if (isPanelVisible && panelBounds?.contains(tapOffset) == false) {
                        isPanelVisible = false
                    }
                }
            }
    ) {
        val (canvas, panel, addButton, debugButton, runButton, trashButton, clearButton, editPanel, consoleUi, consoleButton) = createRefs()

        Column(
            modifier = Modifier
                .constrainAs(canvas) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .zIndex(0f)
                .fillMaxSize()
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
                },
                onBlockClick = { blockId ->
                    selectedBlockId = blockId
                    showSettingsDialog = true
                    BlockEditManager.showEditPanel(blocksManager.uiBlocks.find { it.id == blockId }!!)
                }
            )
        }

        BlockEditPanel(
            modifier = Modifier
                .constrainAs(editPanel) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(1f)
        )

        AnimatedVisibility(
            visible = isPanelVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
            modifier = Modifier
                .constrainAs(panel) {
                    start.linkTo(parent.start) // Привязываем к левому краю
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(180.dp)
                }
                .zIndex(2f)
                .onGloballyPositioned { layoutCoordinates ->
                    panelBounds = layoutCoordinates.boundsInRoot()
                }
        ) {
            ControlPanel(
                blocksManager = blocksManager,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF9F9FF), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
        }

        ConsoleUI(
            consoleBuffer = consoleBuffer,
            isConsoleVisible = isConsoleVisible,
            modifier = Modifier
                .constrainAs(consoleUi) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    height = Dimension.fillToConstraints
                }
                .zIndex(4f)
                .onGloballyPositioned { layoutCoordinates ->
                    consoleBounds = layoutCoordinates.boundsInRoot()
                },
            onConsoleBoundsChange = { newBounds ->
                consoleBounds = newBounds
            }
        )

        StyledButton(
            text = "Add",
            onClick = { isPanelVisible = !isPanelVisible },
            modifier = Modifier
                .constrainAs(addButton) {
                    end.linkTo(parent.end, margin = baseDimension * 0.05f)
                    bottom.linkTo(parent.bottom, margin = baseDimension * 0.05f)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(3f)
        )

        StyledButton(
            text = "Console",
            onClick = { isConsoleVisible.value = !isConsoleVisible.value },
            modifier = Modifier
                .constrainAs(consoleButton) {
                    end.linkTo(addButton.start, margin = baseDimension * 0.02f)
                    bottom.linkTo(addButton.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(3f)
        )


        StyledButton(
            text = "Debug",
            onClick = {},
            modifier = Modifier
                .constrainAs(debugButton) {
                    end.linkTo(parent.end, margin = baseDimension * 0.05f)
                    top.linkTo(parent.top, margin = baseDimension * 0.05f)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(3f)
        )

        StyledButton(
            text = "Run",
            onClick = {
                try {
                    Program.run()
                    val printValue = blocksManager.getPrintBlockValue(blocksManager.uiBlocks)
                    showToast(context, "Вывод: ${printValue ?: "не определено"}")
                } catch (e :Exception) {
                    println(e)
                }
            },
            modifier = Modifier
                .constrainAs(runButton) {
                    end.linkTo(debugButton.start, margin = baseDimension * 0.02f)
                    top.linkTo(parent.top, margin = baseDimension * 0.05f)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(3f)
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
                .zIndex(3f)
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
            modifier = Modifier
                .constrainAs(clearButton) {
                    end.linkTo(parent.end, margin = baseDimension * 0.05f)
                    top.linkTo(debugButton.bottom, margin = baseDimension * 0.03f)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(3f)
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
            "Array" to "Array",
            "Int" to "Int",
            "Add" to "Add",
            "Sub" to "Sub",
            "Print" to "Print",
            "Bool" to "Bool",
            "Float" to "Float",
            "IfElse" to "IfElse",
            "For" to "For"
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

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}