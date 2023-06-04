package com.salmakhd.android.forpractice.KotlinFlows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    // stateflow:
    // used to keep state
    // hot flow
    // can emit a single value
    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    // create a shared flow
    // hot flow
    // not fired when rotations happens
    // can be collected by multiple collectors
    // one-time emissions
    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5) // caching events
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }

    val countDownFlow = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while(currentValue > 0) {
            delay(1000)
            currentValue--
            // emitted to the
            emit(currentValue)
        }
    }

    init {
        collectFlow()
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000)
                println("First FLOW: The received number is $it")
            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000)
                println("Second FLOW: The received number is $it")
            }
        }
        // hot flows will not emit events if there are no collectors
        squareNumber(2)

    }

    @OptIn(FlowPreview::class)
    private fun collectFlow() {
        viewModelScope.launch {
            val result = countDownFlow
                // instruct the flow to manipulate collected values before emitting them
                // it returns a flow
                .filter { time ->
                    // collect even numbers only
                    time%2==0
                }
                .map { time ->
                    time*time
                }
                .onEach { time ->

                }
                // so-called terminal flow operators: they will do something will all the values emitted
                // it will collect the flow -> no need for calling collect
                /* example 1
            // ex.1
            .count {time ->
                // it will count the values that satisfy this condition
                time%2==0

            }
            println("result is $result")
                 */
                    /*
                // example 2
                .reduce { accumulator, value ->
                    accumulator+value
                }
                     */
            // example 3
                    // fold set the accumulator to the initial value passed as a parameter to fold function
                .fold(initial = 100) {accumulator, value ->
                    accumulator+value

                }
            println("result is $result")

//                .collect { time ->
//                    println("Emitted time is: $time")
//                }
        }

        // flatenning: combining 2 or more flows into a single flow
        val flow1 = flow {
            delay(1000)
            emit(1)
        }
        viewModelScope.launch {
            flow1.flatMapConcat {value ->
                flow {
                    emit(value+2)
                    delay(3000)
                    emit(value+4)
                }
            }.collect{value ->
                println("value is $value")
            }
        }
        // equivalent to
        countDownFlow
            .onEach {

            }.launchIn(viewModelScope)

        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main Dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach {
                println("Flow $it is delivered")
            }
                .buffer() // flow will emit value before the previous collect block is finished completely (collect block and flow emission executed in separate coroutines)
                .conflate() // it will skip the rest of collect statements when a new emission happens (collect block and flow emission executed in separate coroutine)
                .collect {
                println("Now eating $it")
                delay(1500L)
                println("Now finished eating $it")
            }
        }
    }
}