package api

import api.arithmeticoperations.*
import kotlin.reflect.KClass

interface Equatable<U : UnitLike<U>, N : Number<N>> {
    fun quantity(): N
    fun unit(): U
    fun evaluate()
}

interface Ratio<NumeratorUnit : UnitLike<NumeratorUnit>, DenominatorUnit : UnitLike<DenominatorUnit>, N : Number<N>> : EquatableWithUnit<Div<NumeratorUnit, DenominatorUnit>, N> {
    operator fun times(other: Ratio<DenominatorUnit, NumeratorUnit, N>): MultiplicationFullCancellation<NumeratorUnit, DenominatorUnit, N> = RawMultiplicationArithmetic.ratioMultiplicationWithFullCancellation({ this.quantity() }, { other.quantity() }, { this.unit() }, { other.unit() })
    infix fun <OtherNumeratorUnit : Unit<OtherNumeratorUnit>> Xdec(other: Ratio<OtherNumeratorUnit, NumeratorUnit, N>): DivisionFromMultiplicationDeclineCancellation<NumeratorUnit, OtherNumeratorUnit, DenominatorUnit, N> = RawMultiplicationArithmetic.ratioMultiplicationWithDeclineCancellation({ this.quantity() }, { other.quantity() }, { this.unit() }, { other.unit() })
    infix fun <OtherDenominatorUnit : Unit<OtherDenominatorUnit>> Xinc(other: Ratio<DenominatorUnit, OtherDenominatorUnit, N>): DivisionFromMultiplicationInclineCancellation<DenominatorUnit, NumeratorUnit, OtherDenominatorUnit, N> = RawMultiplicationArithmetic.ratioMultiplicationWithInclineCancellation({ this.quantity() }, { other.quantity() }, { this.unit() }, { other.unit() })
}

interface EquatableWithUnit<U : Unit<U>, N : Number<N>> : Equatable<U, N> {
    operator fun <OU : Unit<OU>> div(other: EquatableWithUnit<OU, N>): Division<U, OU, N> = EquatableDivision.unitDivisionByUnitOperation(this, other)
    operator fun div(other: EquatableWithNoUnit<N>): AmountOperation<U, NoUnit, U, N> = EquatableDivision.unitDivisionByNoUnitOperation(this, other)
    operator fun div(other: EquatableWithUnit<U, N>): UnitlessDivision<U, U, N> = EquatableDivision.sameUnitDivisionOperation(this, other)

    operator fun <OU : Unit<OU>> times(other: EquatableWithUnit<OU, N>): Multiplication<U, OU, N> = EquatableMultiplication.multiplicationOperation(this, other)

    operator fun <OU : Unit<OU>> plus(other: EquatableWithUnit<OU, N>): Addition<U, OU, N> = EquatableAddition.additionOperation(this, other)
    operator fun <OU : Unit<OU>> minus(other: EquatableWithUnit<OU, N>): Subtraction<U, OU, N> = EquatableSubtraction.subtractionOperation(this, other)
    operator fun plus(other: EquatableWithUnit<U, N>): AmountOperation<U, U, U, N> = EquatableAddition.additionOperation(this, other)
    operator fun minus(other: EquatableWithUnit<U, N>): AmountOperation<U, U, U, N> = EquatableSubtraction.subtractionOperation(this, other)
}

inline operator fun <reified N : Number<N>, U : Unit<U>> EquatableWithUnit<U, N>.div(other: UnitOnlyEquatable<U>): UnitlessDivision<U, U, N> = EquatableDivision.sameUnitDivisionOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>, OU : Unit<OU>> EquatableWithUnit<U, N>.div(other: UnitOnlyEquatable<OU>): Division<U, OU, N> = EquatableDivision.unitDivisionByUnitOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>, OU : Unit<OU>> EquatableWithUnit<U, N>.times(other: UnitOnlyEquatable<OU>): Multiplication<U, OU, N> = EquatableMultiplication.multiplicationOperation(this, other)

interface EquatableWithNoUnit<N : Number<N>> : Equatable<NoUnit, N> {
    override fun unit(): NoUnit = NoUnit
    operator fun <OU : Unit<OU>> div(other: EquatableWithUnit<OU, N>): Division<NoUnit, OU, N> = EquatableDivision.noUnitDivisionByUnitOperation(this, other)
    operator fun div(other: EquatableWithNoUnit<N>): UnitlessDivision<NoUnit, NoUnit, N> = EquatableDivision.sameUnitDivisionOperation(this, other)

    operator fun <OU : Unit<OU>> times(other: EquatableWithUnit<OU, N>): AmountOperation<NoUnit, OU, OU, N> = EquatableMultiplication.multiplicationOperation(this, other)
    operator fun times(other: EquatableWithNoUnit<N>): UnitlessAmountOperation<NoUnit, NoUnit, N> = EquatableMultiplication.multiplicationOperation(this, other)

    operator fun plus(other: EquatableWithNoUnit<N>): UnitlessAmountOperation<NoUnit, NoUnit, N> = EquatableAddition.additionOperation(this, other)
    operator fun minus(other: EquatableWithNoUnit<N>): UnitlessAmountOperation<NoUnit, NoUnit, N> = EquatableSubtraction.subtractionOperation(this, other)
}

inline operator fun <reified N : Number<N>, OU : Unit<OU>> EquatableWithNoUnit<N>.div(other: UnitOnlyEquatable<OU>): Division<NoUnit, OU, N> = EquatableDivision.unitDivisionByUnitOperation(this, other)
inline operator fun <reified N : Number<N>, OU : Unit<OU>> EquatableWithNoUnit<N>.times(other: UnitOnlyEquatable<OU>): AmountOperation<NoUnit, OU, OU, N> = EquatableMultiplication.multiplicationOperation(this, other)

interface UnitOnlyEquatable<U : Unit<U>> {
    fun unit(): U
}

inline operator fun <reified N : Number<N>, U : Unit<U>> UnitOnlyEquatable<U>.div(other: EquatableWithNoUnit<N>): AmountOperation<U, NoUnit, U, N> = EquatableDivision.unitDivisionByNoUnitOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>, OU : Unit<OU>> UnitOnlyEquatable<U>.div(other: EquatableWithUnit<OU, N>): Division<U, OU, N> = EquatableDivision.unitDivisionByUnitOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>> UnitOnlyEquatable<U>.times(other: EquatableWithNoUnit<N>): AmountOperation<U, NoUnit, U, N> = EquatableMultiplication.multiplicationOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>, OU : Unit<OU>> UnitOnlyEquatable<U>.times(other: EquatableWithUnit<OU, N>): Multiplication<U, OU, N> = EquatableMultiplication.multiplicationOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>, OU : Unit<OU>> UnitOnlyEquatable<U>.plus(other: EquatableWithUnit<OU, N>): Addition<U, OU, N> = EquatableAddition.additionOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>> UnitOnlyEquatable<U>.plus(other: EquatableWithUnit<U, N>): AmountOperation<U, U, U, N> = EquatableAddition.additionOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>, OU : Unit<OU>> UnitOnlyEquatable<U>.minus(other: EquatableWithUnit<OU, N>): Subtraction<U, OU, N> = EquatableSubtraction.subtractionOperation(this, other)
inline operator fun <reified N : Number<N>, U : Unit<U>> UnitOnlyEquatable<U>.minus(other: EquatableWithUnit<U, N>): AmountOperation<U, U, U, N> = EquatableSubtraction.subtractionOperation(this, other)
inline fun <reified N : Number<N>, U : Unit<U>> UnitOnlyEquatable<U>.unaryAmount(c: KClass<N>) = UnitfulAmount({ -> UnaryNumber.getUnary(c) }, this::unit)