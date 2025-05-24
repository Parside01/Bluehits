package com.example.bluehits.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

import com.example.bluehits.ui.editPanel.BlockEditManager
import com.example.bluehits.ui.editPanel.BlockEditPanel
import com.example.interpreter.models.Id
import com.example.interpreter.models.Program
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val blocksManager = remember { BlocksManager() }
    val connectionManager = remember { UIConnectionManager() }
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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    errorMessage?.let { message ->
        ErrorNotification(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }

    successMessage?.let { message ->
        SuccessNotification(
            message = message,
            onDismiss = { successMessage = null }
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
    ) {
        val (canvas, panel, addButton, debugButton, runButton, trashButton, clearButton, editPanel) = createRefs()

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
        ) {
            CreateCanvas(
                blocks = blocksManager.uiBlocks,
                textMeasurer = textMeasurer,
                connectionManager = connectionManager,
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
                            draggedBlock?.let { blocksManager.removeBlock(it, connectionManager) }
                        }
                        draggedBlock = null
                        isBlockOverTrash = false
                    }
                },
                onBlockClick = { blockId ->
                    selectedBlockId = blockId
                    showSettingsDialog = true
                    BlockEditManager.showEditPanel(blocksManager.uiBlocks.find { it.id == blockId }!!)
                },
                onConnectionError = { message ->
                    errorMessage = message
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
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut(),
            modifier = Modifier
                .constrainAs(panel) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(180.dp)
                }
                .zIndex(2f)
        ) {
            ControlPanel(
                blocksManager = blocksManager,
                modifier = Modifier
                    .background(color = Color(0xFFF9F9FF), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                onError = { message -> errorMessage = message }
            )
        }

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
                runProgram(
                    blocksManager,
                    context,
                    showError = { message -> errorMessage = message },
                    showSuccess = { message -> successMessage = message }
                )
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
            onClick = { blocksManager.clearAllBlocks(connectionManager) },
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
    modifier: Modifier = Modifier,
    onError: (String) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val buttons = listOf(
            "Main" to "Main",
            "Array" to "Array",
            "Int" to "Int",
            "Add" to "Add",
            "Sub" to "Sub",
            "Print" to "Print",
            "Bool" to "Bool",
            "Float" to "Float",
            "IfElse" to "IfElse",
            "For" to "For",
            "Index" to "Index",
            "Append" to "Append"
        )

        buttons.forEach { (blockType, label) ->
            StyledButton(
                text = label,
                onClick = { blocksManager.addNewBlock(blockType, onError) },
                style = ButtonStyles.controlPanelButtonStyle()
            )
        }
    }
}
@Composable
fun SuccessNotification(
    message: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000)
        visible = false
        delay(300)
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
    ) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(Color(0xFF6C6C6C))
                    .padding(24.dp)
                    .wrapContentSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ErrorNotification(
    message: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000)
        visible = false
        delay(300) // Allow animation to complete
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
    ) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(Color(0xFFC04D4D))
                    .padding(24.dp)
                    .wrapContentSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

fun runProgram(
    blocksManager: BlocksManager,
    context: Context,
    showError: (String) -> Unit,
    showSuccess: (String) -> Unit
) {
    if (blocksManager.uiBlocks.size <= 1) {
        showError("Программа пуста: добавьте блоки и соединения")
        return
    }
    if (blocksManager.uiBlocks.none { it.title == "Main" }) {
        showError("Ошибка: блок Main не найден")
        return
    }
    try {
        Program.run()
        val printValue = blocksManager.getPrintBlockValue(blocksManager.uiBlocks)
        showSuccess("Вывод: ${printValue ?: "не определено"}")
    } catch (e: Exception) {
        showError("Ошибка при выполнении программы: ${e.message}")
    }
}