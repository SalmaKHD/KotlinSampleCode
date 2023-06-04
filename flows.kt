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

    }
}

