package api

import api.equatables.EquatableWithNoUnit
import memoization.memoize
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.reflect.KClass

interface Number<Self : Number<Self>> : SelfArithmeticOperable<Self>, EquatableWithNoUnit<Self> {
    override fun evaluate() = Unit
    fun getUnary(): Self
    fun rem(other: Self): Self
}

object UnaryNumber {
    inline fun <reified N : Number<N>> getUnary(numberType: KClass<N>): N {
        return when (numberType) {
            BDNumber::class -> BDNumber.getUnary() as N
            DNumber::class -> DNumber.getUnary() as N
            else -> throw IllegalArgumentException()
        }
    }
}

data class BDNumber(val number: BigDecimal) : Number<BDNumber> {
    override fun quantity(): BDNumber = this
    override fun minus(other: BDNumber): BDNumber = of(this.number - other.number)
    override fun plus(other: BDNumber): BDNumber = of(this.number + other.number)
    override fun div(other: BDNumber): BDNumber = of(this.number / other.number)
    override fun times(other: BDNumber): BDNumber = of(this.number * other.number)
    override fun rem(other: BDNumber): BDNumber = of(this.number % other.number)
    override fun getUnary(): BDNumber = BDNumber.of(1.bd)

    companion object {
        val memoizeBDNumber = this::unmemoiziedOf.memoize(100)
        private fun unmemoiziedOf(number: BigDecimal): BDNumber = BDNumber(number)
        fun of(number: BigDecimal): BDNumber = memoizeBDNumber.invoke(number)
        fun getUnary(): BDNumber = BDNumber.of(1.bd)
        inline fun bigDecimalNumericalOperation(o1: () -> BigDecimal, o2: () -> BigDecimal, numberOperation: (BigDecimal, BigDecimal) -> BigDecimal) = numberOperation(o1.invoke(), o2.invoke())
        val bigDecimalNumericalOperationMem = this::bigDecimalNumericalOperation.memoize()
    }
}

data class DNumber(val number: Double) : Number<DNumber> {
    override fun quantity(): DNumber = this
    override fun minus(other: DNumber): DNumber = of(this.number - other.number)
    override fun plus(other: DNumber): DNumber = of(this.number + other.number)
    override fun div(other: DNumber): DNumber = of(this.number / other.number)
    override fun times(other: DNumber): DNumber = of(this.number * other.number)
    override fun rem(other: DNumber): DNumber = of(this.number % other.number)
    override fun getUnary(): DNumber = DNumber.of(1.0)

    companion object {
        val memoizeBDNumber = this::unmemoiziedOf.memoize(100)
        private fun unmemoiziedOf(number: Double): DNumber = DNumber(number)
        fun of(number: Double): DNumber = memoizeBDNumber.invoke(number)
        fun getUnary(): DNumber = DNumber.of(1.0)
        inline fun doubleNumericalOperation(o1: () -> Double, o2: () -> Double, numberOperation: (Double, Double) -> Double) = numberOperation(o1.invoke(), o2.invoke())
        val doubleNumericalOperationMem = this::doubleNumericalOperation.memoize()
    }
}

object Numbers {
    val Double.d: DNumber
        get() = DNumber.of(this)
    val Int.d: DNumber
        get() = DNumber.of(this.toDouble())
    val Double.b: BDNumber
        get() = BDNumber.of(BigDecimal(this).setScale(3, RoundingMode.HALF_UP))
    val Int.b: BDNumber
        get() = BDNumber.of(BigDecimal(this).setScale(3, RoundingMode.HALF_UP))
}