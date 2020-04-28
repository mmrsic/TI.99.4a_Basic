package com.github.mmrsic.ti99.tornado

import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.expr.toAsciiCode
import com.github.mmrsic.ti99.hw.KeyboardConverter
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.Screen
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen
import com.github.mmrsic.ti99.hw.TiCode
import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.Controller
import tornadofx.FXEvent
import tornadofx.View
import tornadofx.action
import tornadofx.anchorpane
import tornadofx.borderpane
import tornadofx.contextmenu
import tornadofx.imageview
import tornadofx.item
import tornadofx.singleAssign
import tornadofx.textarea

/** [App] for TI Basic IDE. */
class TiBasicModuleIde : App(TiBasicModuleView::class)

/** [View] for TI Basic module. This is the parent for all views of the application. */
class TiBasicModuleView : View("TI Basic Module") {

    /** Root node of this [TiBasicModuleView]. */
    override val root = borderpane {
        center(TiBasicScreenView::class)
        right(TiBasicProgramView::class)
    }
}

/** [View] for screen of TI Basic module as presented on a TI 99/4a. */
class TiBasicScreenView : View("TI Basic Screen") {

    private val moduleCtrl = find(TiBasicModuleController::class)

    private var screenImage by singleAssign<ImageView>()
    private var updateCanvasTimer = object : AnimationTimer() {
        override fun handle(l: Long) {
            updateCanvas()
        }
    }

    private companion object {
        private const val MIN_WIDTH = TiBasicScreen.PIXEL_WIDTH
        private const val MIN_HEIGHT = TiBasicScreen.PIXEL_HEIGHT
    }

    private val screenPixels = WritableImage(MIN_WIDTH, MIN_HEIGHT)

    /** Root pane of this view. */
    override val root = anchorpane {
        screenImage = imageview {
            image = screenPixels
            minWidth = MIN_WIDTH.toDouble()
            minHeight = MIN_HEIGHT.toDouble()
            prefWidth = minWidth * 2
            prefHeight = minHeight * 2
        }
    }

    override fun onDock() {
        super.onDock()
        setWindowMinSize(MIN_WIDTH, MIN_HEIGHT)
        screenImage.fitWidthProperty().bind(root.widthProperty())
        screenImage.fitHeightProperty().bind(root.heightProperty())
        updateCanvasTimer.start()
    }

    // HELPERS //

    private fun updateCanvas() {
        displayScreen(moduleCtrl.screenProperty.get())
    }

    private fun displayScreen(screen: Screen) {
        screen.patterns.forEachCellDo { row, col, pattern ->
            val fgColor = Color.BLACK // TODO: Introduce character code color map
            val bgColor = Color.CYAN // TODO: Introduce character code color map
            val pixelPattern = toPixelPattern(pattern)
            if (pixelPattern.length != 64) error("Illegal pixel pattern length ${pixelPattern.length}: $pixelPattern")
            for ((patternIdx, patternDigit) in pixelPattern.withIndex()) {
                val x = 8 * (col - 1) + patternIdx % 8
                val y = 8 * (row - 1) + patternIdx / 8
                val color = if (patternDigit == '1') fgColor else bgColor
                screenPixels.pixelWriter.setColor(x, y, color)
            }
        }
    }
}

/** [View] for TI Basic program currently held in memory. */
class TiBasicProgramView : View("TI Basic Program") {

    /** Root node of his [TiBasicProgramView]. */
    override val root = textarea {
        prefWidth = 256.0
        shortcut("Ctrl+r") {
            fire(ExecuteCommandsRequest("$text\nRUN"))
        }
        contextmenu {
            item("RUN").action {
                fire(ExecuteCommandsRequest("$text\nRUN"))
            }
        }
    }
}

/** [Controller] for a single [TiBasicModule]. */
class TiBasicModuleController : Controller() {
    private val module = TiBasicModule()
    val moduleProperty = SimpleObjectProperty(module)
    val screenProperty = SimpleObjectProperty(module.screen)

    init {
        module.setKeyboardInputProvider(object : KeyboardInputProvider {
            override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
                val dlg = TextInputDialog()
                dlg.title = "TI Basic"
                dlg.headerText = "INPUT statement (program line=${ctx.programLine})"
                dlg.contentText = ctx.prompt
                val userInput = dlg.showAndWait()
                return ((if (userInput.isEmpty) "" else userInput.get()) + "\r").asSequence()
            }

            override fun currentlyPressedKeyCode(ctx: KeyboardInputProvider.CallKeyContext): TiCode? {
                val dlg = TextInputDialog()
                dlg.title = "TI Basic"
                dlg.headerText = "CALL KEY statement (program line=${ctx.programLineNumber})"
                dlg.contentText = "Which key do you want to signal to the program? (Type 'break' for a breakpoint)"
                val userInput = dlg.showAndWait()
                if (userInput.isEmpty) return null
                if (userInput.get() == "break") throw Breakpoint()
                return KeyboardConverter.map[toAsciiCode(userInput.get()[0])]
            }
        })

        subscribe<ExecuteCommandsRequest> { event ->
            TiBasicCommandLineInterpreter(module).interpretAll(event.commands, module)
        }
    }
}

/** [FXEvent] signalling the request to execute TI Basic commands. */
class ExecuteCommandsRequest(val commands: String) : FXEvent()

// HELPERS //

private fun toPixelPattern(charPattern: String) = buildString {
    for (c in charPattern) {
        append(Integer.toBinaryString(Integer.parseInt(c.toString(), 16)).padStart(4, '0'))
    }
}