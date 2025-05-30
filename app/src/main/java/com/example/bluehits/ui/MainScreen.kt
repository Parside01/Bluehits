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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.core.content.ContextCompat.getString
import com.example.bluehits.ui.console.ConsoleBuffer
import com.example.bluehits.ui.console.ConsoleUI
import com.example.bluehits.ui.console.ConsoleWriteAdapter
import com.example.bluehits.ui.console.ConsoleWriter
import com.example.bluehits.ui.editPanel.BlockEditManager
import com.example.bluehits.ui.editPanel.BlockEditPanel
import com.example.interpreter.models.Id
import com.example.interpreter.models.Program
import kotlinx.coroutines.CancellationException
import java.io.PrintStream
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import com.example.bluehits.R
import com.example.bluehits.ui.theme.*

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val blocksManager = remember { BlocksManager(context) }
    val connectionManager = remember { UIConnectionManager }
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
    var isDeleteMode by remember { mutableStateOf(false) }

    // Для асинхронного запуска программы.
    var isProgramRunning by remember { mutableStateOf(false) }
    val programScope = rememberCoroutineScope()
    var programJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    errorMessage?.let { message ->
        ErrorNotification(
            message = message,
            onDismiss = { errorMessage = null },
            context
        )
    }
    successMessage?.let { message ->
        SuccessNotification(
            message = message,
            onDismiss = { successMessage = null },
            context
        )
    }

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

    if (blocksManager.showTypeDialog.value) {
        TypeSelectionDialog(
            onTypeSelected = { type ->
                blocksManager.onTypeSelected(type)
            },
            onDismiss = {
                blocksManager.dismissTypeDialog()
            }
        )
    }

    if (blocksManager.showFunctionNameDialog.value) {
        FunctionNameDialog(
            title = when (blocksManager.currentFunctionDialogType) {
                getString(context, R.string.function_def_block_label) -> getString(context, R.string.define_function)
                getString(context, R.string.function_call_block_label) -> getString(context, R.string.call_function)
                getString(context, R.string.function_return_block_label) -> getString(context, R.string.return_from_function)
                getString(context, R.string.int_block_label) -> getString(context, R.string.enter_int_name)
                getString(context, R.string.float_block_label) -> getString(context, R.string.enter_float_name)
                getString(context, R.string.bool_block_label) -> getString(context, R.string.enter_bool_name)
                getString(context, R.string.string_block_label) -> getString(context, R.string.enter_string_name)
                else -> getString(context, R.string.enter_function_name)
            },
            label = when (blocksManager.currentFunctionDialogType) {
                getString(context, R.string.int_block_label) -> getString(context, R.string.int_name_label)
                getString(context, R.string.float_block_label) -> getString(context, R.string.float_name_label)
                getString(context, R.string.bool_block_label) -> getString(context, R.string.bool_name_label)
                getString(context, R.string.string_block_label) -> getString(context, R.string.string_name_label)
                else -> getString(context, R.string.function_name_label)
            },
            onNameEntered = { name ->
                blocksManager.onFunctionNameEntered(name)
            },
            onDismiss = {
                blocksManager.dismissFunctionNameDialog()
            },
            onError = { message -> errorMessage = message },
            context=context
        )
    }

    if (blocksManager.showFunctionSelectionDialog.value) {
        FunctionSelectionDialog(
            functions = blocksManager.getAvailableFunctions(),
            onFunctionSelected = { blocksManager.onFunctionSelected(it) },
            onDismiss = { blocksManager.dismissFunctionSelectionDialog() },
            context=context
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MainScreenConstraintLayoutBackground)
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
        val (canvas, panel, sidePanel, addButton, debugButton, runButton, trashButton, clearButton, editPanel, consoleUi, consoleButton) = createRefs()

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
                            if (block.title != getString(context, R.string.main_block_title)) {
                                draggedBlock?.let { blocksManager.removeBlock(it, connectionManager) }
                            }
                        }
                        draggedBlock = null
                        isBlockOverTrash = false
                    }
                },
                onBlockClick = { blockId ->
                    if (isDeleteMode) {
                        blocksManager.uiBlocks.find { it.id == blockId }?.let { block ->
                            if (block.title != "Main") {
                                blocksManager.removeBlock(block, connectionManager)
                            }
                        }
                    } else {
                        selectedBlockId = blockId
                        showSettingsDialog = true
                        BlockEditManager.showEditPanel(blocksManager.uiBlocks.find { it.id == blockId }!!)
                    }
                },
                connectionManager = connectionManager,
                blocksManager = blocksManager,
                showError = { message -> errorMessage = message },
                context=context
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(sidePanel) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(56.dp)
                }
                .zIndex(3f)
                .background(RightPanelColor)
                .padding(vertical = 8.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(
                    onClick = {
                        runProgram(
                            blocksManager,
                            context,
                            showError = { message -> errorMessage = message },
                            showSuccess = { message -> successMessage = message },
                            onProgramStopped = { Program.stop() },
                            programScope = programScope
                        )
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Run",
                        tint = RunIconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        isPanelVisible = !isPanelVisible
                        isConsoleVisible.value = false
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = AddButtonColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        isConsoleVisible.value = !isConsoleVisible.value
                        isPanelVisible = false
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = "Console",
                        tint = ConsoleButtonColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { isDeleteMode = !isDeleteMode
                        if (!isDeleteMode) {
                            isBlockOverTrash = false
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Trash",
                        tint = if (isDeleteMode) RedClassic else WhiteClassic,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { blocksManager.clearAllBlocks() },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Recycling,
                        contentDescription = "Clear",
                        tint = ClearButtonColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        BlockEditPanel(
            blocksManager = blocksManager,
            modifier = Modifier
                .constrainAs(editPanel) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                    height = Dimension.wrapContent
                }
                .zIndex(1f),
            context = context
        )

        AnimatedVisibility(
            visible = isPanelVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
            modifier = Modifier
                .constrainAs(panel) {
                    start.linkTo(parent.start)
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
                    .background(color = ControlPanelBackground)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                onError = { message -> errorMessage = message },
                context
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
    }
}

@Composable
fun ControlPanel(
    blocksManager: BlocksManager,
    modifier: Modifier = Modifier,
    onError: (String) -> Unit = {},
    context: Context
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val buttons = listOf(
            getString(context, R.string.int_block_label) to getString(context, R.string.int_block_label),
            getString(context, R.string.float_block_label) to getString(context, R.string.float_block_label),
            getString(context, R.string.bool_block_label) to getString(context, R.string.bool_block_label),
            getString(context, R.string.add_block_label) to getString(context, R.string.add_block_label),
            getString(context, R.string.sub_block_label) to getString(context, R.string.sub_block_label),
            getString(context, R.string.greater_block_label) to getString(context, R.string.greater_block_label),
            getString(context, R.string.ifelse_block_label) to getString(context, R.string.ifelse_block_label),
            getString(context, R.string.for_block_label) to getString(context, R.string.for_block_label),
            getString(context, R.string.array_block_label) to getString(context, R.string.array_block_label),
            getString(context, R.string.string_block_label) to getString(context, R.string.string_block_label),
            getString(context, R.string.math_block_label) to getString(context, R.string.math_block_label),
            getString(context, R.string.index_block_label) to getString(context, R.string.index_block_label),
            getString(context, R.string.append_block_label) to getString(context, R.string.append_block_label),
            getString(context, R.string.swap_block_label) to getString(context, R.string.swap_block_label),
            getString(context, R.string.print_block_label) to getString(context, R.string.print_block_label),
            getString(context, R.string.function_def_block_label) to getString(context, R.string.function_def_block_label),
            getString(context, R.string.function_call_block_label) to getString(context, R.string.function_call_block_label),
            getString(context, R.string.function_return_block_label) to getString(context, R.string.function_return_block_label)
        )

        buttons.forEach { (blockType, label) ->
            StyledButton(
                text = label,
                {
                    try {
                        blocksManager.addNewBlock(blockType)
                    } catch (e :Exception) {
                        println(e.stackTraceToString())
                        onError(e.message?:getString(context, R.string.default_error))
                    }
                },
                style = ButtonStyles.controlPanelButtonStyle()
            )
        }
    }
}

@Composable
fun SuccessNotification(
    message: String,
    onDismiss: () -> Unit,
    context: Context
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
                    .background(SuccessNotificationBackground)
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
                            color = WhiteClassic,
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
                            .background(SuccessNotificationBoxBackground)
                            .clickable { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SuccessNotificationIconColor,
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
    onDismiss: () -> Unit,
    context: Context
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
                    .background(ErrorNotificationBackground)
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
                            color = ErrorNotificationTextColor,
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
                            .background(ErrorNotificationBoxBackground)
                            .clickable { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = getString(context, R.string.close_notification),
                            tint = ErrorNotificationIconColor,
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
    showSuccess: (String) -> Unit,
    onProgramStopped: () -> Unit,
    programScope: kotlinx.coroutines.CoroutineScope
) {
    if (blocksManager.uiBlocks.size <= 1) {
        showError(getString(context, R.string.error_empty_program))
        onProgramStopped()
        return
    }
    if (blocksManager.uiBlocks.none { it.title == getString(context, R.string.main_block_title) }) {
        showError(getString(context, R.string.error_no_main))
        onProgramStopped()
        return
    }
    programScope.launch {
        try {
            Program.run()
        } catch (e: Exception) {
            if (e !is CancellationException) {
                showError("${getString(context, R.string.error_no_main)}: ${e.message}")
                onProgramStopped()
            }
        } finally {
            onProgramStopped()
        }
    }
}