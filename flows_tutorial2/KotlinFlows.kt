/*
Topic of investigation: flows in Kotlin
 */
package com.salmakhd.android.forpractice.KotlinFlowsCourse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigInteger

const val Flows_TAG = "Flows Tag"
class KotlinFlows: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // print intermediate values of factorial calculation in one list
        Log.i(
            Flows_TAG,
            "The list of intermediate values are: ${calculateFactorialOfAndReturnList(6)}"
        )
        /*
        OUTPUT:
        The list of intermediate values are: [1, 2, 6, 24, 120, 720] // not asynchronous, not a stream
         */

        // print individual intermediate values using a sequence: not asynchronous either
        Log.i(Flows_TAG, "The individual intermediate values are:")
        calculateFactorialOfAndReturnSequence(6).forEach { value ->
            Log.i(Flows_TAG, "$value\n")
        }

        /*
        OUTPUT:
        The individual intermediate values are: // values are all calculated on the main thread
        1
        2
        6
        24
        120
        720
         */

        // solution? kotlin flows! for building asynchronous data streams!
        Log.i(Flows_TAG, "The asynchronously calculated values are now: ")
        runBlocking {
            launch {
                calculateFactorialOfAndReturnFlow(6).collect { value ->
                    Log.i(Flows_TAG, "$value\n")
                }
            }
        }
    }

    /**
     * logic: build a list and add intermediate values to it.
     * not asynchronous
     */
    private fun calculateFactorialOfAndReturnList(number: Int): List<BigInteger> =
        buildList { // buildList: a list builder
            var factorial = BigInteger.ONE
            for (i in 1..number) {
                Thread.sleep(2000)
                factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
                // add intermediate value to the list
                add(factorial)
            }
        }
    /*
    OUTPUT:
   The asynchronously calculated values are now:
    1
    2
    6
    24
    120
    720
     */

    /**
     * logic: build a sequence and return results in a stream.
     * not asynchronous
     */
    private fun calculateFactorialOfAndReturnSequence(number: Int): Sequence<Int> = sequence {
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            Thread.sleep(2000)
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            // add intermediate value to the list
            yield(factorial.toInt())
        }
    }

    /**
     * logic: build a flow that will emit values individually and asynchronously
     * for returning intermediate values
     */
    private fun calculateFactorialOfAndReturnFlow(number: Int): Flow<Int> = flow { // flows fully support coroutines
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            delay(2000)
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            // add intermediate value to the list
            emit(factorial.toInt())
        }
    }.flowOn(Dispatchers.Default)
}