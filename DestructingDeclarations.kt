/*
Topic of Investigation: Destructing Declarations
Explanation: These declarations are used for decomposing an object in a statement or lambda expression.
 */

/*
Note: data classes define componentN() methods by default
 */
data class Car (
    val name:String,
    val model:String,
    val productionYear:Int
    )

fun main()
{
    // destructing declaration usage 1:
    /*
    Explanation:
    .component1(), .component2() and .component3() methods called on the object automatically
     */
    val (carName, carModel, carProductionYear) = Car("Accent", "N90M21", 2020)
    println("This car's name is: $carName\n" +
            "This car's model is: $carModel\n" +
            "This car's production year is: $carProductionYear\n")
    /*
    OUTPUT:
    This car's name is: Accent
    This car's model is: N90M21
    This car's production year is: 2020
     */

    // alternative declaration
    val (car2Name,_, car2ProductionYear) = Car("RX", "M908IU", 2021)
    println("This car's name is: $car2Name\n" +
            "This car's model is: UNKNOWN\n" +
            "This car's production year is: $car2ProductionYear\n")
    /*
    OUTPUT:
    This car's name is: RX
    This car's model is: UNKNOWN
    This car's production year is: 2021
     */

    // destructing declaration usage 2:
    val printCarInfo: (Car) -> Unit = { (name, model, productionYear) ->
        println("This car's name is: $name\n" +
                "This car's model is: $model\n" +
                "This car's production year is: $productionYear\n")
    }
    printCarInfo(Car("Suzuki", "GW345T", 2017))
    /*
    OUTPUT:
    This car's name is: Suzuki
    This car's model is: GW345T
    This car's production year is: 2017
     */
}