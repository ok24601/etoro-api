package ok.work.etoroapi

import kotlinx.coroutines.*

fun main(args: Array<String>) {
    exampleLaunchCoroutineScope()
}

suspend fun printlnDelayed(message: String) {
    delay(1000)
    println(message)
}

fun exampleBlocking() = runBlocking {
    println("1")
    printlnDelayed("2")
    println("3")
}

fun exampleBlockingDispatcher() {
      runBlocking(Dispatchers.Default) {
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
}

fun exampleLaunchGlobal() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")
    GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    delay(1500)
}

fun exampleLaunchGlobalWaiting() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val job = GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")


    println(job.join())
}

fun exampleLaunchCoroutineScope() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    this.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
}

