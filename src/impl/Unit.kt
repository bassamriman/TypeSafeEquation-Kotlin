package impl

import api.UnitLike
import memoization.DEFAULT_CAPACITY

data class Div<U1 : UnitLike<U1>, U2 : UnitLike<U2>>(override val unit1: U1, override val unit2: U2) : api.Div<U1, U2> {
    companion object {
        val cache: MutableMap<Pair<UnitLike<*>, UnitLike<*>>, Div<*, *>> = HashMap(10)
        /*
        fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>> of(unit1 : U1, unit2 :U2) : Div<U1,U2> {
            val unit = cache.getOrPut(unit1 to unit2, {->Div(unit1, unit2)}) as Div<U1,U2>
            //println(cache)
            return unit
        }*/
        fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>> of(unit1: U1, unit2: U2): Div<U1, U2> = Div(unit1, unit2)

        fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>> jj(unit1: U1, unit2: U2): Div<U1, U2> = memoize({ a: U1, b: U2 -> of(a, b) }).invoke(unit1, unit2)
        //val test = {a, b -> of(a,b)}.memoize()
        fun <A, B, R> memoize(function: ((A, B) -> R), initialCapacity: Int = DEFAULT_CAPACITY): (A, B) -> R {
            val cache: MutableMap<Pair<A, B>, R> = HashMap(initialCapacity)
            return { a: A, b: B ->
                cache.getOrPut(a to b, { function(a, b) })
            }
        }
    }
}

data class Times<U1 : UnitLike<U1>, U2 : UnitLike<U2>>(override val unit1: U1, override val unit2: U2) : api.Times<U1, U2> {
    companion object {
        fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>> of(unit1: U1, unit2: U2) = Times(unit1, unit2)
    }
}

data class Plus<U1 : UnitLike<U1>, U2 : UnitLike<U2>>(override val unit1: U1, override val unit2: U2) : api.Plus<U1, U2> {
    companion object {
        fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>> of(unit1: U1, unit2: U2) = Plus(unit1, unit2)
    }
}

data class Minus<U1 : UnitLike<U1>, U2 : UnitLike<U2>>(override val unit1: U1, override val unit2: U2) : api.Minus<U1, U2> {
    companion object {
        fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>> of(unit1: U1, unit2: U2) = Minus(unit1, unit2)
    }
}

data class Power2<U1 : UnitLike<U1>>(override val unit: U1) : api.Power2<U1> {
    companion object {
        fun <U1 : UnitLike<U1>> of(unit: U1) = Power2(unit)
    }
}