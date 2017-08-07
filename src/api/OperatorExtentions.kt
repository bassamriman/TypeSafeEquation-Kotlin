package api

import java.math.BigDecimal
import java.math.RoundingMode


val Int.bd: BigDecimal
    get() = BigDecimal(this).setScale(3, RoundingMode.HALF_UP)

val Double.bd: BigDecimal
    get() = BigDecimal(this).setScale(3, RoundingMode.HALF_UP)

val Double.d: DNumber
    get() = DNumber.of(this)
val Int.d: DNumber
    get() = DNumber.of(this.toDouble())
val Double.b: BDNumber
    get() = BDNumber(BigDecimal(this).setScale(3, RoundingMode.HALF_UP))
val Int.b: BDNumber
    get() = BDNumber(BigDecimal(this).setScale(3, RoundingMode.HALF_UP))