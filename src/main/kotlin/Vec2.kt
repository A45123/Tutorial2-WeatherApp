import kotlin.math.sqrt

// 1. Vec2 definition
data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {

    // 2. Operator overloading

    // Addition
    operator fun plus(other: Vec2): Vec2 =
        Vec2(x + other.x, y + other.y)

    // Subtraction
    operator fun minus(other: Vec2): Vec2 =
        Vec2(x - other.x, y - other.y)

    // Scalar multiplication (right side)
    operator fun times(scalar: Double): Vec2 =
        Vec2(x * scalar, y * scalar)

    // Unary minus
    operator fun unaryMinus(): Vec2 =
        Vec2(-x, -y)

    // 3. Compare by magnitude
    override operator fun compareTo(other: Vec2): Int =
        this.magnitude().compareTo(other.magnitude())

    // 4. Functions

    fun magnitude(): Double =
        sqrt(x * x + y * y)

    fun dot(other: Vec2): Double =
        x * other.x + y * other.y

    fun normalized(): Vec2 {
        val mag = magnitude()
        if (mag == 0.0) {
            throw IllegalStateException("Cannot normalize zero vector")
        }
        return Vec2(x / mag, y / mag)
    }

    // 5. Index access
    operator fun get(index: Int): Double =
        when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("Index must be 0 or 1")
        }
}

// ✅ Challenge: left-hand scalar multiplication
operator fun Double.times(v: Vec2): Vec2 =
    Vec2(v.x * this, v.y * this)

// 6. Test
fun main() {
    val a = Vec2(3.0, 4.0)
    val b = Vec2(1.0, 2.0)

    println("a = $a")
    println("b = $b")
    println("a + b = ${a + b}")
    println("a - b = ${a - b}")

    println("a * 2.0 = ${a * 2.0}")
    println("-a = ${-a}")

    println("|a| = ${a.magnitude()}")
    println("a dot b = ${a.dot(b)}")

    println("norm(a) = ${a.normalized()}")

    println("a[0] = ${a[0]}")
    println("a[1] = ${a[1]}")

    println("a > b = ${a > b}")
    println("a < b = ${a < b}")

    val vectors = listOf(
        Vec2(1.0, 0.0),
        Vec2(3.0, 4.0),
        Vec2(0.0, 2.0)
    )

    println("Longest = ${vectors.maxOrNull()}")
    println("Shortest = ${vectors.minOrNull()}")

    // ✅ Challenge: destructuring (already works automatically!)
    val (x, y) = a
    println("Destructured a -> x=$x, y=$y")

    // ✅ Challenge: left-hand multiplication
    println("2.0 * a = ${2.0 * a}")
}