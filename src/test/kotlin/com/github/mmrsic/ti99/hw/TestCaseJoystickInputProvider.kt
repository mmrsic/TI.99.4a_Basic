package com.github.mmrsic.ti99.hw

/**
 * [Joystick] implementation for use in test cases.
 * @param id ID of the Joystick - must be 1 or 2
 */
class TestCaseJoystickInputProvider(override val id: Int) : Joystick {

    init {
        if (id !in 1..2) throw IllegalArgumentException("ID must be 1 or 2 but was $id")
    }

    var horizontalDirection: StickHorizontalDirection = StickHorizontalDirection.NONE
    var verticalDirection: StickVerticalDirection = StickVerticalDirection.NONE
    var fireButtonPressed: Boolean = false

    override fun toString() =
        "${javaClass.simpleName}($id) ($horizontalDirection,$verticalDirection), fire=$fireButtonPressed"

    override fun currentState() = object : Joystick.State {
        override val horizontalDirection = this@TestCaseJoystickInputProvider.horizontalDirection
        override val verticalDirection = this@TestCaseJoystickInputProvider.verticalDirection
        override val fireButtonPressed = this@TestCaseJoystickInputProvider.fireButtonPressed
    }
}