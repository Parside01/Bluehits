package model

data class Connection(
    var from: Pin,
    var to: Pin
) {
    fun SetConnection(from: Pin, to: Pin): Boolean {
        if (from == to) return false


    }
}