import api.*
import api.Unit
import memoization.memoize
import java.math.BigDecimal
import kotlin.system.measureTimeMillis


object Second : Unit<Second>
object Newton : Unit<Newton>
object Km : Unit<Km>
fun main(args : Array<String>){
    //val calculation1 = ((2.0.d * Km) / (2.0.denominator * Second)) Xdec ((2.0.denominator * Newton) / (2.0.denominator * Km))
    //val calculation2 = ((2.0.d * Second) / (2.0.denominator * Km)) Xinc ((2.0.denominator * Km) / (2.0.denominator * Newton))
    //val calculation3 = calculation1 * calculation2
    //val result = calculation3.quantity()
    //val unit = calculation3.unit()

    //val time1 = measureTimeMillis({->for(i in 1..20000000){((10.b * Km) / (500.0.b * Second)).quantity()}})
    //val time2 = measureTimeMillis({->for(i in 1..20000000){( 10.bd/ 500.bd)}})

    //println(time1.toString() + " vs " + time2)

    //println(result.toString() + " of " + unit.toString())
    //val calculation1 = measureTimeMillis({->for(i in 1..20000){impl.Div.of(impl.Div.of(impl.Div.of(Km,Newton),Newton),impl.Div.of(Second,Newton))}})
    //val calculation2 = measureTimeMillis({->for(i in 1..20000){impl.Div(impl.Div(impl.Div(Km,Newton),Newton),impl.Div(Second,Newton))}})
    //println(calculation1.toString() + " vs " + calculation2)
    //println(impl.Div.cache.size)
/*
    fun chocolate(a:BigDecimal, b: BigDecimal) {
        Thread.sleep(100)
        a * b
    }

    println(measureTimeMillis({->chocolate(100.bd, 200.bd)}))
    println(measureTimeMillis({->chocolate(100.bd, 200.bd)}))
    val chocolate3 = ::chocolate.memoize(10)
    println(measureTimeMillis({->chocolate3.invoke(300.bd, 400.bd)}))
    println(measureTimeMillis({->chocolate3.invoke(300.bd, 400.bd)}))
*/
    val multResult = measureTimeMillis({->multiplication({->2.0.d},{->2.0.d},{->Second}, {->Newton}).quantity()})
    println(multResult)
    val multResult3 = measureTimeMillis({->multiplication({->2.0.d},{->2.0.d},{->Second}, {->Newton}).quantity()})
    println(multResult3)


}
