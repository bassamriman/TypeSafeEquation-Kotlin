package api

import memoization.memoize

/*
inline fun numericalOperation(o1 : ()->Number<*>, o2 : ()->Number<*>, numberOperation : (Number<*>, Number<*>)->Number<*>) =
        numberOperation.invoke(o1.invoke(),o2.invoke())
val numericalOperationMem = ::numericalOperation.memoize(100)

inline fun unitOperation( o1 : ()->UnitLike<*>, o2 : ()->UnitLike<*>, uOperation : (UnitLike<*>,UnitLike<*>)-> UnitLike<*>) =
        uOperation(o1.invoke(), o2.invoke())
val unitOperationMem = ::unitOperation.memoize(100)
 */


open class Amount<U : UnitLike<U>, N : Number<N>>(open val quantity: () -> N, open val unit: () -> U) : Equatable<U, N> {
    private val lazyQuantity: N by lazy {
        quantity.invoke()
    }

    private val lazyUnit: U by lazy {
        unit.invoke()
    }

    override fun quantity() = lazyQuantity
    override fun unit() = lazyUnit
    override fun evaluate() {
        quantity()
        unit()
    }
}

open class UnitfulAmount<U : Unit<U>, N : Number<N>>(override val quantity: () -> N, override val unit: () -> U) : Amount<U, N>(quantity, unit), EquatableWithUnit<U, N> {
    companion object {
        fun <U : Unit<U>, N : Number<N>> of(quantity: N, unit: U): UnitfulAmount<U, N> = UnitfulAmount({ -> quantity }, { -> unit })
    }
}

open class UnitlessAmount<N : Number<N>>(override val quantity: () -> N, override val unit: () -> NoUnit) : Amount<NoUnit, N>(quantity, unit), EquatableWithNoUnit<N> {
    override fun unit(): NoUnit = NoUnit

    companion object {
        fun <N : Number<N>> of(quantity: N, unit: NoUnit): UnitlessAmount<N> = UnitlessAmount({ -> quantity }, { -> unit })
    }
}

interface Operation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>> {
    val operand1: () -> N
    val operand2: () -> N
    val operand1Unit: () -> Operand1Unit
    val operand2Unit: () -> Operand2Unit
}

open class AmountOperation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> ResultUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) : Amount<ResultUnit, N>(quantity, unit), Operation<Operand1Unit, Operand2Unit, N>

open class UnitlessAmountOperation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> NoUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) : UnitlessAmount<N>(quantity, unit), Operation<Operand1Unit, Operand2Unit, N>

open class UnitfulAmountYieldingOperation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : Unit<ResultUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> ResultUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) : UnitfulAmount<ResultUnit, N>(quantity, unit), Operation<Operand1Unit, Operand2Unit, N>

open class UnitlessAmountYeildingOperation<
        Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> NoUnit,
        override val operand1: () -> N,
        override val operand2: () -> N,
        override val operand1Unit: () -> Operand1Unit,
        override val operand2Unit: () -> Operand2Unit) : UnitlessAmount<N>(quantity, unit), Operation<Operand1Unit, Operand2Unit, N> {
    override fun unit(): NoUnit = NoUnit
}

object Arithmetic{

    /**
     *  Represents a number arithmetic operation
     */
    inline fun <N : Number<N>> numericalOperation(operand1: () -> N, operand2: () -> N, numberOperation: (N, N) -> N) =
            numberOperation.invoke(operand1.invoke(), operand2.invoke())

    /**
     *  Represents unit arithmetic operation
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            ResultUnit : UnitLike<ResultUnit>>
            unitOperation(operand1: () -> Operand1Unit,
                          operand2: () -> Operand2Unit,
                          unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit) =
        unitOperation(operand1(), operand2())

    /**
     *  Represents an operation that takes two operands and yields a result
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            ResultUnit : UnitLike<ResultUnit>,
            ResultAmount : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>,
            N : Number<N>>
            operation(crossinline operand1: () -> N,
                      crossinline operand2: () -> N,
                      crossinline operand1Unit: () -> Operand1Unit,
                      crossinline operand2Unit: () -> Operand2Unit,
                      crossinline numberOperation: (N, N) -> N,
                      crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                      amountCreator: (() -> N, () -> N, () -> Operand1Unit, () -> Operand2Unit, () -> N, () -> ResultUnit) -> ResultAmount)
            : ResultAmount
    {
        return amountCreator(
                { -> operand1() },
                { -> operand2() },
                { -> operand1Unit() },
                { -> operand2Unit() },
                { -> Arithmetic.numericalOperation(operand1, operand2, numberOperation) },
                { -> Arithmetic.unitOperation(operand1Unit, operand2Unit, unitOperation) }
        )
    }

    /**
     *  Represents an operation that takes two operands of the same unit and yields the no unit eg. division
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            ResultAmount : UnitlessAmountOperation<Operand1Unit, Operand2Unit, N>,
            N : Number<N>>
            unitlessOperation(crossinline operand1: () -> N,
                                            crossinline operand2: () -> N,
                                            crossinline operand1Unit: () -> Operand1Unit,
                                            crossinline operand2Unit: () -> Operand2Unit,
                                            crossinline numberOperation: (N, N) -> N,
                                            crossinline unitOperation: (Operand1Unit, Operand2Unit) -> NoUnit,
                                            amountCreator: (() -> N,
                                                            () -> N,
                                                            () -> Operand1Unit,
                                                            () -> Operand2Unit,
                                                            () -> N,
                                                            () -> NoUnit) -> ResultAmount)
            : ResultAmount
    {
        return amountCreator(
                { -> operand1() },
                { -> operand2() },
                { -> operand1Unit()},
                { -> operand2Unit() },
                { -> numericalOperation(operand1, operand2, numberOperation) },
                { -> unitOperation(operand1Unit, operand2Unit, unitOperation) })
    }

    /**
     *  Represents an operation that takes two operands of the same unit and yields the no unit eg. division
     */
    inline fun <OperandUnit : UnitLike<OperandUnit>,
            ResultAmount : UnitlessAmountOperation<OperandUnit, OperandUnit, N>,
            N : Number<N>>
            noUnitYieldingSameUnitOperation(crossinline operand1: () -> N,
                                            crossinline operand2: () -> N,
                                            crossinline operandUnit: () -> OperandUnit,
                                            crossinline numberOperation: (N, N) -> N,
                                            crossinline unitOperation: (OperandUnit, OperandUnit) -> NoUnit,
                                            amountCreator: (() -> N,
                                                            () -> N,
                                                            () -> OperandUnit,
                                                            () -> OperandUnit,
                                                            () -> N,
                                                            () -> NoUnit) -> ResultAmount)
            : ResultAmount
    {
            return amountCreator(
                    { -> operand1() },
                    { -> operand2() },
                    { -> operandUnit()},
                    { -> operandUnit() },
                    { -> numericalOperation(operand1, operand2, numberOperation) },
                    { -> unitOperation(operandUnit, operandUnit, unitOperation) })
    }

    /**
     *  Represents an operation that takes two operands of the same unit and yields the same unit eg. Addition, Subtraction
     */
    inline fun <OperandUnit : UnitLike<OperandUnit>,
            ResultAmount : AmountOperation<OperandUnit, OperandUnit, OperandUnit, N>,
            N : Number<N>> sameUnitYeildingSameUnitOperation(crossinline operand1: () -> N,
                                                             crossinline operand2: () -> N,
                                                             crossinline operand1Unit: () -> OperandUnit,
                                                             crossinline operand2Unit: () -> OperandUnit,
                                                             crossinline numberOperation: (N, N) -> N,
                                                             crossinline uOperation: (OperandUnit, OperandUnit) -> OperandUnit,
                                                             amountCreator: (() -> N, () -> OperandUnit) -> ResultAmount): ResultAmount {
        return amountCreator(
                { -> numericalOperation(operand1, operand2, numberOperation) },
                { -> unitOperation(operand1Unit, operand2Unit, uOperation) }
        )
    }

    /**
     *  Represents an operation that takes two operands
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            ResultUnit : UnitLike<ResultUnit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : Equatable<Operand2Unit, N>,
            Result : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>,
            N : Number<N>> equatableOperation(o1: Operand1,
                                              o2: Operand2,
                                              operation: (() -> N, () -> N, () -> Operand1Unit, () -> Operand2Unit) -> Result): Result {
        return operation({ -> o1.quantity() }, { -> o2.quantity() }, { -> o1.unit() }, { -> o2.unit() })
    }

    /**
     *  Represents an operation that takes two operands
     */
    inline fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : UnitOnlyEquatable<OperandUnit>,
            Operand2 : EquatableWithNoUnit<N>,
            Result : AmountOperation<OperandUnit, NoUnit, OperandUnit, N>,
            reified N : Number<N>> equatableOperation(o1: Operand1,
                                              o2: Operand2,
                                              operation: (() -> N, () -> N, () -> OperandUnit, () -> NoUnit) -> Result): Result {
        return operation({ -> o1.unaryAmount(N::class).quantity() }, { -> o2.quantity() }, { -> o1.unit() }, { -> o2.unit() })
    }

    /**
     *  Represents an operation that takes two operands
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            ResultUnit : UnitLike<ResultUnit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : UnitOnlyEquatable<Operand2Unit>,
            Result : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>,
            reified N : Number<N>> equatableOperation(o1: Operand1,
                                              o2: Operand2,
                                              operation: (() -> N, () -> N, () -> Operand1Unit, () -> Operand2Unit) -> Result): Result {
        return operation({ -> o1.quantity() }, { -> o2.unaryAmount(N::class).quantity() }, { -> o1.unit() }, { -> o2.unit() })
    }

    /**
     *  Represents an operation that takes two operands with the same unit and yield no unit
     */
    inline fun <OperandsUnit : UnitLike<OperandsUnit>,
            Operand : Equatable<OperandsUnit, N>,
            Result : UnitlessAmountOperation<OperandsUnit, OperandsUnit, N>,
            N : Number<N>> equatableOperation(o1: Operand,
                                              o2: Operand,
                                              operation: (() -> N, () -> N, () -> OperandsUnit) -> Result): Result {
        return operation({ -> o1.quantity() }, { -> o2.quantity() }, { -> o1.unit() })
    }

    /**
     *  Represents an operation that takes two operands with the same unit and yield no unit
     */
    inline fun <OperandsUnit : Unit<OperandsUnit>,
            Operand1 : EquatableWithUnit<OperandsUnit, N>,
            Operand2 : UnitOnlyEquatable<OperandsUnit>,
            Result : UnitlessAmountOperation<OperandsUnit, OperandsUnit, N>,
            reified N : Number<N>> equatableOperation(o1: Operand1,
                                              o2: Operand2,
                                              operation: (() -> N, () -> N, () -> OperandsUnit) -> Result): Result {

        return operation({ -> o1.quantity() }, { -> o2.unaryAmount(N::class).quantity() }, { -> o1.unit() })
    }


    inline fun <O1Unit : Unit<O1Unit>,
            O2Unit : Unit<O2Unit>,
            N : Number<N>> multiplication(crossinline o1: () -> N,
                                          crossinline o2: () -> N,
                                          crossinline o1Unit: () -> O1Unit,
                                          crossinline o2Unit: () -> O2Unit): UnitfulAmount<Times<O1Unit, O2Unit>, N> {
        return operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: O1Unit, u2: O2Unit -> u1 * u2 })
    }

    inline fun <O1Numerator : Unit<O1Numerator>,
            O1Denominator : Unit<O1Denominator>,
            O2Numerator : Unit<O2Numerator>,
            O2Denominator : Unit<O2Denominator>,
            N : Number<N>> ratioMultiplication(crossinline o1: () -> N,
                                               crossinline o2: () -> N,
                                               crossinline o1Unit: () -> Div<O1Numerator, O1Denominator>,
                                               crossinline o2Unit: () -> Div<O2Numerator, O2Denominator>): UnitfulAmount<Div<Times<O1Numerator, O2Numerator>, Times<O1Denominator, O2Denominator>>, N> {
        return operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<O1Numerator, O1Denominator>, u2: Div<O2Numerator, O2Denominator> -> u1 * u2 })
    }

    inline fun <UnitToCancel : Unit<UnitToCancel>,
            O1Denominator : Unit<O1Denominator>,
            O2Numerator : Unit<O2Numerator>,
            N : Number<N>> ratioMultiplicationWithInclineCancelation(crossinline o1: () -> N,
                                                                     crossinline o2: () -> N,
                                                                     crossinline o1Unit: () -> Div<UnitToCancel, O1Denominator>,
                                                                     crossinline o2Unit: () -> Div<O2Numerator, UnitToCancel>): UnitfulAmount<Div<O2Numerator, O1Denominator>, N> {
        return operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<UnitToCancel, O1Denominator>, u2: Div<O2Numerator, UnitToCancel> -> u1 Xdec u2 })
    }

    inline fun <UnitToCancel : Unit<UnitToCancel>,
            O1Numerator : Unit<O1Numerator>,
            O2Denominator : Unit<O2Denominator>,
            N : Number<N>> ratioMultiplicationWithDeclineCancelation(crossinline o1: () -> N,
                                                                     crossinline o2: () -> N,
                                                                     crossinline o1Unit: () -> Div<O1Numerator, UnitToCancel>,
                                                                     crossinline o2Unit: () -> Div<UnitToCancel, O2Denominator>): UnitfulAmount<Div<O1Numerator, O2Denominator>, N> {
        return operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<O1Numerator, UnitToCancel>, u2: Div<UnitToCancel, O2Denominator> -> u1 Xinc u2 })
    }


    inline fun <UnitToCancel1 : Unit<UnitToCancel1>,
            UnitToCancel2 : Unit<UnitToCancel2>,
            N : Number<N>> ratioMultiplicationWithFullCancellation(
            crossinline o1: () -> N,
            crossinline o2: () -> N,
            crossinline o1Unit: () -> Div<UnitToCancel1, UnitToCancel2>,
            crossinline o2Unit: () -> Div<UnitToCancel2, UnitToCancel1>): UnitlessAmount<N> {
        return noUnitYieldingOperation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: Div<UnitToCancel1, UnitToCancel2>, u2: Div<UnitToCancel2, UnitToCancel1> -> u1 * u2 })
    }

    inline fun <O1Unit : Unit<O1Unit>,
            N : Number<N>> noUnitSecondOperandMultiplication(crossinline o1: () -> N,
                                                             crossinline o2: () -> N,
                                                             crossinline o1Unit: () -> O1Unit): UnitfulAmount<O1Unit, N> {
        return operation(o1, o2, o1Unit, { -> NoUnit }, { n1: N, n2: N -> n1 * n2 }, { u1: O1Unit, u2: NoUnit -> u1 * u2 })
    }


    inline fun <O2Unit : Unit<O2Unit>,
            N : Number<N>> noUnitFirstOperandMultiplication(crossinline o1: () -> N,
                                                            crossinline o2: () -> N,
                                                            crossinline o2Unit: () -> O2Unit): UnitfulAmount<O2Unit, N> {
        return operation(o1, o2, { -> NoUnit }, o2Unit, { n1: N, n2: N -> n1 * n2 }, { u1: NoUnit, u2: O2Unit -> u1 * u2 })
    }

    inline fun <N : Number<N>> noUnitYeildingMultiplication(crossinline o1: () -> N,
                                                            crossinline o2: () -> N): UnitlessAmount<N> {
        return noUnitYieldingOperation(o1, o2, { -> NoUnit }, { -> NoUnit }, { n1: N, n2: N -> n1 * n2 }, { u1: NoUnit, u2: NoUnit -> u1 * u2 })
    }

    inline fun <U : Unit<U>, N : Number<N>> addition(crossinline o1: () -> N,
                                                     crossinline o2: () -> N,
                                                     crossinline o1Unit: () -> U,
                                                     crossinline o2Unit: () -> U): UnitfulAmount<U, N> {
        return operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 + n2 }, { u1: U, u2: U -> u1 + u2 })
    }

    inline fun <N : Number<N>> noUnitYeildingAddition(crossinline o1: () -> N,
                                                      crossinline o2: () -> N): UnitlessAmount<N> {
        return noUnitYieldingOperation(o1, o2, { -> NoUnit }, { -> NoUnit }, { n1: N, n2: N -> n1 + n2 }, { u1: NoUnit, u2: NoUnit -> u1 + u2 })
    }


    inline fun <U : Unit<U>, N : Number<N>> subtraction(crossinline o1: () -> N,
                                                        crossinline o2: () -> N,
                                                        crossinline o1Unit: () -> U,
                                                        crossinline o2Unit: () -> U): UnitfulAmount<U, N> {
        return operation(o1, o2, o1Unit, o2Unit, { n1: N, n2: N -> n1 + n2 }, { u1: U, u2: U -> u1 + u2 })
    }

    inline fun <N : Number<N>> noUnitYeildingSubtaction(crossinline o1: () -> N,
                                                        crossinline o2: () -> N): UnitlessAmount<N> {
        return noUnitYieldingOperation(o1, o2, { -> NoUnit }, { -> NoUnit }, { n1: N, n2: N -> n1 - n2 }, { u1: NoUnit, u2: NoUnit -> u1 - u2 })
    }

    /**
     *  Represents an operation that takes two operands and yields no unit
     */
    /*
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            N : Number<N>> noUnitYieldingOperation(crossinline operand1: () -> N,
                                                   crossinline operand2: () -> N,
                                                   crossinline operand1Unit: () -> Operand1Unit,
                                                   crossinline operand2Unit: () -> Operand2Unit,
                                                   crossinline numberOperation: (N, N) -> N,
                                                   crossinline unitOperation: (Operand1Unit, Operand2Unit) -> NoUnit): UnitlessAmount<N> {
        return operation(operand1, operand2, operand1Unit, operand2Unit, numberOperation, unitOperation, { n, u -> UnitlessAmount(n, u) })
    }
    inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        ResultUnit : Unit<ResultUnit>,
        N : Number<N>> unitYieldingOperation(crossinline o1: () -> N,
                                             crossinline o2: () -> N,
                                             crossinline o1Unit: () -> O1Unit,
                                             crossinline o2Unit: () -> O2Unit,
                                             crossinline numberOperation: (N, N) -> N,
                                             crossinline unitOperation: (O1Unit, O2Unit) -> ResultUnit): UnitfulAmount<ResultUnit, N> {
    return operation(o1, o2, o1Unit, o2Unit, numberOperation, unitOperation, { u, n -> UnitfulAmount(u, n) })
}
    */
}




/*

inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit, N>,
        Operand2 : Equatable<Operand2Unit, N>,
        Result : Equatable<ResultUnit, N>,
        N : Number<N>> equatableOperation(o1: Operand1,
                                          o2: Operand2,
                                          crossinline numberOperation: (N, N) -> N,
                                          crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                                          crossinline equatableCreator: (() -> N, () -> ResultUnit) -> Result): Result {
    return operation({ -> o1.quantity() }, { -> o2.quantity() }, { -> o1.unit() }, { -> o2.unit() }, numberOperation, unitOperation, equatableCreator)
}

inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit, N>,
        Operand2 : Equatable<Operand2Unit, N>,
        Result : Amount<ResultUnit, N>,
        N : Number<N>> equatableOperation(o1: Operand1,
                                          o2: Operand2,
                                          crossinline numberOperation: (N, N) -> N,
                                          crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                                          crossinline equatableCreator: (() -> N, () -> ResultUnit) -> Result): Result {
    return operation({ -> o1.quantity() }, { -> o2.quantity() }, { -> o1.unit() }, { -> o2.unit() }, numberOperation, unitOperation, equatableCreator)
}

inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit, N>,
        Operand2 : Equatable<Operand2Unit, N>,
        Result : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>,
        N : Number<N>> equatableOperation(o1: Operand1,
                                          o2: Operand2,
                                          crossinline numberOperation: (N, N) -> N,
                                          crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                                          crossinline equatableCreator: (() -> N, () -> N, () -> Operand1Unit, () -> Operand2Unit, () -> N, () -> ResultUnit) -> Result): Result {
    return operation({ -> o1.quantity() }, { -> o2.quantity() }, { -> o1.unit() }, { -> o2.unit() }, numberOperation, unitOperation, equatableCreator)
}
*/



