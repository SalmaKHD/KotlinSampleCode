/*
* Topic of Investigation: Type aliases
* Type aliases are used to shorten the names of long types, or other similar purposes.
 */

class TypeAliases {
    inner class Inner
}

// type alias for an inner class
typealias MyInnerClass = TypeAliases.Inner
// type alias for a standard type
typealias MyOwnVersionOfString = String
// type alias for a parameterized type
typealias MyArrayListType<K> = ArrayList<K>
// type alias for a function type
typealias MyFunType = (Int, String) -> Double

fun main(args:Array<String>)
{
    // declare a variable using MyInnerClass
    var var1 = TypeAliases().MyInnerClass()
    println(var1)
    /*
    OUTPUT:
    TypeAliases$Inner@2f4d3709
     */

    // declare a variable using MyOwnVersionOfString
    var var2:MyOwnVersionOfString = MyOwnVersionOfString()
    println(var2 is String)
    /*
    OUTPUT:
    true
     */

    // declare a variable using MyMutableListType<K>
    var var3:MyArrayListType<String> = MyArrayListType<String>()
    println(var3 is ArrayList<String>)
    /*
    OUTPUT:
    true
     */

    // declare a variable using MyFunType
    var fun1:MyFunType = fun(param1:Int, param2:String): Double {return 0.0}
    println(fun1)
    /*
    OUTPUT:
    Function2<java.lang.Integer, java.lang.String, java.lang.Double>
     */
}
