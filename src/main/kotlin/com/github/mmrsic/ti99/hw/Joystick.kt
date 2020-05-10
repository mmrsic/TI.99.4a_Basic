package com.github.mmrsic.ti99.hw

interface StickDirection

/** Possible horizontal directions of a [Joystick]. */
enum class StickHorizontalDirection : StickDirection {

   LEFT, RIGHT, NONE
}

/** Possible vertical directions of a [Joystick]. */
enum class StickVerticalDirection : StickDirection {

   UP, DOWN, NONE
}

/** A single TI Joystick, aka Wired Remote Controller. */
interface Joystick {

   /** The Joystick ID, a number of 1 or 2. */
   val id: Int

   /** Current state of this [Joystick]. */
   fun currentState(): State = DefaultState

   /** State of a Joystick. */
   interface State {

      /** [StickHorizontalDirection] of this state. */
      val horizontalDirection: StickHorizontalDirection

      /** Current vertical direction of the Joystick's stick. */
      val verticalDirection: StickVerticalDirection

      /** Current state of the fire button. */
      val fireButtonPressed: Boolean
   }

   /** Default [State] of a [Joystick]. */
   object DefaultState : State {

      override val fireButtonPressed = false
      override val horizontalDirection = StickHorizontalDirection.NONE
      override val verticalDirection = StickVerticalDirection.NONE

      override fun toString() =
         "${Joystick::class.simpleName}.${State::class.simpleName}: ($horizontalDirection,$verticalDirection) fire=$fireButtonPressed"
   }

}