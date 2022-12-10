/*
* Topic of Investigation: Functions in Kotlin
 */

class Functions {
    // declare a property to be used in an infix member function
    var num:Int = 10

    // define a member function
    fun printMe()
    {
        println("Just doing nothing!")
    }

    // define a member function with default arguments
    fun printParams(param1: Int = 3, param2: String, param3: (Int)->Boolean)
    {
        println("The value of the first param is: $param1," +
                "\nand the value of the second param is: $param2," +
                "\nand the value of the return type of the third param is: ${param3(param1)}")
    }

    // Infix notation
    infix fun add(rightOperand: Int):Int = num+rightOperand
}

// define a function with the same signature as the param3 parameter in
// printParams() member function (will be used as a callable reference)
fun isZero(number:Int): Boolean = number == 0

// define a function with variable number of arguments
fun varNumberOfArgsTest(vararg param:String)
{
    print("This set of arguments' values are:")
    for (e in param)
        print(" $e")
    println()
}

// tail recursive functions
// Precondition: no negative values allowed to be passed.
tailrec fun recursiveFun(number:Int) {
    if (number == 0) {
        println("Recursive call was just stopped...")
    }
    else {
        println("Current value of number is: $number")
        recursiveFun(number -1)
    }
}

fun main(args:Array<String>)
{
    // call a member function from outside its class
    Functions().printMe()
    /*
    OUTPUT:
    Just doing nothing!
     */

    // call a function with named arguments
    // ::isZero -> callable reference
    Functions().printParams(param1 = 4, param2 = "Sally", param3 = ::isZero)
    /*
    OUTPUT:
    The value of the first param is: 4,
    and the value of the second param is: Sally,
    and the value of the return type of the third param is: false
     */

    // alternative call
    Functions().printParams(4, "Sally") {
        it == 0
    }
    /*
    OUTPUT:
    The value of the first param is: 4,
    and the value of the second param is: Sally,
    and the value of the return type of the third param is: false
     */

    // call a function with variable number of arguments
    varNumberOfArgsTest(*arrayOf("Sally", "Soheil"))
    /*
    OUTPUT:
    This set of arguments' values are: Sally Soheil
     */

    // Method2:
    var names:Array<String> = Array<String>(2) {
        "Sally"
    }
    varNumberOfArgsTest(*names) // * -> spread operator
    /*
    OUTPUT:
    This set of arguments' values are: Sally Sally
     */

    // invoke the infix member of the Functions class
    var num:Functions = Functions()
    println("The result of invoking the infix fun is: ${num add 2}")
    /*
    OUTPUT:
    The result of invoking the infix fun is: 12
     */

    // call an efficient recursive function
    recursiveFun(10)
    /*
    OUTPUT:
    Current value of number is: 10
    Current value of number is: 9
    Current value of number is: 8
    Current value of number is: 7
    Current value of number is: 6
    Current value of number is: 5
    Current value of number is: 4
    Current value of number is: 3
    Current value of number is: 2
    Current value of number is: 1
    Recursive call was just stopped...
     */
}

