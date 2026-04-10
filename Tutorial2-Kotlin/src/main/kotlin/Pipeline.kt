// 1. Pipeline class
class Pipeline {

    private val stages = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(name to transform)
    }

    fun execute(input: List<String>): List<String> {
        return stages.fold(input) { acc, (_, transform) ->
            transform(acc)
        }
    }

    fun describe() {
        println("Pipeline stages:")
        stages.forEachIndexed { index, (name, _) ->
            println("${index + 1}. $name")
        }
    }

    // 🔹 Challenge: compose two stages
    fun compose(stage1: String, stage2: String, newName: String) {
        val i1 = stages.indexOfFirst { it.first == stage1 }
        val i2 = stages.indexOfFirst { it.first == stage2 }

        if (i1 == -1 || i2 == -1) return

        val f1 = stages[i1].second
        val f2 = stages[i2].second

        // function composition: f1 andThen f2
        val composed: (List<String>) -> List<String> = { input ->
            f2(f1(input))
        }

        // remove both and insert new composed stage at position of first
        val minIndex = minOf(i1, i2)
        stages.removeAt(maxOf(i1, i2))
        stages.removeAt(minIndex)
        stages.add(minIndex, newName to composed)
    }
}

// 2. buildPipeline DSL-style builder
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    val pipeline = Pipeline()
    pipeline.block()
    return pipeline
}

// 🔹 Challenge: fork function
fun fork(
    input: List<String>,
    p1: Pipeline,
    p2: Pipeline
): Pair<List<String>, List<String>> {
    return p1.execute(input) to p2.execute(input)
}

// 3 & 4. Test
fun main() {

    val logs = listOf(
        " INFO: server started ",
        " ERROR: disk full ",
        " DEBUG: checking config ",
        " ERROR: out of memory ",
        " INFO: request received ",
        " ERROR: connection timeout "
    )

    val pipeline = buildPipeline {
        addStage("Trim") { list ->
            list.map { it.trim() }
        }
        addStage("Filter errors") { list ->
            list.filter { it.contains("ERROR") }
        }
        addStage("Uppercase") { list ->
            list.map { it.uppercase() }
        }
        addStage("Add index") { list ->
            list.mapIndexed { index, line ->
                "${index + 1}. $line"
            }
        }
    }

    // Describe
    pipeline.describe()

    // Execute
    val result = pipeline.execute(logs)

    println("Result:")
    result.forEach { println(it) }

    // 🔹 Challenge demo: compose stages
    println("\nAfter composing Trim + Filter errors:")
    pipeline.compose("Trim", "Filter errors", "Trim+Filter")

    pipeline.describe()

    val newResult = pipeline.execute(logs)
    newResult.forEach { println(it) }

    // 🔹 Challenge demo: fork
    val p1 = buildPipeline {
        addStage("Trim") { it.map { s -> s.trim() } }
    }

    val p2 = buildPipeline {
        addStage("Uppercase") { it.map { s -> s.uppercase() } }
    }

    val (r1, r2) = fork(logs, p1, p2)

    println("\nFork results:")
    println("Pipeline 1: $r1")
    println("Pipeline 2: $r2")
}