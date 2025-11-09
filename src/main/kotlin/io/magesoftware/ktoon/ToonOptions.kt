package io.magesoftware.ktoon

data class ToonOptions(
    val indent: Int = 2,
    val delimiter: Delimiter = Delimiter.COMMA,
    val lengthMarker: Boolean = false
) {
    companion object {
        val DEFAULT: ToonOptions = ToonOptions()
    }
}
