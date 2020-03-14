package com.github.mmrsic.ti99.hw

enum class TiColor {
    Transparent, Black, MediumGreen, LightGreen, DarkBlue, LightBlue, DarkRed, Cyan,
    MediumRed, LightRed, DarkYellow, LightYellow, DarkGreen, Magenta, Gray, White;

    override fun toString(): String {
        return super.toString() + toCode()
    }

    companion object {
        fun fromCode(code: Int): TiColor = mapping().getValue(code)
    }
}

fun TiColor.toCode(): Int = mapping().values.indexOf(this)
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
        if (!this.contains(TiColor.Transparent)) {
            return this
        }

        return TiCharacterColor(
            foreground.replaceTransparentBy(replacementColor), background.replaceTransparentBy(replacementColor)
        )
    }

}

// HELPERS //

private fun mapping(): Map<Int, TiColor> {
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
