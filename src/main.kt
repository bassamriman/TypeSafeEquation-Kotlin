import api.*
import api.Unit
import kotlin.system.measureTimeMillis


object Second : Unit<Second>
object Newton : Unit<Newton>
object Km : Unit<Km>
fun main(args : Array<String>){
    val calculation1 = ((2.0.d * Km) / (2.0.d * Second)) Xdec ((2.0.d * Newton) / (2.0.d * Km))
    val calculation2 = ((2.0.d * Second) / (2.0.d * Km)) Xinc ((2.0.d * Km) / (2.0.d * Newton))
    val calculation3 = calculation1 * calculation2
    val result = calculation3.quantity()
    val unit = calculation3.unit()

    val time1 = measureTimeMillis({->for(i in 1..20000000){((10.b * Km) / (500.0.b * Second)).quantity()}})
    val time2 = measureTimeMillis({->for(i in 1..20000000){( 10.bd/ 500.bd)}})

    println(time1.toString() + " vs " + time2)

    println(result.toString() + " of " + unit.toString())
    //val calculation1 = measureTimeMillis({->for(i in 1..20000){impl.Div.of(impl.Div.of(impl.Div.of(Km,Newton),Newton),impl.Div.of(Second,Newton))}})
    //val calculation2 = measureTimeMillis({->for(i in 1..20000){impl.Div(impl.Div(impl.Div(Km,Newton),Newton),impl.Div(Second,Newton))}})
    //println(calculation1.toString() + " vs " + calculation2)
    //println(impl.Div.cache.size)
}
