/*
Topic of Investigation: coroutines in Kotlin
 */
fun coroutines() {
    // what does this code do?
    // runBlocking{} will build a CoroutineScope which will block the main
    // thread until its execution is complete
    runBlocking {
        // launch{} will launch a coroutine and return a Job for manipulation
        val job = launch { // launch a new coroutine and continue
            // delay() will suspend the current function
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            println("World!") // print after delay
        }
        // manipulate how the coroutine is executed through using the Job
        job.cancel() // cancel the running coroutine
        println("Hello") // main coroutine continues while a previous one is delayed

        // define a coroutineScope that will not block the parent thread
        // a container for concurrent coroutines
        coroutineScope() {
            // these 2 coroutines will now run concurrently
            launch {
                println("hello")
            }
            launch {
                println("World")
            }
        }
    }
}