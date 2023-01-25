/*
Topics of Investigation: interfaces in Kotlin
 */

interface Creature {
    val name:String

    // define a default method
    fun printName() { println("This creature's name is: $name")}
}

// define a concrete class
class Human: Creature {
    // override name property (abstract in the interface by default)
    /*
    Note: properties are overridden through overriding their get() method
     */
    override val name: String
        get() = "Human"

    // override the printName() method and call the interface version as well
    override fun printName() {
        super.printName()
        println("Current Class: Human")
    }
}

fun main()
{
    // instantiate the Human class
    val human = Human()
    human.printName()
    /*
    OUTPUT:
    This creature's name is: Human
    Current Class: Human
     */
}