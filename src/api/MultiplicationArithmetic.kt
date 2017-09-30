package api

open class Multiplication<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> ResultUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) :
        AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class MultiplicationInclineCancellation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> ResultUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) :
        Multiplication<Operand1Unit, Operand2Unit, ResultUnit, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

open class MultiplicationDeclineCancellation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> ResultUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) :
        Multiplication<Operand1Unit, Operand2Unit, ResultUnit, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

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

open class MultiplicationFullCancellation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> NoUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) :
        UnitlessMultiplication<Operand1Unit, Operand2Unit, N>(quantity, unit, operand1, operand2, operand1Unit, operand2Unit)

object RawMultiplicationArithmetic{

    inline fun <O1Unit : Unit<O1Unit>,
            O2Unit : Unit<O2Unit>,
            N : Number<N>> multiplication(crossinline o1: () -> N,
                                          crossinline o2: () -> N,
                                          crossinline o1Unit: () -> O1Unit,
                                          crossinline o2Unit: () -> O2Unit): Multiplication<O1Unit, O2Unit, Times<O1Unit, O2Unit>, N> {
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
                { o1n: () -> N, o2n: () -> N, o1u: () -> O1Unit, o2u: () -> NoUnit, resultn, resultu -> Multiplication(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <N : Number<N>> multiplication(crossinline o1: () -> N,
                                          crossinline o2: () -> N): UnitlessMultiplication<NoUnit, NoUnit, N> {
        return Arithmetic.noUnitYieldingSameUnitOperation(o1, o2, {NoUnit},
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
            : Multiplication<Div<O1Numerator, O1Denominator>,
                             Div<O2Numerator, O2Denominator>,
                             Div<Times<O1Numerator, O2Numerator>, Times<O1Denominator, O2Denominator>>,
                             N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: Div<O1Numerator, O1Denominator>, u2: Div<O2Numerator, O2Denominator> -> u1 * u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<O1Numerator, O1Denominator>,
                  o2u: () -> Div<O2Numerator, O2Denominator>,
                  resultn, resultu -> Multiplication(resultn, resultu, o1n, o2n, o1u, o2u) }
        )
    }

    inline fun <UnitToCancel : Unit<UnitToCancel>,
            O1Denominator : Unit<O1Denominator>,
            O2Numerator : Unit<O2Numerator>,
            N : Number<N>> ratioMultiplicationWithInclineCancelation(crossinline o1: () -> N,
                                                                     crossinline o2: () -> N,
                                                                     crossinline o1Unit: () -> Div<UnitToCancel, O1Denominator>,
                                                                     crossinline o2Unit: () -> Div<O2Numerator, UnitToCancel>): MultiplicationInclineCancellation<Div<UnitToCancel, O1Denominator>,Div<O2Numerator, UnitToCancel>, Div<O2Numerator, O1Denominator>, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<UnitToCancel, O1Denominator>, u2: Div<O2Numerator, UnitToCancel> -> u1 Xdec u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<UnitToCancel, O1Denominator>,
                  o2u: () -> Div<O2Numerator, UnitToCancel>,
                  resultn, resultu -> MultiplicationInclineCancellation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <UnitToCancel : Unit<UnitToCancel>,
            O1Numerator : Unit<O1Numerator>,
            O2Denominator : Unit<O2Denominator>,
            N : Number<N>> ratioMultiplicationWithDeclineCancelation(crossinline o1: () -> N,
                                                                     crossinline o2: () -> N,
                                                                     crossinline o1Unit: () -> Div<O1Numerator, UnitToCancel>,
                                                                     crossinline o2Unit: () -> Div<UnitToCancel, O2Denominator>): MultiplicationDeclineCancellation<Div<O1Numerator, UnitToCancel>, Div<UnitToCancel, O2Denominator>, Div<O1Numerator, O2Denominator>, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<O1Numerator, UnitToCancel>, u2: Div<UnitToCancel, O2Denominator> -> u1 Xinc u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<O1Numerator, UnitToCancel>,
                  o2u: () -> Div<UnitToCancel, O2Denominator>,
                  resultn, resultu -> MultiplicationDeclineCancellation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }


    inline fun <UnitToCancel1 : Unit<UnitToCancel1>,
            UnitToCancel2 : Unit<UnitToCancel2>,
            N : Number<N>> ratioMultiplicationWithFullCancellation(
            crossinline o1: () -> N,
            crossinline o2: () -> N,
            crossinline o1Unit: () -> Div<UnitToCancel1, UnitToCancel2>,
            crossinline o2Unit: () -> Div<UnitToCancel2, UnitToCancel1>): MultiplicationFullCancellation<Div<UnitToCancel1, UnitToCancel2>, Div<UnitToCancel2, UnitToCancel1>, N> {
        return Arithmetic.unitlessOperation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 * n2 },
                { u1: Div<UnitToCancel1, UnitToCancel2>, u2: Div<UnitToCancel2, UnitToCancel1> -> u1 * u2 },
                { o1n: () -> N,
                  o2n: () -> N,
                  o1u: () -> Div<UnitToCancel1, UnitToCancel2>,
                  o2u: () -> Div<UnitToCancel2, UnitToCancel1>,
                  resultn, resultu -> MultiplicationFullCancellation(resultn, resultu, o1n, o2n, o1u, o2u) })
    }
}

object BaseMultiplicationDivision {
    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <Operand1Unit : Unit<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : Equatable<Operand2Unit, N>,
            N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                   operand2: Operand2): Multiplication<Operand1Unit, Operand2Unit, Times<Operand1Unit, Operand2Unit>, N> {
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
            Operand1 : Equatable<OperandUnit, N>,
            Operand2 : EquatableWithNoUnit<N>,
            N : Number<N>> multiplicationOperation(operand1: Operand1,
                                                   operand2: Operand2): AmountOperation<OperandUnit, NoUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> OperandUnit, o2Unit: () -> NoUnit -> RawMultiplicationArithmetic.multiplication(o1, o2, o1Unit, o2Unit) }
        )
    }
}

object MultiplicationDivision {
    /**
     *  Equatable Operation
     *
     *  unit1/unit2 = unit1/unit2
     *
     *  @param o1 numerator with unit
     *  @param o2 denominator with unit
     *  @return division with numerator unit/ denominator unit as unit
     */
    fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : EquatableWithUnit<Operand2Unit, N>,
            N : Number<N>> unitDivisionByUnitOperation(o1: Operand1,
                                                       o2: Operand2): Division<Operand1Unit, Operand2Unit, Div<Operand1Unit, Operand2Unit>, N> {
        return BaseEquatableDivision.divisionOperation(o1, o2)
    }
}