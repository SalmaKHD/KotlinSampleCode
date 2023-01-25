/*
* Topic of investigation: Builder Design Pattern in Kotlin
* Why useful?
* >> this design is useful for classes that need to define a large number of parameters in their constructors
* >> this design encapsulates the logic for setting up an instance of a class inside another class
 */

class Person private constructor(builder:Builder) {
    // declare members
    var name:String? = null
    var age:Int? = null
    var address:String? = null
    var sinNumber:String? = null
    var phoneNumber:String? = null

    init {
        // initialize members
        name = builder.getName()
        age = builder.getAge()
        address = builder.getAddress()
        sinNumber = builder.getSinNumber()
        phoneNumber = builder.getPhoneNumber()
    }

    class Builder {
        // declare members
        private var name:String? = null
        private var age:Int? = null
        private var address:String? = null
        private var sinNumber:String? = null
        private var phoneNumber:String? = null

        // define setters
        // why use apply{} ? because we want the object to be returned at the end
        fun setName(_name:String) = apply { this.name = _name}
        fun setAge(_age:Int) = apply { this.age = _age}
        fun setAddress(_address:String) = apply { this.address = _address}
        fun setSinNumber(_sinNumber:String) = apply { this.sinNumber = _sinNumber}
        fun setPhoneNumber(_phoneNumber:String) = apply { this.phoneNumber = _phoneNumber}

        // define getters
        fun getName() = name
        fun getAge() = age
        fun getAddress() = address
        fun getSinNumber() = sinNumber
        fun getPhoneNumber() = phoneNumber

        // define .build() function
        fun build():Person = Person(this)

        //override .toString() method
        override fun toString(): String {
            return "This person's info is as follows:\n" +
                    "Name: $name\n" +
                    "Age: $age\n" +
                    "Address: $address\n" +
                    "SIN Number: $sinNumber\n" +
                    "Phone Number: $phoneNumber"
        }
    }
}

fun main()
{
    // create an object using this pattern
    val salma = Person.Builder()
        .setName("Salma")
        .setAge(21)
        .setAddress("192 Steve Dr, ON, 143OC5")
        .setSinNumber("1435675")
        .setPhoneNumber("8746578978")

    // print results
    println(salma)
    /*
    OUTPUT:
    This person's info is as follows:
    Name: Salma
    Age: 21
    Address: 192 Steve Dr, ON, 143OC5
    SIN Number: 1435675
    Phone Number: 8746578978
     */
}