package api

import kotlin.reflect.KClass

interface Equatable<U : UnitLike<U>, N: Number<N>> {
    fun quantity() : N
    fun unit() : U
    fun evaluate()
}

interface EquatableWithUnit<U : Unit<U>, N: Number<N>> : Equatable<U,N>{
    operator fun <OU : Unit<OU>> div(other: EquatableWithUnit<OU, N>): Division<U, OU, Div<U,OU>, N> = EquatableDivision.unitDivisionByUnitOperation(this, other)
    //operator fun <OU : Unit<OU>> div(other: UnitOnlyEquatable<OU>): Division<U, OU, Div<U,OU>,N> = DivisionArithmetic.unitDivisionByUnitOperation(this, other)
    //operator fun div(other: UnitOnlyEquatable<U>): UnitlessDivision<U, U, N> = DivisionArithmetic.sameUnitDivisionOperation(this, other)
    operator fun div(other: EquatableWithNoUnit<N>): AmountOperation<U, NoUnit, U, N> = EquatableDivision.unitDivisionByNoUnitOperation(this, other)
    operator fun div(other: EquatableWithUnit<U, N>): UnitlessDivision<U,U, N> = EquatableDivision.sameUnitDivisionOperation(this, other)

    operator fun <OU : Unit<OU>> times(other: EquatableWithUnit<OU, N>): Multiplication<U, OU, N> = Multiplication(this::quantity, other::quantity, this::unit, other::unit)
    operator fun <OU : Unit<OU>> times(other: UnitOnlyEquatable<OU>): Multiplication<U, OU, N> = Multiplication(this::quantity, this.quantity()::getUnary, this::unit, other::unit)
    operator fun times(other: EquatableWithNoUnit<N>): MultiplicationO2WithNoUnit<U, N> = MultiplicationO2WithNoUnit(this::quantity, other::quantity, this::unit)

    operator fun plus(other: EquatableWithUnit<U,N>): Addition<U,N> = Addition(this::quantity, other::quantity, this::unit)
    operator fun minus (other: EquatableWithUnit<U,N>): Subtraction<U,N> = Subtraction(this::quantity, other::quantity, this::unit)
}
inline fun <reified N : Number<N>, U: Unit<U>> EquatableWithUnit<U, N>.div(other: UnitOnlyEquatable<U>): UnitlessDivision<U, U, N> = EquatableDivision.sameUnitDivisionOperation(this, other)
inline fun <reified N : Number<N>, U: Unit<U>, OU: Unit<OU>> EquatableWithUnit<U, N>.div(other: UnitOnlyEquatable<OU>): Division<U, OU, Div<U,OU>,N> = EquatableDivision.unitDivisionByUnitOperation(this, other)

interface EquatableWithNoUnit<N: Number<N>> : Equatable<NoUnit,N>{
    override fun unit(): NoUnit = NoUnit
    operator fun <OU : Unit<OU>> div(other: EquatableWithUnit<OU, N>): Division<NoUnit, OU, Div<NoUnit,OU>, N> = EquatableDivision.noUnitDivisionByUnitOperation(this,other)
    //operator fun <OU : Unit<OU>> div(other: UnitOnlyEquatable<OU>): Division<NoUnit, OU, Div<NoUnit,OU>, N> = DivisionArithmetic.noUnitDivisionByOnlyUnitOperation(this, other)
    operator fun div(other: EquatableWithNoUnit<N>): UnitlessDivision<NoUnit,NoUnit, N> = EquatableDivision.sameUnitDivisionOperation(this,other)

    operator fun <OU : Unit<OU>> times(other: EquatableWithUnit<OU, N>): MultiplicationO1WithNoUnit<OU, N> = MultiplicationO1WithNoUnit(this::quantity, other::quantity, other::unit)
    operator fun <OU : Unit<OU>> times(other: UnitOnlyEquatable<OU>): UnitfulAmount<OU,N> = UnitfulAmount(this::quantity, other::unit)
    operator fun times(other: EquatableWithNoUnit<N>): NoUnitYieldingMultiplication<N> = NoUnitYieldingMultiplication(this::quantity, other::quantity)

    operator fun plus(other: EquatableWithNoUnit<N>): NoUnitYieldingAddition<N> = NoUnitYieldingAddition(this::quantity, other::quantity)
    operator fun minus (other: EquatableWithNoUnit<N>): NoUnitYieldingSubtraction<N> = NoUnitYieldingSubtraction(this::quantity, other::quantity)
}
inline fun <reified N : Number<N>, OU: Unit<OU>> EquatableWithNoUnit<N>.div(other: UnitOnlyEquatable<OU>): Division<NoUnit, OU, Div<NoUnit,OU>,N> = EquatableDivision.unitDivisionByUnitOperation(this, other)

interface UnitOnlyEquatable<U : Unit<U>> {
    fun unit() : U
    operator fun <OU : Unit<OU>, N: Number<N>> div(other: EquatableWithUnit<OU, N>): UnitfulRatio<U, OU, N> = UnitfulRatio(other.quantity()::getUnary, other::quantity, this::unit, other::unit)
    //operator fun <N: Number<N>> div(other: EquatableWithNoUnit<N>): HardRatio<U, NoUnit, N> = Ratio(other.quantity()::getUnary, other::quantity, this::unit, {->NoUnit})

    operator fun <OU : Unit<OU>, N: Number<N>> times(other: EquatableWithUnit<OU, N>): Multiplication<U, OU, N> = Multiplication(other.quantity()::getUnary, other::quantity, this::unit, other::unit)
    operator fun <N: Number<N>> times(other: EquatableWithNoUnit<N>): UnitfulAmount<U,N> = UnitfulAmount(other::quantity, this::unit)

    operator fun <N: Number<N>> plus(other: EquatableWithUnit<U,N>): Addition<U,N> = Addition(other.quantity()::getUnary, other::quantity, this::unit)
    operator fun <N: Number<N>> minus(other: EquatableWithUnit<U,N>): Subtraction<U,N> = Subtraction(other.quantity()::getUnary, other::quantity, this::unit)
}
inline fun <reified N : Number<N>, U: Unit<U>> UnitOnlyEquatable<U>.div(other: EquatableWithNoUnit<N>): AmountOperation<U, NoUnit, U, N> = EquatableDivision.unitDivisionByNoUnitOperation(this, other)
inline fun <reified N : Number<N>, U: Unit<U>> UnitOnlyEquatable<U>.unaryAmount(c : KClass<N>) = UnitfulAmount({->UnaryNumber.getUnary(c)}, this::unit)