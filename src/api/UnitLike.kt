package api

interface UnitLike<Self : UnitLike<Self>> {
    operator fun div(other: Self): NoUnit = NoUnit
    operator fun plus(other: Self): Self = this as Self
    operator fun minus(other: Self): Self = this as Self
    operator fun times(other: NoUnit): Self = this as Self

    operator fun <TOther : UnitLike<TOther>> div(other: TOther): Div<Self,TOther> = UnitOperations.div(this as Self, other)
    operator fun <TOther : Unit<TOther>> times(other: TOther): UnitLike<*>
    operator fun <TOther : UnitLike<TOther>> plus(other: TOther): Plus<Self,TOther> = UnitOperations.plus(this as Self, other)
    operator fun <TOther : UnitLike<TOther>> minus(other: TOther): Minus<Self,TOther> = UnitOperations.minus(this as Self, other)
}

interface Unit<Self : Unit<Self>> : UnitLike<Self>, UnitOnlyEquatable<Self>{
    override fun unit(): Self = this as Self
    operator fun times(other: Self): Power2<Self> = UnitOperations.power2(this as Self)
    operator fun div(other: NoUnit): Self = this as Self
    override operator fun <TOther : Unit<TOther>> times(other: TOther): Times<Self,TOther> = UnitOperations.times(this as Self, other)
}

object NoUnit : UnitLike<NoUnit> {
    override operator fun <TOther : Unit<TOther>> times(other: TOther): TOther = other
}

//Unit Composition
interface Div<U1 : UnitLike<U1>, U2 : UnitLike<U2>> : Unit<Div<U1, U2>>, UnitOperation<U1,U2>{
    operator fun <OU1 : UnitLike<OU1>, OU2 : UnitLike<OU2>> times(other: Div<OU1, OU2>): Div<Times<U1,OU1>,Times<U2,OU2>> = impl.Div.of(impl.Times.of(this.unit1,other.unit1),impl.Times.of(this.unit2,other.unit2))
    operator fun times(other: Div<U2, U1>): NoUnit = NoUnit
    infix fun <OU : UnitLike<OU>> Xdec(other: Div<OU, U1>): Div<OU, U2> = UnitOperations.div(other.unit1, this.unit2)
    infix fun <OU : UnitLike<OU>> Xinc(other: Div<U2, OU>): Div<U1, OU> = UnitOperations.div(this.unit1, other.unit2)
}
interface UnitOperation<U1 : UnitLike<U1>, U2 : UnitLike<U2>> {
    val unit1 : U1
    val unit2 : U2
}
interface Times<U1 : UnitLike<U1>, U2 : UnitLike<U2>> : Unit<Times<U1, U2>>, UnitOperation<U1,U2>
interface Plus<U1 : UnitLike<U1>, U2 : UnitLike<U2>> : Unit<Plus<U1, U2>> , UnitOperation<U1,U2>
interface Minus<U1 : UnitLike<U1>, U2 : UnitLike<U2>> : Unit<Minus<U1, U2>> , UnitOperation<U1,U2>
interface Power2<U1 : UnitLike<U1>> : Unit<Power2<U1>>{
    val unit : U1
}

object UnitOperations{
    fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>>  div(unit1 : U1, unit2 : U2) : Div<U1,U2> = impl.Div.of(unit1, unit2)
    fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>>  times(unit1 : U1, unit2 : U2) : Times<U1,U2> = impl.Times.of(unit1, unit2)
    fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>>  plus(unit1 : U1, unit2 : U2) : Plus<U1,U2> = impl.Plus.of(unit1, unit2)
    fun <U1 : UnitLike<U1>, U2 : UnitLike<U2>>  minus(unit1 : U1, unit2 : U2) : Minus<U1,U2> = impl.Minus.of(unit1, unit2)
    fun <U1 : UnitLike<U1>> power2(unit1 : U1) : Power2<U1> = impl.Power2.of(unit1)
}





