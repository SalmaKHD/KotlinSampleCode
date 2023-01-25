/*
Topics of Investigation: Sealed classes in Kotlin
 */

sealed class Shape
{
    // define derived class 1
    data class Rectangle(val name:String, val draw: ()->Unit): Shape()
    data class Triangle(val name:String, val draw: ()->Unit): Shape()
    // define a singleton using object declaration
    object Example {
        val name:String = "Emoji"
        val draw: ()->Unit = {print("----\n|  |\n----")}
    }
}
// define another class that extends the Shape sealed class
data class Circle(val name:String, val draw: ()->Unit) :Shape()

fun main()
{
    // draw a square
    Shape.Example.draw()
    /*
    OUTPUT:
    ----
    |  |
    ----
     */
}