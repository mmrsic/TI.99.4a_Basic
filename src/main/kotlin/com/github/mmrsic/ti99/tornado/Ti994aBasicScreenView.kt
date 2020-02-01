package com.github.mmrsic.ti99.tornado

import com.github.mmrsic.ti99.hw.Cursor
import com.github.mmrsic.ti99.hw.Screen
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen
import javafx.animation.Timeline
import javafx.scene.Node
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class Ti994aBasicScreenView : View("TI 99/4a: Screen") {

    private val machine = TiBasicModule()

    private val scaleFactor = 2

    override val root = stackpane {

        canvas {
            val scale = scaleFactor.toDouble()
            scaleX = scale
            scaleY = scale
            width = TiBasicScreen.PIXEL_WIDTH.toDouble()
            height = TiBasicScreen.PIXEL_HEIGHT.toDouble()

            displayScreen(machine.screen, graphicsContext2D)
            createCursorNode(machine.screen.cursor, graphicsContext2D)?.apply {
                val cursorNode = this
                timeline {
                    keyframe(.5.seconds) {
                        keyvalue(cursorNode.visibleProperty(), !cursorNode.isVisible)
                    }
                    cycleCount = Timeline.INDEFINITE
                }
            }
        }
    }

    init {
        with(root) {
            prefWidth = scaleFactor * TiBasicScreen.PIXEL_WIDTH.toDouble()
            prefHeight = scaleFactor * TiBasicScreen.PIXEL_HEIGHT.toDouble()
        }
    }

    private fun displayScreen(screen: Screen, gc: GraphicsContext) {
        screen.patterns.patternsDo { row, col, pattern ->
            val fgColor = Color.BLACK // TODO: Introduce character code color map
            val bgColor = Color.CYAN // TODO: Introduce character code color map
            val pixelPattern = toPixelPattern(pattern)
            displayScreenCell(col, row, pixelPattern, fgColor, bgColor, gc)
        }
    }

    private fun displayScreenCell(
        col: Int, row: Int, pixelPattern: String,
        fgColor: Color, bgColor: Color, gc: GraphicsContext
    ) {
        if (pixelPattern.length != 64) {
            throw Exception("Illegal pixel pattern length ${pixelPattern.length}: $pixelPattern")
        }
        for ((patternIdx, patternDigit) in pixelPattern.withIndex()) {
            val x = 8 * (col - 1) + patternIdx % 8
            val y = 8 * (row - 1) + patternIdx / 8
            val color = if (patternDigit == '1') fgColor else bgColor
            gc.pixelWriter.setColor(x, y, color)
        }
    }

    private fun createCursorNode(c: Cursor?, gc: GraphicsContext): Node? {
        if (c == null) {
            return null
        }
        val x = scaleFactor * 8.0 * (c.column - 1) + 1
        val y = scaleFactor * 8.0 * (c.row - 1) + 1
        val extension = 6.0 * scaleFactor
        val result = Rectangle(x, y, extension, extension)
        gc.canvas.add(result)
        gc.fillRect(x, y, extension, extension)
        result.fill = Color.BLACK // TODO: Change to color code variable
        return result
    }

    private fun toPixelPattern(charPattern: String) = buildString {
        for (c in charPattern) {
            append(
                when (c) {
                    '0' -> "0000"
                    '1' -> "0001"
                    '2' -> "0010"
                    '3' -> "0011"
                    '4' -> "0100"
                    '5' -> "0101"
                    '6' -> "0110"
                    '7' -> "0111"
                    '8' -> "1000"
                    '9' -> "1001"
                    'A' -> "1010"
                    'B' -> "1011"
                    'C' -> "1100"
                    'D' -> "1101"
                    'E' -> "1110"
                    'F' -> "1111"
                    else -> throw Exception("Illegal character pattern digit: $c")
                }
            )
        }
    }


}
