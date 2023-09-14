package com.salmakhd.android.forpractice

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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.GregorianCalendar

const val MAIN_ACTIVITY_TAG = "Main Activity Tag"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // tied to the current activity
        lifecycleScope.launch {
            // perform some operations
            /*
             will continue to execute if when another activity is started. -> the activity will remain in the activity stack
             call finish() to prevent this behavior
             */
        }

        // launch a coroutine that observes the lifecycle of the current activity
        lifecycleScope.launchWhenStarted {
            Log.i(MAIN_ACTIVITY_TAG, "This is executed as soon as the activity is started. (visible)")
        }
        /*
        OUTPUT:
    2023-09-10 02:42:46.836  7536-7536  Main Activity Tag       com.salmakhd.android.forpractice     I  This is executed as soon as the activity is started. (visible)
         */

        lifecycleScope.launch(Dispatchers.IO + CoroutineName("MyCoroutine")) {
            // use 'this' to access the context that is created when a CoroutineScope is created.
            Log.i(MAIN_ACTIVITY_TAG, "The name of this coroutine is: ${this.coroutineContext[CoroutineName.Key].toString()}")

        }
        /*
        OUTPUT: // PAY ATTENTION TO THE TIMESTAMP OF THIS OUTPUT AND THE PREVIOUS ONE
        2023-09-10 02:42:46.822  7536-7565  Main Activity Tag       com.salmakhd.android.forpractice     I  The name of this coroutine is: CoroutineName(MyCoroutine)
         */

        // jobs in coroutines
        runBlocking {
            val job = lifecycleScope.launch {
                // check to see if the job is still active
                if (isActive) {
                    repeat(100) {
                        Log.i(MAIN_ACTIVITY_TAG, "printing something")
                    }
                }
            }

            delay(1000)
            job.cancel()
        }
        /*
        OUTPUT:
          2023-09-10 02:46:08.223  7677-7677  Main Activity Tag       com.salmakhd.android.forpractice     I  printing something
        2023-09-10 02:46:08.226  7677-7677  Main Activity Tag       com.salmakhd.android.forpractice     I  printing something
         */

        // INTERESTING POINT: runBlocking will freeze the ui until its job is complete, so launching any
        // coroutines from within the same scope will result in the coroutine being executed only after
        // runBlocking has returned --> very very important for tests that use runBlocking!
        runBlocking {
            GlobalScope.launch(Dispatchers.Main) {
                Log.i(
                    MAIN_ACTIVITY_TAG,
                    "whereas this line is executed as soon as the coroutine is launched in the global scope"
                )
            }

            Log.i(MAIN_ACTIVITY_TAG, "This line will obviously be printed before any other one.")
            launch(Dispatchers.Main) {
                Log.i(MAIN_ACTIVITY_TAG, "This line is printed AFTER runBlocking has returned.")
            }

            lifecycleScope.launch(Dispatchers.Main) {
                Log.i(
                    MAIN_ACTIVITY_TAG,
                    "wonder when this line is printed? this is not gonna wait "
                )
            }
            Log.i(
                MAIN_ACTIVITY_TAG,
                "This line is printed as the last log statement in runBlocking{} block."
            )
            delay(2000)
        }
        /*
        OUTPUT:
        2023-09-10 02:52:54.907  8159-8159  Main Activity Tag       com.salmakhd.android.forpractice     I  This line will obviously be printed before any other one.
        2023-09-10 02:52:54.908  8159-8159  Main Activity Tag       com.salmakhd.android.forpractice     I  This line is printed as the last log statement in runBlocking{} block.
            */

        // async: can be used for executing coroutines in parallel and using their results only after they have returned
            lifecycleScope.launch {
                val randomNumberOne = async {
                    delay(2000)
                    3
                }

                val randomNumberTwo = async {
                    delay(1000)
                    4
                }
                Log.i(
                    MAIN_ACTIVITY_TAG,
                    "This will be printed only after both functions have returned. Result: ${randomNumberOne.await() + randomNumberTwo.await()}"
                )
        }
    /*
    OUTPUT:
    2023-09-10 02:54:41.403  8292-8292  Main Activity Tag       com.salmakhd.android.forpractice     I  This will be printed only after both functions have returned. Result: 7

     */

        // CoroutineExceptionHandler: a way to handle errors in launch{} blocks
        val errorHandler = CoroutineExceptionHandler {coroutineContext, throwable ->
            // log a message when an exception is thrown
            Log.i(MAIN_ACTIVITY_TAG,"An exception was just caught!")
        }
        GlobalScope.launch(Dispatchers.Main + errorHandler) {
            Log.i(MAIN_ACTIVITY_TAG, "About to throw an exception!")
            throw IllegalArgumentException()
        }

        /*
        OUTPUT: // USE TRY AND CATCH BLOCKS FOR ALL OTHER COROUTINE BUILDERS
        2023-09-10 02:55:58.782  8407-8407  Main Activity Tag       com.salmakhd.android.forpractice     I  About to throw an exception!
        2023-09-10 02:55:58.783  8407-8407  Main Activity Tag       com.salmakhd.android.forpractice     I  An exception was just caught!
         */


        setContent {
        }
    }
}