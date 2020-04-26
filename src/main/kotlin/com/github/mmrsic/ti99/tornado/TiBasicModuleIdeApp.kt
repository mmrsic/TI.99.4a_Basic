package com.github.mmrsic.ti99.tornado

import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.Screen
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen
import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.Controller
import tornadofx.FXEvent
import tornadofx.View
import tornadofx.action
import tornadofx.anchorpane
import tornadofx.borderpane
import tornadofx.canvas
import tornadofx.contextmenu
import tornadofx.item
import tornadofx.singleAssign
import tornadofx.textarea


class TiBasicModuleIde : App(TiBasicModuleView::class)

class TiBasicModuleView : View("TI Basic Module") {

    override val root = borderpane {
        center(TiBasicScreenView::class)
        right(TiBasicProgramView::class)
    }
}

class TiBasicScreenView : View("TI Basic Screen") {

    private val moduleCtrl = find(TiBasicModuleController::class)

    private var canvas by singleAssign<Canvas>()
    private var updateCanvasTimer = object : AnimationTimer() {
        override fun handle(l: Long) {
            updateCanvas()
        }
    }

    override val root = anchorpane {
        canvas = canvas {
            minWidth = TiBasicScreen.PIXEL_WIDTH.toDouble()
            minHeight = TiBasicScreen.PIXEL_HEIGHT.toDouble()
            width = minWidth
            height = minHeight
            graphicsContext2D.fillText("TI Basic Screen", 70.0, 80.0)
        }
    }.apply {
        updateCanvasTimer.start()
    }

    private fun updateCanvas() {
        val gc = canvas.graphicsContext2D
        gc.fill = Color.CYAN
        gc.fillRect(.0, .0, canvas.width, canvas.height)
        displayScreen(moduleCtrl.screenProperty.get(), gc)
    }

    private fun displayScreen(screen: Screen, gc: GraphicsContext) {
        screen.patterns.forEachCellDo { row, col, pattern ->
            val fgColor = Color.BLACK // TODO: Introduce character code color map
            val bgColor = Color.CYAN // TODO: Introduce character code color map
            val pixelPattern = toPixelPattern(pattern)
            if (pixelPattern.length != 64) error("Illegal pixel pattern length ${pixelPattern.length}: $pixelPattern")
            for ((patternIdx, patternDigit) in pixelPattern.withIndex()) {
                val x = 8 * (col - 1) + patternIdx % 8
                val y = 8 * (row - 1) + patternIdx / 8
                val color = if (patternDigit == '1') fgColor else bgColor
                gc.pixelWriter.setColor(x, y, color)
            }
        }
    }
}

class TiBasicProgramView : View("TI Basic Program") {

    private val moduleCtrl = find(TiBasicModuleController::class)

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

class TiBasicModuleController : Controller() {
    private val module = TiBasicModule()
    val moduleProperty = SimpleObjectProperty(module)
    val screenProperty = SimpleObjectProperty(module.screen)

    init {
        subscribe<ExecuteCommandsRequest> { event ->
            TiBasicCommandLineInterpreter(module).interpretAll(event.commands, module)
        }
    }
}

class ExecuteCommandsRequest(val commands: String) : FXEvent()

// HELPERS //

private fun toPixelPattern(charPattern: String) = buildString {
    for (c in charPattern) {
        append(Integer.toBinaryString(Integer.parseInt(c.toString(), 16)).padStart(4, '0'))
    }
}