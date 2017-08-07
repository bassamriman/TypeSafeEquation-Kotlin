package api

interface Equatable<U : UnitLike<U>, N: Number<N>> {
    fun quantity() : N
    fun unit() : U
}

interface EquatableWithUnit<U : Unit<U>, N: Number<N>> : Equatable<U,N>{
    operator fun <OU : Unit<OU>> div(other: EquatableWithUnit<OU, N>): UnitfulRatio<U, OU, N> = UnitfulRatio(this::quantity, other::quantity, this::unit, other::unit)
    operator fun div(other: EquatableWithUnit<U, N>): NoUnitYieldingSameUnitRatio<U, N> = NoUnitYieldingSameUnitRatio(this::quantity, other::quantity, this::unit)
    operator fun <OU : Unit<OU>> times(other: EquatableWithUnit<OU, N>): Multiplication<U, OU, N> = Multiplication(this::quantity, other::quantity, this::unit, other::unit)
    operator fun <OU : Unit<OU>> div(other: UnitOnlyEquatable<OU>): UnitfulRatio<U, OU, N> = UnitfulRatio(this::quantity, this.quantity()::getUnary, this::unit, other::unit)
    operator fun div(other: UnitOnlyEquatable<U>): Amount<U, N> = Amount(this::quantity, other::unit)
    operator fun <OU : Unit<OU>> times(other: UnitOnlyEquatable<OU>): Multiplication<U, OU, N> = Multiplication(this::quantity, this.quantity()::getUnary, this::unit, other::unit)
    operator fun div(other: EquatableWithNoUnit<N>): Ratio<U, NoUnit, N> = Ratio(this::quantity, other::quantity, this::unit, {->NoUnit})
    operator fun times(other: EquatableWithNoUnit<N>): MultiplicationO2WithNoUnit<U, N> = MultiplicationO2WithNoUnit(this::quantity, other::quantity, this::unit)
    operator fun plus(other: EquatableWithUnit<U,N>): Addition<U,N> = Addition(this::quantity, other::quantity, this::unit)
    operator fun minus (other: EquatableWithUnit<U,N>): Subtraction<U,N> = Subtraction(this::quantity, other::quantity, this::unit)
}

interface EquatableWithNoUnit<N: Number<N>> : Equatable<NoUnit,N>{
    override fun unit(): NoUnit = NoUnit
    operator fun <OU : Unit<OU>> div(other: EquatableWithUnit<OU, N>): Ratio<NoUnit, OU, N> = Ratio(this::quantity, other::quantity, {->NoUnit}, other::unit)
    operator fun <OU : Unit<OU>> times(other: EquatableWithUnit<OU, N>): MultiplicationO1WithNoUnit<OU, N> = MultiplicationO1WithNoUnit(this::quantity, other::quantity, other::unit)
    operator fun <OU : Unit<OU>> div(other: UnitOnlyEquatable<OU>): Ratio<NoUnit, OU, N> = Ratio(this::quantity, this.quantity()::getUnary, {->NoUnit}, other::unit)
    operator fun <OU : Unit<OU>> times(other: UnitOnlyEquatable<OU>): Amount<OU,N> = Amount(this::quantity, other::unit)
    operator fun div(other: EquatableWithNoUnit<N>): NoUnitYieldingNoUnitRatio<N> = NoUnitYieldingNoUnitRatio(this::quantity, other::quantity)
    operator fun times(other: EquatableWithNoUnit<N>): NoUnitYieldingMultiplication<N> = NoUnitYieldingMultiplication(this::quantity, other::quantity)
    operator fun plus(other: EquatableWithNoUnit<N>): NoUnitYieldingAddition<N> = NoUnitYieldingAddition(this::quantity, other::quantity)
    operator fun minus (other: EquatableWithNoUnit<N>): NoUnitYieldingSubtraction<N> = NoUnitYieldingSubtraction(this::quantity, other::quantity)
}

interface UnitOnlyEquatable<U : Unit<U>> {
    fun unit() : U
    operator fun <OU : Unit<OU>, N: Number<N>> div(other: EquatableWithUnit<OU, N>): UnitfulRatio<U, OU, N> = UnitfulRatio(other.quantity()::getUnary, other::quantity, this::unit, other::unit)
    operator fun <OU : Unit<OU>, N: Number<N>> times(other: EquatableWithUnit<OU, N>): Multiplication<U, OU, N> = Multiplication(other.quantity()::getUnary, other::quantity, this::unit, other::unit)
    operator fun <N: Number<N>> div(other: EquatableWithNoUnit<N>): Ratio<U, NoUnit, N> = Ratio(other.quantity()::getUnary, other::quantity, this::unit, {->NoUnit})
    operator fun <N: Number<N>> times(other: EquatableWithNoUnit<N>): Amount<U,N> = Amount(other::quantity, this::unit)
    operator fun <N: Number<N>> plus(other: EquatableWithUnit<U,N>): Addition<U,N> = Addition(other.quantity()::getUnary, other::quantity, this::unit)
    operator fun <N: Number<N>> minus(other: EquatableWithUnit<U,N>): Subtraction<U,N> = Subtraction(other.quantity()::getUnary, other::quantity, this::unit)
}

data class Amount<U : Unit<U>, N: Number<N>>(val quantity : () -> N, val unit: () -> U): EquatableWithUnit<U,N>{
    override fun quantity(): N = quantity.invoke()
    override fun unit(): U = unit.invoke()
    companion object {
        fun <U : Unit<U>, N: Number<N>> of(quantity : N, unit: U): Amount<U, N> = Amount({->quantity},{->unit})
    }
}

open class Operation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : Unit<ResultUnit>,
        N: Number<N>> (val operand1 : ()->N,
                       val operand2 : ()->N,
                       val operand1Unit : ()->Operand1Unit,
                       val operand2Unit : ()->Operand2Unit,
                       val numberOperation : (N,N)->N,
                       val unitOperation : (Operand1Unit, Operand2Unit) -> ResultUnit ): EquatableWithUnit<ResultUnit, N>{
    override fun quantity(): N = numberOperation(operand1.invoke(), operand2.invoke())
    override fun unit() : ResultUnit = unitOperation(operand1Unit.invoke(), operand2Unit.invoke())
}

open class NoUnitYieldingOperation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N: Number<N>> (val operand1 : ()->N,
                       val operand2 : ()->N,
                       val operand1Unit : ()->Operand1Unit,
                       val operand2Unit : ()->Operand2Unit,
                       val numberOperation : (N,N)->N,
                       val unitOperation : (Operand1Unit, Operand2Unit) -> NoUnit): EquatableWithNoUnit<N>{
    override fun quantity(): N = numberOperation(operand1.invoke(), operand2.invoke())
    override fun unit() : NoUnit = unitOperation(operand1Unit.invoke(), operand2Unit.invoke())
}