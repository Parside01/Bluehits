package interpreter.models

class Connection(
    from: Pin,
    to: Pin
) {
    private var from: Pin
    private var to: Pin

    init {
        if (from == to) throw IllegalArgumentException("Cannot connect the same pin.")
        if (from.id == to.id) throw IllegalArgumentException("Cannot connect pins with the same ID.")
        if (from.type != to.type) throw IllegalArgumentException("Cannot connect pins of different types.")

        this.from = from
        this.to = to

        this.to.setValue(this.from.getValue())
    }

    fun getFrom(): Pin? {
        return from
    }

    fun getTo(): Pin? {
        return to
    }
}