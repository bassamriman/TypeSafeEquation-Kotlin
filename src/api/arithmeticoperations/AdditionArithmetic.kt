package api.arithmeticoperations

import api.*
import api.Number
import api.Unit
import api.equatables.EquatableWithNoUnit
import api.equatables.EquatableWithUnit
import api.equatables.UnitOnlyEquatable

open class Addition<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> Plus<Operand1Unit, Operand2Unit>,
        val numerator: () -> N,
        val denominator: () -> N,
        val numeratorUnit: () -> Operand1Unit,
        val denominatorUnit: () -> Operand2Unit) :
        UnitfulAmountOperation<Operand1Unit, Operand2Unit, Plus<Operand1Unit, Operand2Unit>, N>(quantity, unit, numerator, denominator, numeratorUnit, denominatorUnit)

object RawAdditionArithmetic {
    /**
     *  Raw operation
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            N : Number<N>> addition(crossinline o1: () -> N,
                                    crossinline o2: () -> N,
                                    crossinline o1Unit: () -> Operand1Unit,
                                    crossinline o2Unit: () -> Operand2Unit): Addition<Operand1Unit, Operand2Unit, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 + n2 },
                { u1: Operand1Unit, u2: Operand2Unit -> u1 + u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> Operand1Unit, o2u: () -> Operand2Unit, resultn, resultu -> Addition(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <OperandUnit : Unit<OperandUnit>,
            N : Number<N>> addition(crossinline o1: () -> N,
                                    crossinline o2: () -> N,
                                    crossinline operandUnit: () -> OperandUnit): UnitfulAmountOperation<OperandUnit, OperandUnit, OperandUnit, N> {
        return Arithmetic.operation(
                o1,
                o2,
                operandUnit,
                operandUnit,
                { n1: N, n2: N -> n1 + n2 },
                { u1: OperandUnit, u2: OperandUnit -> u1 + u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> OperandUnit, o2u: () -> OperandUnit, resultn, resultu -> UnitfulAmountOperation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <
            N : Number<N>> addition(crossinline o1: () -> N,
                                    crossinline o2: () -> N): UnitlessAmountOperation<NoUnit, NoUnit, N> {
        return Arithmetic.noUnitYieldingSameUnitOperation(
                o1,
                o2,
                { NoUnit },
                { n1: N, n2: N -> n1 + n2 },
                { u1: NoUnit, u2: NoUnit -> u1 + u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> NoUnit, o2u: () -> NoUnit, resultn, resultu -> UnitlessAmountOperation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }
}

object EquatableAddition {
    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            Operand1 : EquatableWithUnit<Operand1Unit, N>,
            Operand2 : EquatableWithUnit<Operand2Unit, N>,
            N : Number<N>> additionOperation(operand1: Operand1,
                                             operand2: Operand2): Addition<Operand1Unit, Operand2Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawAdditionArithmetic.addition(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    inline fun <Operand1Unit : Unit<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : UnitOnlyEquatable<Operand1Unit>,
            Operand2 : EquatableWithUnit<Operand2Unit, N>,
            reified N : Number<N>> additionOperation(operand1: Operand1,
                                                     operand2: Operand2): Addition<Operand1Unit, Operand2Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawAdditionArithmetic.addition(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : EquatableWithUnit<OperandUnit, N>,
            Operand2 : EquatableWithUnit<OperandUnit, N>,
            N : Number<N>> additionOperation(operand1: Operand1,
                                             operand2: Operand2): UnitfulAmountOperation<OperandUnit, OperandUnit, OperandUnit, N> {
        return Arithmetic.equatableOperationYieldingSameUnit(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, unit: () -> OperandUnit -> RawAdditionArithmetic.addition(o1, o2, unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    inline fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : UnitOnlyEquatable<OperandUnit>,
            Operand2 : EquatableWithUnit<OperandUnit, N>,
            reified N : Number<N>> additionOperation(operand1: Operand1,
                                                     operand2: Operand2): UnitfulAmountOperation<OperandUnit, OperandUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, unit: () -> OperandUnit -> RawAdditionArithmetic.addition(o1, o2, unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <Operand1 : EquatableWithNoUnit<N>,
            Operand2 : EquatableWithNoUnit<N>,
            N : Number<N>> additionOperation(operand1: Operand1,
                                             operand2: Operand2): UnitlessAmountOperation<NoUnit, NoUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, unit: () -> NoUnit -> RawAdditionArithmetic.addition(o1, o2) }
        )
    }
}
