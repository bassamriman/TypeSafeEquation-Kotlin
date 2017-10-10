package api

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

object Arithmetic {

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
            : ResultAmount {
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
            : ResultAmount {
        return amountCreator(
                { -> operand1() },
                { -> operand2() },
                { -> operand1Unit() },
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
            : ResultAmount {
        return amountCreator(
                { -> operand1() },
                { -> operand2() },
                { -> operandUnit() },
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
    inline fun <Operand1Unit : Unit<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            ResultUnit : Unit<ResultUnit>,
            Operand1 : UnitOnlyEquatable<Operand1Unit>,
            Operand2 : EquatableWithUnit<Operand2Unit, N>,
            Result : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>,
            reified N : Number<N>> equatableOperation(o1: Operand1,
                                                      o2: Operand2,
                                                      operation: (() -> N, () -> N, () -> Operand1Unit, () -> Operand2Unit) -> Result): Result {
        return operation({ -> o1.unaryAmount(N::class).quantity() }, { -> o2.quantity() }, { -> o1.unit() }, { -> o2.unit() })
    }

    /**
     *  Represents an operation that takes two operands
     */
    inline fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : UnitOnlyEquatable<OperandUnit>,
            Operand2 : EquatableWithUnit<OperandUnit, N>,
            Result : AmountOperation<OperandUnit, OperandUnit, OperandUnit, N>,
            reified N : Number<N>> equatableOperation(o1: Operand1,
                                                      o2: Operand2,
                                                      operation: (() -> N, () -> N, () -> OperandUnit) -> Result): Result {
        return operation({ o1.unaryAmount(N::class).quantity() }, { o2.quantity() }, { o1.unit() })
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
        return operation({ o1.quantity() }, { o2.unaryAmount(N::class).quantity() }, { o1.unit() }, { o2.unit() })
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
        return operation({ o1.quantity() }, { o2.quantity() }, { o1.unit() })
    }

    /**
     *  Represents an operation that takes two operands with the same unit and yield same unit
     */
    inline fun <OperandsUnit : UnitLike<OperandsUnit>,
            Operand : Equatable<OperandsUnit, N>,
            Result : AmountOperation<OperandsUnit, OperandsUnit, OperandsUnit, N>,
            N : Number<N>> equatableOperationYieldingSameUnit(o1: Operand,
                                                              o2: Operand,
                                                              operation: (() -> N, () -> N, () -> OperandsUnit) -> Result): Result {
        return operation({ o1.quantity() }, { o2.quantity() }, { o1.unit() })
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
}