package api.arithmeticoperations

import api.*
import api.Number
import api.Unit

open class Multiplication<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> Times<Operand1Unit, Operand2Unit>,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) :
        AmountOperation<Operand1Unit, Operand2Unit, Times<Operand1Unit, Operand2Unit>, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class DivisionFromMultiplicationDeclineCancellation<
        UnitToCancel : UnitLike<UnitToCancel>,
        NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> Div<NumeratorUnit, DenominatorUnit>,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Div<UnitToCancel, DenominatorUnit>,
        override val operand2Unit: () -> Div<NumeratorUnit, UnitToCancel>) :
        Ratio<NumeratorUnit, DenominatorUnit, N>,
        AmountOperation<Div<UnitToCancel, DenominatorUnit>,
                Div<NumeratorUnit, UnitToCancel>,
                Div<NumeratorUnit, DenominatorUnit>,
                N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class DivisionFromRatioMultiplication<
        Numerator1Unit : UnitLike<Numerator1Unit>,
        Denominator1Unit : UnitLike<Denominator1Unit>,
        Numerator2Unit : UnitLike<Numerator2Unit>,
        Denominator2Unit : UnitLike<Denominator2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> Div<Times<Numerator1Unit, Numerator2Unit>, Times<Denominator1Unit, Denominator2Unit>>,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Div<Numerator1Unit, Denominator1Unit>,
        override val operand2Unit: () -> Div<Numerator2Unit, Denominator2Unit>) :
        AmountOperation<Div<Numerator1Unit, Denominator1Unit>,
                Div<Numerator2Unit, Denominator2Unit>,
                Div<Times<Numerator1Unit, Numerator2Unit>, Times<Denominator1Unit, Denominator2Unit>>,
                N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class DivisionFromMultiplicationInclineCancellation<
        UnitToCancel : UnitLike<UnitToCancel>,
        NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> Div<NumeratorUnit, DenominatorUnit>,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Div<NumeratorUnit, UnitToCancel>,
        override val operand2Unit: () -> Div<UnitToCancel, DenominatorUnit>) :
        Ratio<NumeratorUnit, DenominatorUnit, N>,
        AmountOperation<Div<NumeratorUnit, UnitToCancel>,
                Div<UnitToCancel, DenominatorUnit>,
                Div<NumeratorUnit, DenominatorUnit>, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class UnitlessMultiplication<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> NoUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) :
        UnitlessAmountOperation<Operand1Unit, Operand2Unit, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class MultiplicationFullCancellation<NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> NoUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Div<NumeratorUnit, DenominatorUnit>,
        override val operand2Unit: () -> Div<DenominatorUnit, NumeratorUnit>) :
        UnitlessMultiplication<Div<NumeratorUnit, DenominatorUnit>, Div<DenominatorUnit, NumeratorUnit>, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

object RawMultiplicationArithmetic {

    inline fun <O1Unit : Unit<O1Unit>,
            O2Unit : Unit<O2Unit>,
            N : Number<N>> multiplication(crossinline o1: () -> N,
                                          crossinline o2: () -> N,
                                          crossinline o1Unit: () -> O1Unit,
                                          crossinline o2Unit: () -> O2Unit): Multiplication<O1Unit, O2Unit, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: O1Unit, u2: O2Unit -> u1 * u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> O1Unit, o2u: () -> O2Unit, resultn, resultu -> Multiplication(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <O1Unit : Unit<O1Unit>,
            N : Number<N>> multiplication(crossinline o1: () -> N,
                                          crossinline o2: () -> N,
                                          crossinline o1Unit: () -> O1Unit,
                                          crossinline o2Unit: () -> NoUnit): AmountOperation<O1Unit, NoUnit, O1Unit, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: O1Unit, u2: NoUnit -> u1 * u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> O1Unit, o2u: () -> NoUnit, resultn, resultu -> AmountOperation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <O2Unit : Unit<O2Unit>,
            N : Number<N>> noUnitO1multiplication(crossinline o1: () -> N,
                                                  crossinline o2: () -> N,
                                                  crossinline o1Unit: () -> NoUnit,
                                                  crossinline o2Unit: () -> O2Unit): AmountOperation<NoUnit, O2Unit, O2Unit, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: NoUnit, u2: O2Unit -> u1 * u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> NoUnit, o2u: () -> O2Unit, resultn, resultu -> AmountOperation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <N : Number<N>> multiplication(crossinline o1: () -> N,
                                              crossinline o2: () -> N): UnitlessMultiplication<NoUnit, NoUnit, N> {
        return Arithmetic.noUnitYieldingSameUnitOperation(o1, o2, { NoUnit },
                { n1: N, n2: N -> n1 * n2 },
                { u1: NoUnit, u2: NoUnit -> u1 * u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> NoUnit, o2u: () -> NoUnit, resultn, resultu -> UnitlessMultiplication(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <O1Numerator : Unit<O1Numerator>,
            O1Denominator : Unit<O1Denominator>,
            O2Numerator : Unit<O2Numerator>,
            O2Denominator : Unit<O2Denominator>,
            N : Number<N>> ratioMultiplication(crossinline o1: () -> N,
                                               crossinline o2: () -> N,
                                               crossinline o1Unit: () -> Div<O1Numerator, O1Denominator>,
                                               crossinline o2Unit: () -> Div<O2Numerator, O2Denominator>)
            : DivisionFromRatioMultiplication<O1Numerator, O1Denominator, O2Numerator, O2Denominator, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: Div<O1Numerator, O1Denominator>, u2: Div<O2Numerator, O2Denominator> -> u1 * u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<O1Numerator, O1Denominator>,
                  o2u: () -> Div<O2Numerator, O2Denominator>,
                  resultn, resultu ->
                    DivisionFromRatioMultiplication(resultn, resultu, o1n, o2n, o1u, o2u)
                }
        )
    }

    inline fun <UnitToCancel : UnitLike<UnitToCancel>,
            O1Denominator : UnitLike<O1Denominator>,
            O2Numerator : UnitLike<O2Numerator>,
            N : Number<N>> ratioMultiplicationWithDeclineCancellation(crossinline o1: () -> N,
                                                                      crossinline o2: () -> N,
                                                                      crossinline o1Unit: () -> Div<UnitToCancel, O1Denominator>,
                                                                      crossinline o2Unit: () -> Div<O2Numerator, UnitToCancel>): DivisionFromMultiplicationDeclineCancellation<UnitToCancel, O2Numerator, O1Denominator, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<UnitToCancel, O1Denominator>, u2: Div<O2Numerator, UnitToCancel> -> u1 Xdec u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<UnitToCancel, O1Denominator>,
                  o2u: () -> Div<O2Numerator, UnitToCancel>,
                  resultn, resultu ->
                    DivisionFromMultiplicationDeclineCancellation(resultn, resultu, o1n, o2n, o1u, o2u)
                })
    }

    inline fun <UnitToCancel : UnitLike<UnitToCancel>,
            O1Numerator : UnitLike<O1Numerator>,
            O2Denominator : UnitLike<O2Denominator>,
            N : Number<N>> ratioMultiplicationWithInclineCancellation(crossinline o1: () -> N,
                                                                      crossinline o2: () -> N,
                                                                      crossinline o1Unit: () -> Div<O1Numerator, UnitToCancel>,
                                                                      crossinline o2Unit: () -> Div<UnitToCancel, O2Denominator>): DivisionFromMultiplicationInclineCancellation<UnitToCancel, O1Numerator, O2Denominator, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<O1Numerator, UnitToCancel>, u2: Div<UnitToCancel, O2Denominator> -> u1 Xinc u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<O1Numerator, UnitToCancel>,
                  o2u: () -> Div<UnitToCancel, O2Denominator>,
                  resultn, resultu ->
                    DivisionFromMultiplicationInclineCancellation(resultn, resultu, o1n, o2n, o1u, o2u)
                })
    }


    inline fun <NumeratorUnit : UnitLike<NumeratorUnit>,
            DenominatorUnit : UnitLike<DenominatorUnit>,
            N : Number<N>> ratioMultiplicationWithFullCancellation(
            crossinline o1: () -> N,
            crossinline o2: () -> N,
            crossinline o1Unit: () -> Div<NumeratorUnit, DenominatorUnit>,
            crossinline o2Unit: () -> Div<DenominatorUnit, NumeratorUnit>): MultiplicationFullCancellation<NumeratorUnit, DenominatorUnit, N> {
        return Arithmetic.unitlessOperation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: Div<NumeratorUnit, DenominatorUnit>, u2: Div<DenominatorUnit, NumeratorUnit> -> u1 * u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<NumeratorUnit, DenominatorUnit>,
                  o2u: () -> Div<DenominatorUnit, NumeratorUnit>,
                  resultn, resultu ->
                    MultiplicationFullCancellation(resultn, resultu, o1n, o2n, o1u, o2u)
                })
    }
}

object EquatableMultiplication {
    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <Operand1Unit : Unit<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : Equatable<Operand2Unit, N>,
            N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                   operand2: Operand2): Multiplication<Operand1Unit, Operand2Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawMultiplicationArithmetic.multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : EquatableWithUnit<OperandUnit, N>,
            Operand2 : EquatableWithNoUnit<N>,
            N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                   operand2: Operand2): AmountOperation<OperandUnit, NoUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> OperandUnit, o2Unit: () -> NoUnit -> RawMultiplicationArithmetic.multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : EquatableWithNoUnit<N>,
            Operand2 : EquatableWithUnit<OperandUnit, N>,
            N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                   operand2: Operand2): AmountOperation<NoUnit, OperandUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> NoUnit, o2Unit: () -> OperandUnit -> RawMultiplicationArithmetic.noUnitO1multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

    inline fun <Operand1Unit : Unit<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : EquatableWithUnit<Operand1Unit, N>,
            Operand2 : UnitOnlyEquatable<Operand2Unit>,
            reified N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                           operand2: Operand2): Multiplication<Operand1Unit, Operand2Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawMultiplicationArithmetic.multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

    inline fun <Operand2Unit : Unit<Operand2Unit>,
            Operand1 : EquatableWithNoUnit<N>,
            Operand2 : UnitOnlyEquatable<Operand2Unit>,
            reified N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                           operand2: Operand2): AmountOperation<NoUnit, Operand2Unit, Operand2Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> NoUnit, o2Unit: () -> Operand2Unit -> RawMultiplicationArithmetic.noUnitO1multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

    fun <Operand : EquatableWithNoUnit<N>,
            N : Number<N>> multiplicationOperation(operand1: Operand,
                                                   operand2: Operand): UnitlessAmountOperation<NoUnit, NoUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, _: () -> NoUnit -> RawMultiplicationArithmetic.multiplication(o1, o2) }
        )
    }

    inline fun <Operand1Unit : Unit<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : UnitOnlyEquatable<Operand1Unit>,
            Operand2 : EquatableWithUnit<Operand2Unit, N>,
            reified N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                           operand2: Operand2): Multiplication<Operand1Unit, Operand2Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawMultiplicationArithmetic.multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

    inline fun <Operand1Unit : Unit<Operand1Unit>,
            Operand1 : UnitOnlyEquatable<Operand1Unit>,
            Operand2 : EquatableWithNoUnit<N>,
            reified N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                           operand2: Operand2): AmountOperation<Operand1Unit, NoUnit, Operand1Unit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> NoUnit -> RawMultiplicationArithmetic.multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }

}
