/*
Topic of Investigation: Advanced Flows in Kotlin
CAUTION: execute each block separately (uncommenting should be enough)
 */
package com.salmakhd.android.forpractice

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.time.LocalDate
import java.util.GregorianCalendar

const val MAIN_ACTIVITY_TAG = "Main Activity Tag"
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // types:
        // 1: cold flow

        // 2. as...
        val numbersFlow = (1 until 10).asFlow()
        // .flowOf()
        val valuesFlow = flowOf(1 until 10)
        val valuesFlowTwo = flow {
            for(i in 0 until 100) {
                delay(1000)
                emit(i)
            }
        }

//        GlobalScope.launch {
//            // flows cannot be canceled directly, but since their collection must
//            // happen from within a coroutine, we can cancel the parent coroutine
//            // say after 5 seconds
//            withTimeout(5000) {
//                valuesFlowTwo.collect { i ->
//                    Log.i(MAIN_ACTIVITY_TAG, "The current of i: $i")
//                }
//            }
//        }
        /*
        OUTPUT:
        2023-09-11 12:47:11.358 24233-24264 Main Activity Tag       com.salmakhd.android.forpractice     I  The current of i: 0
        2023-09-11 12:47:12.359 24233-24264 Main Activity Tag       com.salmakhd.android.forpractice     I  The current of i: 1
        2023-09-11 12:47:13.361 24233-24264 Main Activity Tag       com.salmakhd.android.forpractice     I  The current of i: 2
        2023-09-11 12:47:14.362 24233-24264 Main Activity Tag       com.salmakhd.android.forpractice     I  The current of i: 3
         */

        // collect vs. collectLatest
//        lifecycleScope.launch {
//            valuesFlowTwo.collect {i ->
//                delay(2000)
//                Log.i(MAIN_ACTIVITY_TAG, "Current value of i is: $i" )
//            }
//        }
        /*
        OUTPUT:
        2023-09-11 12:56:45.842 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 0
        2023-09-11 12:56:48.845 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 1
        2023-09-11 12:56:51.848 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 2
        2023-09-11 12:56:54.850 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 3
        2023-09-11 12:56:57.853 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 4
        2023-09-11 12:57:00.857 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 5
        2023-09-11 12:57:03.860 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 6
        2023-09-11 12:57:06.862 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 7
        .
        .
        .
        2023-09-11 12:57:06.862 24737-24737 Main Activity Tag       com.salmakhd.android.forpractice     I  Current value of i is: 99
         */

        // transform function: can be used to emit new values
//        lifecycleScope.launch {
//            valuesFlowTwo
//                .filter {
//                    it %2 == 0
//                }
//                .transform {
//                    emit(it)
//                    emit(it*it)
//                }
//                .collect {
//                    Log.i(MAIN_ACTIVITY_TAG, "Emission value is: $it")
//                }
//        }
        /*
        PARTIAL OUTPUT:
        2023-09-11 13:15:36.991 25682-25682 Main Activity Tag       com.salmakhd.android.forpractice     I  Emission value is: 0
        2023-09-11 13:15:36.991 25682-25682 Main Activity Tag       com.salmakhd.android.forpractice     I  Emission value is: 0
        2023-09-11 13:15:38.997 25682-25682 Main Activity Tag       com.salmakhd.android.forpractice     I  Emission value is: 2
        2023-09-11 13:15:38.997 25682-25682 Main Activity Tag       com.salmakhd.android.forpractice     I  Emission value is: 4
        2023-09-11 13:15:41.003 25682-25682 Main Activity Tag       com.salmakhd.android.forpractice     I  Emission value is: 4
        2023-09-11 13:15:41.003 25682-25682 Main Activity Tag       com.salmakhd.android.forpractice     I  Emission value is: 16
         */

        /* .buffer -> used to store values that haven't been collected yet
        -> emission and collection will happen one at a time otherwise (this applies to cold flows only!)
         */
        val myflowThree = flow {
            delay(200)
            repeat(10) {
                emit(1)
            }
        }

//        lifecycleScope.launch {
//            myflowThree
//                .buffer() // the upstream flow won't wait for collection of the previous emitted value to happen first
//                .collect {
//                }
//        }

        // state flow: a hot flow in Kotlin -> do not confuse it with Compose flows!
        val myStateFlow = MutableStateFlow(0)
        lifecycleScope.launch {
            for (i in 0..20) {
                delay(1000)
                myStateFlow.emit(i)
            }
            
        }
        // what happens to not-yet collected values in this case? they will be stored
        // in memory if no buffering mechanisms are in place!
        lifecycleScope.launch {
            // collect values at a lower speed
            myStateFlow.collectLatest {
                delay(2000)
                withContext(Dispatchers.Main) {
                    Log.i(MAIN_ACTIVITY_TAG, "The value that was just collected is: $it")
                }
            }
        }
        /*
        OUTPUT:
        2023-09-14 06:28:02.692  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 0
        2023-09-14 06:28:04.693  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 1
        2023-09-14 06:28:06.695  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 2
        2023-09-14 06:28:08.697  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 4
        2023-09-14 06:28:10.701  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 6
        2023-09-14 06:28:12.702  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 8
        2023-09-14 06:28:14.705  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 10
        2023-09-14 06:28:16.708  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 12
        2023-09-14 06:28:18.711  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 14
        2023-09-14 06:28:20.713  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 16
        2023-09-14 06:28:22.715  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 18
        2023-09-14 06:28:24.717  4974-4974  Main Activity Tag       com.salmakhd.android.forpractice     I  The value that was just collected is: 20
         */

        setContent {
        }
    }
}
