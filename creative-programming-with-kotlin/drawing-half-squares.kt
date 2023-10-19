        var i = 5
        while(i > 0) {
            repeat(i) {
                print("#")
            }
            println()
            i--
        }
        /*
        OUTPUT:
#####
####
###
##
#
         */
        var a = 1
        repeat(4) {
            repeat(a) {
                print("#")
            }
            println()
                a++
        }
        a=3
        repeat(3) {
            repeat(a) {
                print("#")
            }
            println()
            a--
        }
        /*
        OUTPUT:
#
##
###
####
###
##
#
         */