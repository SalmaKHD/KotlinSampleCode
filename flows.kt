/*
Topics od investigation:
1. flows in Kotlin
*/

package com.salmakhd.forpracticelocal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException

const val MAIN_ACTIVITY_TAG = "Main"
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create a flow using a flow builder
        val myFlow: Flow<Int> = flow {
            for (i in 1..3) {
                emit(i)
            }
        }
        runBlocking {
            myFlow.collectLatest {number ->
                println("collected number is: $number")

            }
        }
        // create a flow using another flow builder
        val mySecondFlow: Flow<Int> = (1..3).asFlow()
        runBlocking {
            mySecondFlow.collect{
                // same result
            }
        }

        // intermediate flow operators
        //  runBlocking {
        /*
        key point: .map does not have to be executed inside a coroutine scope, why because myFlow is a cold flow, a flow object is returned only
         */
        val myFlow3: Flow<Int> = flow {
            Log.i(MAIN_ACTIVITY_TAG, "just entered the block of flow")
            emit(2)
            delay(2000)
            Log.i(MAIN_ACTIVITY_TAG,"executing last line of flow")

        }
        myFlow
            // executed within a coroutine when collect is called
            // the function itself however returns a flow object only
            //
            .map {
            }
        // }

        runBlocking {
            launch {
                myFlow3
                    .map { it ->
                        Log.i(MAIN_ACTIVITY_TAG, "inside the map block now...")
                        it*2
                    }
                    .collect {
                        Log.i(MAIN_ACTIVITY_TAG, "collected value is $it")
                    }
            }

            // define a more genera transformation method
            launch {
                myFlow3
                    .transform { number ->
                        emit(3)
                        emit(3)
                    }
                    .collect {number ->
                        Log.i(MAIN_ACTIVITY_TAG, "Collected value after transform() is: $number")

                    }
            }
        }

        // size-limiting intermediate operators
        runBlocking {
            (1..3).asFlow()
                // execution of the flow is terminated when limit is reached
                .take(2)
                .collect {
                    Log.i(MAIN_ACTIVITY_TAG, "Collected value, take() used: $it")
                }

        }

        // terminal operators: operators that collect emitted values
        /*
        terminal operator examples: reduce(), fold(), toList(), toSet(), first(),
        most common one: collect()
         */
        runBlocking {
            val finalResult = (1..3).asFlow()
                .map{
                    it*2
                }
                // a flow collector
                .reduce{ accumulator, number ->
                    accumulator+number // the result will be a single value
                }
            Log.i(MAIN_ACTIVITY_TAG, "The final value with reduce() is: $finalResult")
        }

        // intermediate flow operators are executed sequentially
        runBlocking {
            (1..6).asFlow()
                .filter {it ->
                    Log.i(MAIN_ACTIVITY_TAG, "Inside filter now. Current value is: $it")
                    it % 3 == 0
                }
                .map {number ->
                    Log.i(MAIN_ACTIVITY_TAG, "Inside map{} now. Current value is: $number")
                    "Just floating aimlessly"
                }
                .collect {it ->
                    Log.i(MAIN_ACTIVITY_TAG,"Inside collect{} now. Current value is: $it")
                }
        }

        // flow blocks always run n the context specified by the collector
        val myFlow4: Flow<Int> = flow {
            /*
            CAUTION: DON'T ADD THE FOLLOWING CODE,
            FLOW WILL NOT EMIT ANY VALUES
             */
            // withContext(Dispatchers.IO) {emit(4)}
            Log.i(MAIN_ACTIVITY_TAG, "Current thread inside flow builder is: ${Thread.currentThread()}")
            emit(4)
        }
        runBlocking {
            withContext(Dispatchers.IO) {
                myFlow4.collect {it->
                    Log.i(MAIN_ACTIVITY_TAG, "Current thread inside collect is: ${Thread.currentThread()}")
                }
            }
        }
        /*
        OUTPUT:
         Current thread inside flow builder is: Thread[DefaultDispatcher-worker-2,5,main
         Current thread inside collect is: Thread[DefaultDispatcher-worker-2,5,main]
         */

        // change flow emission context without getting an exception
        val myFlow5 = flow {
            Log.i(MAIN_ACTIVITY_TAG, "This time the current thread from flow is: ${Thread.currentThread()}")
            emit(4)
        }.flowOn(Dispatchers.IO)
        lifecycleScope.launch(Dispatchers.Main) {
            myFlow5.collect {it ->
                Log.i(MAIN_ACTIVITY_TAG, "This time the current thread from collector is: ${Thread.currentThread()}")
            }
        }
        /*
        // conclusion: when upstream and downstream flows' contexts differ, they will be run
        // concurrently in different coroutines <- important! No problem if you change coroutine context in the collector block
        OUTPUT:
         This time the current thread from flow is: Thread[DefaultDispatcher-worker-3,5,main]
         This time the current thread from collector is: Thread[main,5,main]
         */

        // run upstream flow and downstream flow concurrently while buffering emitted values for
        // the collector to consume when the coroutine is available

        runBlocking {
            (1..5).asFlow()
                // this will ensure that flow emits even when the collector is not done
                // with previous values yet
                .buffer()
                .collect {
                    // pretend there is some long-running operation here
                    delay(5000)
                    // values will be buffered as the flow emits them at a faster pace than the collector
                }
        }

        // run upstream flow and down stream flow concurrently
        // to process only the most recent value emitted by the upstream flow
        val myFlow6 = flow {
            // fastest possible emissions
            emit(5)
            emit(6)
            emit(7)
        }
        runBlocking {
            myFlow6
                .conflate()
                .collect { it ->
                    delay(3000)
                    Log.i(MAIN_ACTIVITY_TAG, "The emission that made it to this line is: $it")

                }
        }
        /*
        // conclusion: 5 was first emitted and collected immediately, then a 5 sec delay was executed
        // during which 6 was emitted, but dropped because before the collector could process it 7 was emitted
        // then processed as usual
        OUTPUT:
        The emission that made it to this line is: 5
        The emission that made it to this line is: 7
         */

        // do the same thing the other way around, meaning collection should be canceled when
        // new values are emitted and restarted immediately
        runBlocking {
            (1..4).asFlow()
                .collectLatest {
                    delay(2000)
                    Log.i(
                        MAIN_ACTIVITY_TAG,
                        "Guess which number made it to this line? of course it's $it"
                    )
                }
        }
        /*
        // conclusion: by the time the collector processes earlier values, new values are emitted,
        therefore the collector's coroutine is restarted with the most recent value
        OUTPUT:
        Guess which number made it to this line? of course it's 4
         */

        // combine flows
        // method 1: .zip{} : combines values from two flows
        val flow1 = (1..3).asFlow()
        val flow2 = flow {
            emit("Salma")
            emit("Philippe")
            emit("Sogand")

        }
        runBlocking {
            flow1.zip(flow2) { a, b ->
                "$a -> $b"
            }
                .collect {
                    Log.i(MAIN_ACTIVITY_TAG, "The final emitted value is: $it")
                }
        }

        /*
        OUTPUT:
The final emitted value is: 1 -> Salma
The final emitted value is: 2 -> Philippe
The final emitted value is: 3 -> Sogand
         */

        // use combine to combine values this time
        val flow3: Flow<Int> = flow {
            emit(1)
            delay(5000)
            emit(2)
            delay(3000)
            emit(3)
            delay(5000)
            emit(4)
        }
        val flow4: Flow<Int> = flow {
            emit(1)
            emit(2)
            emit(3)
        }

        // let's compare .zip() with .combine()

        // let's do .combine() first
        runBlocking {
            flow3
                .combine(flow4) { a, b ->
                    "$a -> $b"
                }
                .collect {
                    Log.i(MAIN_ACTIVITY_TAG, "COMBINE: a->b is: $it")
                }
        }
        /*
        OUTPUT:
         COMBINE: a->b is: 1 -> 1
         COMBINE: a->b is: 1 -> 2
         COMBINE: a->b is: 1 -> 3
         COMBINE: a->b is: 2 -> 3
         COMBINE: a->b is: 3 -> 3
         COMBINE: a->b is: 4 -> 3
         */

        runBlocking {
            flow3
                .zip(flow4) { a, b ->
                    "$a -> $b"
                }
                .collect {
                    Log.i(MAIN_ACTIVITY_TAG, "ZIP: a->b is: $it")
                }
        }

        /*
        OUTPUT:
        ZIP: a->b is: 1 -> 1
        ZIP: a->b is: 2 -> 2
        ZIP: a->b is: 3 -> 3
         */

        // flattening techniques in flows
        // 1: .flatMapConcat() -> it ensures the inner flow is executed before resuming collection
        // from the first flow
        val flow5 = (1..3).asFlow()
        val flow6 = flow {
            throw IOException()
            delay(2000)
            emit(1)
            delay(2000)
            emit(2)
        }

        // handling exceptions
        runBlocking {
            flow6
                    // catches all the exceptions thrown in the upstream flow
                .catch { Log.i(MAIN_ACTIVITY_TAG, "The exception was just caught")}
                .collect {
                    // .catch will not do anything special in this case
                    //throw IOException()
                }

            /*
            output:
                The exception was just caught
                 FATAL EXCEPTION: main Process: com.salmakhd.forpracticelocal, PID: 10245
            java.lang.RuntimeException: Unable to start activity ComponentInfo{com.salmakhd.forpracticelocal/com.salmakhd.forpracticelocal.MainActivity}: java.io.IOException
             */

            // catching bot upstream and downstream flows
            flow6
                .onEach {
                    // move all .collect() logic to here
                    // operations...
                    throw IOException() // NO PROBLEM~
                }
                .catch { e->
                    Log.i(MAIN_ACTIVITY_TAG, "Could catch the exception thrown in both streams")
                }
                .onCompletion { flowState ->
                    if (flowState != null)
                        Log.i(MAIN_ACTIVITY_TAG, "The flow executed successfully")
                    else
                        Log.i(MAIN_ACTIVITY_TAG, "An exception was detected, coroutine canceled.")
                    Log.i(MAIN_ACTIVITY_TAG, "This line is executed no matter what.")
                }
                .collect { value ->
                    // IMPORTANT: coroutine collection will be canceled when an exception is thrown
                    // this line will never get executed
                    Log.i(MAIN_ACTIVITY_TAG, "Exception was successfully handled!")
                }
            /*
            OUTPUT:
                Could catch the exception thrown in both streams
                This line is executed no matter what.
             */
        }

        // making flows cancellable
        val flow8 = flow {
            emit(2)
            delay(2000)
            emit(3)
        }
        runBlocking {
            launch {
                flow8
                    .cancellable()
                    .collect { number ->
                        Log.i(
                            MAIN_ACTIVITY_TAG,
                            "The value that made it to this line is: $number only!"
                        )
                        delay(1000)
                        cancel()
                    }
            }
        }
        /*
        OUTPUT:
        The value that made it to this line is: 2 only!
         */

        //
    }
}
