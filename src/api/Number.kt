package api

import java.math.BigDecimal
import java.math.RoundingMode

interface Number<Self : Number<Self>> : SelfArithmeticOperable<Self>, EquatableWithNoUnit<Self>{
    fun getUnary() : Self
    fun rem(other: Self): Self
}

data class BDNumber(val number : BigDecimal) : Number<BDNumber>{
    override fun quantity(): BDNumber = this
    override fun minus(other: BDNumber): BDNumber = of(this.number - other.number)
    override fun plus(other: BDNumber): BDNumber = of(this.number + other.number)
    override fun div(other: BDNumber): BDNumber = of(this.number / other.number)
    override fun times(other: BDNumber): BDNumber = of(this.number * other.number)
    override fun rem(other: BDNumber): BDNumber = of(this.number % other.number)
    override fun getUnary() : BDNumber = BDNumber.of(1.bd)
    companion object {
        fun of(number : BigDecimal) : BDNumber = BDNumber(number)
    }
}

data class DNumber(val number : Double) : Number<DNumber> {
    override fun quantity(): DNumber = this
    override fun minus(other: DNumber): DNumber = of(this.number - other.number)
    override fun plus(other: DNumber): DNumber = of(this.number + other.number)
    override fun div(other: DNumber): DNumber = of(this.number / other.number)
    override fun times(other: DNumber): DNumber = of(this.number * other.number)
    override fun rem(other: DNumber): DNumber = of(this.number % other.number)
    override fun getUnary() : DNumber = DNumber.of(1.0)
    companion object {
        fun of(number : Double) : DNumber = DNumber(number)
    }
}

object Numbers {
    val Double.d: DNumber
        get() = DNumber.of(this)
    val Int.d: DNumber
        get() = DNumber.of(this.toDouble())
    val Double.b: BDNumber
        get() = BDNumber(BigDecimal(this).setScale(3, RoundingMode.HALF_UP))
    val Int.b: BDNumber
        get() = BDNumber(BigDecimal(this).setScale(3, RoundingMode.HALF_UP))
}