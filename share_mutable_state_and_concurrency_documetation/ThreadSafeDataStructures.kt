/*
Topic of investigation: Shared Mutable State and Concurrency [https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html]
Performing operations on variables from within multiple threads (coroutines running in different contexts)
 */
package com.salmakhd.android.forpractice.threadsafedatastructures

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicInteger

const val SHARED_STATE_ACTIVITY_TAG = "SHARED STATE ACTIVITY"
class SharedStateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        solution 1:  AtomicInteger()
         */
        val counter1 = AtomicInteger()
        runBlocking {
            withContext(Dispatchers.Default) {
                repeat(100) {
                    launch {
                        repeat(1000) { counter1.incrementAndGet() }
                    }
                }
            }
            Log.i(SHARED_STATE_ACTIVITY_TAG, "Final value of counter1: $counter1")
        }
        /*
        OUTPUT:
        Final value of counter1: 100000
         */

        // solution 2:
        // performing operation from within a single context only
        // slow
        var counter2 = 0
        val counterContext = newSingleThreadContext("CounterContext") // single threaded context
        runBlocking {
            coroutineScope {
                repeat(100) {
                    repeat(1000) {
                        // ensure all operations on counter2 are performed from within the same context
                        withContext(counterContext) {
                            counter2++
                        }
                    }
                }
            }
            Log.i(SHARED_STATE_ACTIVITY_TAG, "Final value of counter2 is: $counter2")
            /*
            OUTPUT:
            Final value of counter2 is: 100000
             */

            // solution 3: switch context in bigger chunks for less overhead
            // FASTEST
            var counter3 = 0
            runBlocking {
                withContext(counterContext) {
                    repeat(100) {
                        repeat(1000) {
                            // ensure all operations on counter2 are performed from within the same context
                            counter3++
                        }
                    }
                }
                Log.i(SHARED_STATE_ACTIVITY_TAG, "Final value of counter3 is: $counter3")
                /*
                OUTPUT:
                Final value of counter3 is: 100000
                 */
            }
        }

        // solution 4: use Mutex which adds a lock to synchronize operations
        // to a value in multiple threads (coroutines in different contexts)
        // TODO: OUTPUT IS NOT PRODUCED
        var counter4 = 0
        val mutex = Mutex()
        runBlocking {
            withContext(Dispatchers.Default) {
                repeat(100) {
                    repeat(1000) {
                        // ensure all operations on counter2 are performed from within the one context at a time
                        mutex.lock {
                            counter4++
                        }
                    }
                }
            }
            Log.i(SHARED_STATE_ACTIVITY_TAG, "Final value of counter4 is: $counter4")
            /*
            OUTPUT:
             */
        }
    }
}