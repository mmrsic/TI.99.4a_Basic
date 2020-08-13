package com.github.mmrsic.ti99.hw

class TiBasicColors {

   var background = TiColor.Cyan
   private val colorSets = mutableMapOf<TiColorSet, TiCharacterColor>()
   private val defaultCharColor = TiCharacterColor(TiColor.Black, TiColor.Transparent)

   /** Change the color for a given [TiColorSet] to a given [TiCharacterColor]. */
   fun setCharacterSet(charSet: TiColorSet, charSetColors: TiCharacterColor) {
      colorSets[charSet] = charSetColors
   }

   /** [TiCharacterColor] for a given [TiColorSet]. */
   fun getColor(charSet: TiColorSet) = colorSets.getOrDefault(charSet, defaultCharColor)

   fun forCharacter(charCode: Int) {

   }

   /** Reset the colors of this instance to its default. */
   fun reset() {
      colorSets.clear()
      background = TiColor.Cyan
   }

}

enum class TiColor {
   Transparent, Black, MediumGreen, LightGreen, DarkBlue, LightBlue, DarkRed, Cyan,
   MediumRed, LightRed, DarkYellow, LightYellow, DarkGreen, Magenta, Gray, White;

   override fun toString(): String {
      return super.toString() + toCode()
   }

   companion object {
      /** Convert a TI Basic color code into the corresponding [TiColor]. */
      fun fromCode(code: Int): TiColor = codeToColor().getValue(code)
   }
}

/** Convert this [TiColor] to its TI Basic code. */
fun TiColor.toCode(): Int = codeToColor().filterValues { it == this }.keys.first()

fun TiColor.replaceTransparentBy(replacementColor: TiColor): TiColor {
   return if (this != TiColor.Transparent) this else replacementColor
}

data class TiCharacterColor(val foreground: TiColor, val background: TiColor) {

   constructor(foregroundCode: Int, backgroundCode: Int) : this(
      TiColor.fromCode(foregroundCode),
      TiColor.fromCode(backgroundCode)
   )

   fun contains(color: TiColor): Boolean {
      return foreground == color || background == color
   }

   fun replaceTransparentBy(replacementColor: TiColor): TiCharacterColor {
      if (!this.contains(TiColor.Transparent)) return this
      return TiCharacterColor(
         foreground.replaceTransparentBy(replacementColor), background.replaceTransparentBy(replacementColor)
      )
   }

}

enum class TiColorSet(val setNum: Int, val charCodeRange: IntRange) {
   ONE(1, 32..39),
   TWO(2, 40..47),
   THREE(3, 48..55),
   FOUR(4, 56..63),
   FIVE(5, 64..71),
   SIX(6, 72..79),
   SEVEN(7, 80..87),
   EIGHT(8, 88..95),
   NINE(9, 96..103),
   TEN(10, 104..111),
   ELEVEN(11, 112..119),
   TWELVE(12, 120..127),
   THIRTEEN(13, 128..135),
   FOURTEEN(14, 136..143),
   FIFTEEN(15, 144..151),
   SIXTEEN(16, 152..159);

   companion object {
      /** The [TiColorSet] for which [TiColorSet.setNum] equals a given number. */
      fun withNumber(setNum: Int) = values().find { it.setNum == setNum }
         ?: throw IllegalArgumentException("No such color set: $setNum")

      /** The [TiColorSet] for a given TI Basic character code. */
      fun forCharacterCode(charCode: Int) = values().find { it.charCodeRange.contains(charCode) }
         ?: throw IllegalArgumentException("No color set for character code=$charCode")
   }
}

// HELPERS //

private fun codeToColor(): Map<Int, TiColor> {
   return mapOf(
      1 to TiColor.Transparent,
      2 to TiColor.Black,
      3 to TiColor.MediumGreen,
      4 to TiColor.LightGreen,
      5 to TiColor.DarkBlue,
      6 to TiColor.LightBlue,
      7 to TiColor.DarkRed,
      8 to TiColor.Cyan,
      9 to TiColor.MediumRed,
      10 to TiColor.LightRed,
      11 to TiColor.DarkYellow,
      12 to TiColor.LightYellow,
      13 to TiColor.DarkGreen,
      14 to TiColor.Magenta,
      15 to TiColor.Gray,
      16 to TiColor.White
   )
}
