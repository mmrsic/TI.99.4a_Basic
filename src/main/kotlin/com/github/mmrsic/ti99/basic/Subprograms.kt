package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.NumericVariable
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.StickHorizontalDirection
import com.github.mmrsic.ti99.hw.StickVerticalDirection
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiColor
import kotlin.math.roundToInt

/**
 * The CHAR subprogram allows you to define special graphics characters. You can redefine the standard sets of
 * characters (ASCII codes 32-127) and the undefined characters, ASCII codes 128-159.
 * The CHAR subprogram is the inverse of the [CharpatSubprogram].
 *
 * @param code The char-code specifies the code of the character you wish to define and must be a numeric expression
 * with a value between 32 and 159, inclusive. If the character you are defining is in the range 128-159 and there is
 * insufficient free memory to define the character, the program will terminate with a "MEMORY FULL" error.
 * @param pattern The pattern-identifier is a 16-character string expression which specifies the pattern of the
 * character you want to use in your program. The string expression is a coded representation of the 64 dots which make
 * up a character position on the screen. These 64 dots comprise an 8-by-8 grid. Each row is partitioned into two blocks
 * of four dots each. The first two characters in the string describe the pattern for row one of the dot-grid, the next
 * two describe row two, and so on. If the pattern is less than 16 characters, the computer will assume that the
 * remaining characters are zero. If it is longer than 16 characters, the computer will ignore the excess.
 */
class CharSubprogram(private val code: NumericExpr, private val pattern: StringExpr) : Statement, Command {
    override val name = "CHAR"
    override fun listText() = "CALL $name(${code.listText().trim()},${pattern.listText()})"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val characterCode = code.value().toNative().toInt()
        val patternIdentifier = pattern.displayValue()
        machine.defineCharacter(characterCode, patternIdentifier)
    }
}

/**
 * The CLEAR subprogram is used to clear (erase) the entire screen. When the CLEAR subprogram is called, the space
 * character (ASCII code 32) is placed in all positions on the screen.
 */
class ClearSubprogram : Statement, Command {
    override val name = "CLEAR"
    override fun listText() = "CALL $name"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.screen.clear()
    override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The COLOR subprogram provides a powerful design capability by allowing you to specify screen character colors. (To
 * change the screen color itself, see the [ScreenSubprogram].)
 *
 *  Each character displayed on your computer screen has two colors. The color of the dots that make up the character
 * itself is called the foreground color. The color that occupies the rest of the character position on the screen is
 * called the background color. Sixteen colors are available on the TI computer, so your entries for foreground and
 * background color must have a value of 1 through 16. The color codes are given in the table below.
 *
 * If transparent (code 1) is specified, the present screen color shows through when a character is displayed. Until
 * a CALL COLOR is performed, the standard foreground is black (code 2) and the standard background color is transparent
 * (code 1) for all characters. When a breakpoint occurs, all characters are reset to the standard colors.
 *
 * ```
 * | Color Code |      Color     |
 * |      1     |  Transparent   |
 * |      2     |  Black         |
 * |      3     |  Medium Green  |
 * |      4     |  Light Green   |
 * |      5     |  Dark Blue     |
 * |      6     |  Light Blue    |
 * |      7     |  Dark Red      |
 * |      8     |  Cyan          |
 * |      9     |  Medium Red    |
 * |     10     |  Light Red     |
 * |     11     |  Dark Yellow   |
 * |     12     |  Light Yellow  |
 * |     13     |  Dark Green    |
 * |     14     |  Magenta       |
 * |     15     |  Gray          |
 * |     16     |  White         |
 * ```
 *
 * To use CALL COLOR you must also specify to which of sixteen character sets the character you are printing belongs.
 * The list of ASCII character codes for the standard characters is given in the Appendix. The character is displayed
 * in the color specified when you use the [HcharSubprogram] or the [VcharSubprogram]. The character-set-numbers are
 * given below:
 * ```
 * | Set Number | Character Codes |
 * |      1     |      32-39      |
 * |      2     |      40-47      |
 * |      3     |      48-55      |
 * |      4     |      56-63      |
 * |      5     |      64-71      |
 * |      6     |      72-79      |
 * |      7     |      80-87      |
 * |      8     |      88-95      |
 * |      9     |      96-103     |
 * |     10     |     104-111     |
 * |     11     |     112-119     |
 * |     12     |     120-127     |
 * |     13     |     128-135     |
 * |     14     |     136-143     |
 * |     15     |     144-151     |
 * |     16     |     152-159     |
 * ```
 */
class ColorSubprogram(
    private val characterSetNumber: NumericExpr,
    private val foregrColorCode: NumericExpr,
    private val backgrColorCode: NumericExpr
) : Statement, Command {
    override val name = "COLOR"
    override fun listText() =
        "CALL $name(${characterSetNumber.listText()},${foregrColorCode.listText()},${backgrColorCode.listText()})"

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setColor(characterSetNumber.value(), foregrColorCode.value(), backgrColorCode.value())
    }
}

/**
 * The GCHAR subprogram allows you to read a character from anywhere on the display screen.
 * The row and column arguments are numeric expressions. If the evaluation of the numeric expressions results in a non-
 * integer value, the result is rounded to obtain an integer.
 * @param row row of the character position to read - a value between 1 (=top) and 24 (=bottom)
 * @param column column of the character position to read - a value between 1 (=left side) and 32 (=right side)
 * @param numericVarRef where to put the ASCII numeric code of the requested character
 */
class GcharSubprogram(
    private val row: NumericExpr,
    private val column: NumericExpr,
    private val numericVarRef: NumericVariable
) : Statement, Command {

    override val name = "GCHAR"
    override fun listText() = "CALL $name(${row.listText()},${column.listText()},$numericVarRef)"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val intRow = row.value().toNative().roundToInt()
        val intCol = column.value().toNative().roundToInt()
        machine.readScreenCharCode(numericVarRef.name, intRow, intCol)
    }
}


/**
 * The HCHAR subprogram places a character anywhere on the screen and, optionally, repeats it horizontally. The
 * row-number and column-number locate the starting position on the screen.
 *
 * If the evaluation of any of the numeric expressions results in a non-integer value, the result is rounded to obtain
 * an integer. The valid ranges are given below:
 * ```
 * Value                    Range
 * Row-number               1-24, inclusive
 * Column-number            1-32, inclusive
 * Char-code                0-32767, inclusive
 * Number-of-repetitions    0-32767, inclusive
 * ```
 * Although you may specify a value as large as 32767 for char-code, the computer will convert the value specified to
 * a range of 0 through 255. Character codes 32 through 127 are defined as the standard ASCII character codes. Character
 * codes 128 through 159 may be defined using the [CharSubprogram]. If you specify an undefined character for char-code,
 * you get whatever is in memory at the time the HCHAR subprogram is called.
 * @param row A value of 1 indicates the top of the screen.
 * @param column A value of 1 indicates the left side of the screen.
 */
class HcharSubprogram(
    private val row: NumericExpr, private val column: NumericExpr, private val charCode: NumericExpr,
    private val repetitions: NumericExpr = NumericConstant.ONE
) : Statement, Command {
    override val name = "HCHAR"
    override fun listText(): String {
        val rowPart = row.listText().trim()
        val columnPart = column.listText().trim()
        val codePart = charCode.listText().trim()
        val optionalRepetitionPart = if (repetitions != NumericConstant.ONE) ",${repetitions.listText().trim()}" else ""
        return "CALL $name($rowPart,$columnPart,$codePart$optionalRepetitionPart)"
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val row = row.value().toNative().roundToInt()
        val column = column.value().toNative().roundToInt()
        val characterCode = charCode.value().toNative().roundToInt()
        val repetition = repetitions.value().toNative().roundToInt()
        machine.screen.hchar(row, column, characterCode, repetition)
    }
}

/**
 * The JOYST subprogram allows you to input information to the computer based on the position of the lever on the Wired
 * Remote Controllers accessory (available separately).
 *
 * The key-unit is a [NumericExpr] which, when evaluated, has a value of 1 through 4. Specifying a key-unit of 3,4, or 5
 * maps the console keyboard to a particular mode of operation, as explained in [KeySubprogram]. If key-unit has a value
 * of 3, 4, or 5, the computer will not properly detect input from the remote controllers.
 *
 * Numeric variables must be used for xReturn and yReturn. The subprogram assigns an integer value of -4, +4, or 0 to
 * each of these variables, based on the position of the joystick at that time:
 * ```
 *                                              (0,4)
 *                                  (-4,4)                  (4,4)
 *                      (-4,0)                  (0,0)                 (4,0)
 *                                  (-4,-4)                 (4,-4)
 *                                              (0,-4)
 * ```
 *
 * @param keyUnit 1 = controller 1 - 2 = controller 2 - 3, 4, and 5 = specific modes for console keyboard
 * @param xReturn filled with a value of -4, 0, or 4, according to the values above, where the first value is the x
 * return value
 * @param yReturn filled with a value of -4, 0, or 4, according to the values above, where the second value is the y
 * return value
 */
class JoystSubprogram(private val keyUnit: NumericExpr, val xReturn: NumericVariable, val yReturn: NumericVariable) :
    Statement, Command {
    override val name = "JOYST"
    override fun listText() = "CALL $name(${keyUnit.listText()},${xReturn.listText()},${yReturn.listText()})"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val state = machine.getJoystickState(keyUnit.value())
        println("CALL JOYST: $state")
        val x = when (state.horizontalDirection) {
            StickHorizontalDirection.LEFT -> -4
            StickHorizontalDirection.RIGHT -> 4
            StickHorizontalDirection.NONE -> 0
        }
        machine.setNumericVariable(xReturn.name, NumericConstant(x))
        val y = when (state.verticalDirection) {
            StickVerticalDirection.UP -> 4
            StickVerticalDirection.DOWN -> -4
            StickVerticalDirection.NONE -> 0
        }
        machine.setNumericVariable(yReturn.name, NumericConstant(y))
    }
}

/**
 * The KEY subprogram allows you to transfer one character from the keyboard directly to your program. This eliminates
 * the need for an [InputStatement] and saves time in getting data from a single key into memory. The character
 * represented by the key pressed is not displayed on the screen.
 *
 * A key-unit of 0 remaps the keyboard in whatever mode was specified in the previous CALL KEY program line.
 * Key-units of 1 and 2 are used for a split-keyboard scan, when you want to separate the console keyboard into two
 * smaller duplicate keyboards or when you are using the remote controller firebuttons as input devices.
 *
 * Specifying 3,4, or 5 as key-unit maps the keyboard to a particular mode of operation. The keyboard mode you specify
 * determines the character codes returned by certain keys.
 * A key-unit of 3 places the computer in the standard TI-99/4 keyboard mode. (Most Command Module software uses this
 * mode.) In this mode, both upper- and lower-case alphabetical characters are returned by the computer as upper-case
 * only, and the function keys (Back, Begin, Clear, etc.) return codes 1 through 15. No control characters are active.
 * A key-unit of 4 remaps the keyboard in the Pascal mode. Here, both upper- and lower-case alphabetical character codes
 * are returned by the computer, and the function keys return codes ranging from 129 through 143. The control character
 * codes are 1 through 31.
 * A key-unit of 5 places the keyboard in the BASIC mode. Both upper- and lower-case alphabetical character codes are
 * returned by the computer. The function key codes are 1 through 15, and the control key codes are 128 through 159 (and
 * 187).
 *
 * The return-variable must be a numeric variable. The computer will place in return-variable the numeric character code
 * represented by the key pressed. If the unit used is the console keyboard (unit 0), the character codes are the normal
 * ASCII codes and may range from 0-127. If you are using the split keyboard (unit 1 and/or unit 2), the character codes
 * will be 0 through 19.
 *
 * The status-variable is a numeric variable which servers as an indicator to let you know what happened at the
 * keyboard. The computer will return one of the following codes to the status-variable after performing the CALL KEY
 * routine:
 * * +1 = a new key was pressed since the last performance of the CALL KEY routine
 * * -1 = the same key was pressed during the performance of CALL KEY as was pressed during the previous performance
 * *  0 = no key was pressed
 *
 * @param keyUnit indicates which keyboard is the input device - a value between 0 and 5
 * @param returnVar numeric variable name where to save the key character code
 * @param statusVar numeric variable name where to save the key character code
 */
class KeySubprogram(
    private val keyUnit: NumericExpr,
    private val returnVar: NumericVariable,
    private val statusVar: NumericVariable
) : Statement, Command {
    override val name = "KEY"
    override fun listText() = "CALL $name()"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.acceptKeyboardInput(programLineNumber, keyUnit.value(), returnVar, statusVar)
    }
}

/**
 * The SCREEN subprogram enhances the graphic capabilities of the TI computer by allowing you to change the screen
 * color. The standard screen color while a program is running is light green (color-code = 4).
 *
 * The color code is a numeric expression which, when evaluated, has a value of 1 through 16. The table of the sixteen
 * available colors and their codes is given below.
 * ```
 *   Color-code     Color
 *        1         Transparent
 *        2         Black
 *        3         Medium Green
 *        4         Light Green
 *        5         Dark Blue
 *        6         Light Blue
 *        7         Dark Red
 *        8         Cyan
 *        9         Medium Red
 *       10         Light Red
 *       11         Dark Yellow
 *       12         Light Yellow
 *       13         Dark Green
 *       14         Magenta
 *       15         Gray
 *       16         White
 * ```
 * When the CALL SCREEN is performed, the entire screen background changes to the color specified by the color-code.
 * All characters on the screen remain the same unless you have specified a transparent foreground or background color
 * for them. In that case, the screen color "shows through" the transparent foreground or background.
 *
 * The screen is set to cyan (code 8) when a program stops for a breakpoint or terminates. If you [ContinueCommand] a
 * program after a breakpoint, the screen is reset to the standard color (light green).
 */
class ScreenSubprogram(private val colorCode: NumericExpr) : Statement, Command {
    override val name = "SCREEN"
    override fun listText() = "CALL $name(${colorCode.listText()})"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.screen.colors.backgroundColor = TiColor.fromCode(colorCode.value().toNative().roundToInt())
    }
}

/**
 * The SOUND subprogram tells the computer to produce tones of different frequencies. The values you include control
 * three aspects of the tone:
 *  * duration - how long the tone lasts
 *  * frequency - what tone actually plays
 *  * volume - how loud the tone is
 *
 *  The duration, frequency, and volume are numeric expressions. If the evaluation of any of the numeric expressions
 *  results in a non-integer value, the result is rounded to obtain an integer. The valid ranges for each of these are
 *  given in the table and discussed further below.
 *  ```
 *  Value           Range
 *  duration        1 to 4250, inclusive
 *                  -1 to -4250, inclusive
 *  frequency       (Tone) 110 to 44733, inclusive
 *                  (Noise) -1 to -8, inclusive
 *  volume          0 (loudest) to 30 (quietest), inclusive
 *  ```
 *  A maximum of three tones and one noise can be activated simultaneously. For each tone or noise specified, its volume
 *  must be indicated immediately following the tone or noise.
 *  @param duration The duration is measured in milliseconds. Thus, the duration ranges from .001 to 4.25 seconds. (The
 *  actual duration may vary as much as 1/60th of a second.) On a negative value, the previous sound is stopped and the
 *  new one is begun immediately, otherwise the new sound is delayed until the previous finishes.
 *  @param frequency1 The frequency may be either a tone or a noise. The tones, measured in Hertz (one cycle per
 *  second), can be specified from a low-pitch of 100 Hz to a high-pitch of 44733 Hz, well above human hearing limits.
 *  If a negative value for frequency is specified, a noise, rather than a tone, is produced. The noise is either a
 *  "white noise", or "periodic noise". The noise associated with each value is given in the table below.
 *   ```
 *   Frequency value    Characteristic
 *          -1          Periodic Noise, Type 1
 *          -2          Periodic Noise, Type 2
 *          -3          Periodic Noise, Type 3
 *          -4          Periodic Noise that varies with the frequency of the third tone specified
 *          -5          White Noise, Type 1
 *          -6          White Noise, Type 2
 *          -7          White Noise, Type 3
 *          -8          White Noise that varies with the frequency of the third tone specified
 *   ```
 */
class SoundSubprogram(
    private val duration: NumericExpr,
    private val frequency1: NumericExpr, private val volume1: NumericExpr,
    private val frequency2: NumericExpr? = null, private val volume2: NumericExpr? = null,
    private val frequency3: NumericExpr? = null, private val volume3: NumericExpr? = null,
    private val noise: NumericExpr? = null, private val volumeNoise: NumericExpr? = null
) : Statement, Command {
    override val name = "SOUND"
    override fun listText() = "CALL $name($duration,$frequency1,$volume1)"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.sound.play(
            duration.value(),
            frequency1.value(), volume1.value(),
            frequency2?.value(), volume2?.value(),
            frequency3?.value(), volume3?.value()
        )
    }
}

/**
 * The VCHAR subprogram performs very much like the [HcharSubprogram] except that it repeats characters vertically
 * rather than horizontally. The computer will display the character beginning at the specified position and continuing
 * down the screen. If the bottom of the screen is reached, the display will continue at the top of the next column to
 * the right. If the right edge of the screen is reached, the display will continue at the left edge. See the
 * [HcharSubprogram] for more details.
 */
class VcharSubprogram(
    private val row: NumericExpr, private val column: NumericExpr,
    private val charCode: NumericExpr, private val repetitions: NumericExpr = NumericConstant.ONE
) : Statement, Command {
    override val name = "VCHAR"
    override fun listText(): String {
        val rowPart = row.listText().trim()
        val columnPart = column.listText().trim()
        val codePart = charCode.listText().trim()
        val optionalRepetitionPart = if (repetitions != NumericConstant.ONE) ",${repetitions.listText().trim()}" else ""
        return "CALL $name($rowPart,$columnPart,$codePart$optionalRepetitionPart)"
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val row = row.value().toNative().toInt()
        val column = column.value().toNative().toInt()
        val characterCode = charCode.value().toNative().toInt()
        val repetition = repetitions.value().toNative().toInt()
        machine.screen.vchar(row, column, characterCode, repetition)
    }
}
