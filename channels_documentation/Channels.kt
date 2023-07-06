package com.salmakhd.forpracticelocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.contextaware.withContextAvailable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicInteger

/* Topic of investigation: channels in Kotlin
 */

class Channels: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // create a basic channel
        // 4? channel buffer size
        GlobalScope.launch {
            val channel = Channel<Int>(4) // a channel producer
            val sender = launch {
                channel.send(12)
                println("12 sent successfully")
                channel.send(11)
                println("11 sent successfully")
                channel.send(13)
                println("13 sent successfully")
                channel.send(15)
                println("15 sent successfully")
                channel.send(24)
                println("24 sent successfully")
            }
            // omit this code to get 4 values in the channel only
            // (no receivers to make room for the last sent value)
            channel.receive()
            println("first value received successfully.")
            delay(2000)
            // cancel the channel
            channel.cancel()
        }

        // define a thread-safe data structure
        val counter = AtomicInteger()
        runBlocking {
            withContext(Dispatchers.Default) {
                repeat(1000) {
                    launch {
                        repeat(2000) {
                            launch {
                                counter.incrementAndGet()
                            }
                        }
                    }
                }
            }
        }
        println("counter value now is: $counter")
        /*
        OUTPUT:
        counter value now is: 2000000 // wow!
         */

        // solution 2: switch to a single unified context when
        // manipulating the value (very slow): thread confinement fine-grained
        val myContext = newSingleThreadContext("Counter Context")
        var counter2 = 0
        runBlocking {
            repeat(100) {
                launch {
                    repeat(100) {
                        launch {
                            withContext(myContext) {
                                counter2++
                            }
                        }
                    }
                }
            }
        }
        println("counter2 value is: $counter2")
        /*
        OUTPUT:
        counter2 value is: 10000 // very very very slow
         */

        // solution 3: switch context in bigger chunks
        val myContext2 = newSingleThreadContext("Counter Context2")
        var counter3 = 0
        runBlocking {
            withContext(myContext2) {
                repeat(100) {
                    launch {
                        repeat(100) {
                            launch {
                                    counter3++
                            }
                        }
                    }
                }
            }

        }
        println("counter3 value is: $counter3")
        /*
        OUTPUT:
        counter3 value is: 10000 // Speed: better
         */
    }
}