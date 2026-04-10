// 1. Sealed class definition
sealed class Event {
    data class Login(val username: String, val timestamp: Long) : Event()
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    data class Logout(val username: String, val timestamp: Long) : Event()
}

// 2. Extension function: filterByUser
fun List<Event>.filterByUser(username: String): List<Event> {
    return this.filter {
        when (it) {
            is Event.Login -> it.username == username
            is Event.Purchase -> it.username == username
            is Event.Logout -> it.username == username
        }
    }
}

// 3. Extension function: totalSpent
fun List<Event>.totalSpent(username: String): Double {
    return this
        .filterIsInstance<Event.Purchase>()
        .filter { it.username == username }
        .sumOf { it.amount }
}

// 4. Higher-order function: processEvents
fun processEvents(events: List<Event>, handler: (Event) -> Unit) {
    events.forEach(handler)
}

// 5 & 6. Testing with sample data
fun main() {
    val events = listOf(
        Event.Login("alice", 1_000),
        Event.Purchase("alice", 49.99, 1_100),
        Event.Purchase("bob", 19.99, 1_200),
        Event.Login("bob", 1_050),
        Event.Purchase("alice", 15.00, 1_300),
        Event.Logout("alice", 1_400),
        Event.Logout("bob", 1_500)
    )

    // Process and print events
    processEvents(events) { event ->
        when (event) {
            is Event.Login -> {
                println("[LOGIN]")
                println("${event.username} logged in at t=${event.timestamp}")
            }
            is Event.Purchase -> {
                println("[PURCHASE] ${event.username} spent $${event.amount} at t=${event.timestamp}")
            }
            is Event.Logout -> {
                println("[LOGOUT]")
                println("${event.username} logged out at t=${event.timestamp}")
            }
        }
    }

    // Totals
    println("Total spent by alice: $${events.totalSpent("alice")}")
    println("Total spent by bob: $${events.totalSpent("bob")}")

    // Filtered events
    println("Events for alice:")
    events.filterByUser("alice").forEach {
        println(it)
    }
}