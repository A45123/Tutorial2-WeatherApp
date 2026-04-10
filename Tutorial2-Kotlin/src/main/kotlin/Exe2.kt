// 1. Generic Cache class
class Cache<K : Any, V : Any> {

    private val store: MutableMap<K, V> = mutableMapOf()

    fun put(key: K, value: V) {
        store[key] = value
    }

    fun get(key: K): V? {
        return store[key]
    }

    fun evict(key: K) {
        store.remove(key)
    }

    fun size(): Int {
        return store.size
    }

    // 2. getOrPut
    fun getOrPut(key: K, default: () -> V): V {
        return if (store.containsKey(key)) {
            store[key]!!
        } else {
            val value = default()
            store[key] = value
            value
        }
    }

    // 3. transform
    fun transform(key: K, action: (V) -> V): Boolean {
        val current = store[key]
        return if (current != null) {
            store[key] = action(current)
            true
        } else {
            false
        }
    }

    // 4. snapshot (immutable copy)
    fun snapshot(): Map<K, V> {
        return store.toMap()
    }

    // Challenge: filterValues
    fun filterValues(predicate: (V) -> Boolean): Map<K, V> {
        return store.filterValues(predicate).toMap()
    }
}

// 5. Testing
fun main() {

    println("---- Word frequency cache ----")
    val wordCache = Cache<String, Int>()

    wordCache.put("kotlin", 1)
    wordCache.put("scala", 1)
    wordCache.put("haskell", 1)

    println("Size: ${wordCache.size()}")
    println("Frequency of \"kotlin\": ${wordCache.get("kotlin")}")

    println("getOrPut \"kotlin\": ${wordCache.getOrPut("kotlin") { 0 }}")
    println("getOrPut \"java\": ${wordCache.getOrPut("java") { 0 }}")

    println("Size after getOrPut: ${wordCache.size()}")

    println("Transform \"kotlin\" (+1): ${wordCache.transform("kotlin") { it + 1 }}")
    println("Transform \"cobol\" (+1): ${wordCache.transform("cobol") { it + 1 }}")

    println("Snapshot: ${wordCache.snapshot()}")

    // Challenge usage
    println("Words with count > 0: ${wordCache.filterValues { it > 0 }}")


    println("\n---- Id registry cache ----")
    val idCache = Cache<Int, String>()

    idCache.put(1, "Alice")
    idCache.put(2, "Bob")

    println("Id 1 -> ${idCache.get(1)}")
    println("Id 2 -> ${idCache.get(2)}")

    idCache.evict(1)

    println("After evict id 1, size: ${idCache.size()}")
    println("Id 1 after evict -> ${idCache.get(1)}")
}