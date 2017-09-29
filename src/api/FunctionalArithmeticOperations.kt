package api

import memoization.memoize
import kotlin.reflect.KClass

/*
inline fun numericalOperation(o1 : ()->Number<*>, o2 : ()->Number<*>, numberOperation : (Number<*>, Number<*>)->Number<*>) =
        numberOperation.invoke(o1.invoke(),o2.invoke())
val numericalOperationMem = ::numericalOperation.memoize(100)

inline fun unitOperation( o1 : ()->UnitLike<*>, o2 : ()->UnitLike<*>, uOperation : (UnitLike<*>,UnitLike<*>)-> UnitLike<*>) =
        uOperation(o1.invoke(), o2.invoke())
val unitOperationMem = ::unitOperation.memoize(100)
 */

open class Amount<U : UnitLike<U>, N: Number<N>>(open val quantity : () -> N, open val unit: () -> U) : Equatable<U,N>{
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

open class UnitfulAmount<U : Unit<U>, N: Number<N>>(override val quantity : () -> N, override val unit: () -> U): Amount<U,N>(quantity,unit), EquatableWithUnit<U,N>{
    companion object {
        fun <U : Unit<U>, N: Number<N>> of(quantity : N, unit: U): UnitfulAmount<U, N> = UnitfulAmount({->quantity},{->unit})
    }
}

open class UnitlessAmount<N: Number<N>>(override val quantity : () -> N, override val unit: () -> NoUnit): Amount<NoUnit,N>(quantity,unit), EquatableWithNoUnit<N>{
    override fun unit(): NoUnit = NoUnit
    companion object {
        fun <N: Number<N>> of(quantity : N, unit : NoUnit): UnitlessAmount<N> = UnitlessAmount({->quantity}, {->unit})
    }
}

interface Operation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N: Number<N>> {
    val operand1 : ()->N
    val operand2 : ()->N
    val operand1Unit : ()->Operand1Unit
    val operand2Unit : ()->Operand2Unit
}

open class AmountOperation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        N: Number<N>>(
        override val quantity : () -> N,
        override val unit: () -> ResultUnit,
        override val operand1 : ()->N,
        override val operand2 : ()->N,
        override val operand1Unit : ()->Operand1Unit,
        override val operand2Unit : ()->Operand2Unit): Amount<ResultUnit,N>(quantity,unit), Operation<Operand1Unit,Operand2Unit,N>

open class UnitlessAmountOperation<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N: Number<N>>(
        override val quantity : () -> N,
        override val unit: () -> NoUnit,
        override val operand1 : ()->N,
        override val operand2 : ()->N,
        override val operand1Unit : ()->Operand1Unit,
        override val operand2Unit : ()->Operand2Unit): UnitlessAmount<N>(quantity,unit), Operation<Operand1Unit,Operand2Unit,N>


open class Division<NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        ResultUnit : UnitLike<ResultUnit>,
        N: Number<N>> (
        override val quantity : () -> N,
        override val unit: () -> ResultUnit,
        val n : ()->N,
        val d : ()->N,
        val nUnit : ()->NumeratorUnit,
        val dUnit : ()->DenominatorUnit) :
        AmountOperation<NumeratorUnit, DenominatorUnit, ResultUnit, N>(quantity,unit,n,d,nUnit,dUnit)

open class UnitlessDivision<NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        N: Number<N>> (
        override val quantity : () -> N,
        override val unit: () -> NoUnit,
        val n : ()->N,
        val d : ()->N,
        val nUnit : ()->NumeratorUnit,
        val dUnit : ()->DenominatorUnit) :
        UnitlessAmountOperation<NumeratorUnit, DenominatorUnit, N>(quantity,unit,n,d,nUnit,dUnit)

open class Multiplication<Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        N: Number<N>> (
        override val quantity : () -> N,
        override val unit: () -> ResultUnit,
        override val operand1 : ()->N,
        override val operand2 : ()->N,
        override val operand1Unit : ()->Operand1Unit,
        override val operand2Unit : ()->Operand2Unit) :
        AmountOperation<Operand1Unit, Operand2Unit, ResultUnit, N>(quantity,unit,operand1,operand2,operand1Unit,operand2Unit)

open class UnitfulAmountYieldingOperation<Operand1Unit : UnitLike<Operand1Unit>,
    Operand2Unit : UnitLike<Operand2Unit>,
    ResultUnit : Unit<ResultUnit>,
    N: Number<N>>(
    override val quantity : () -> N,
    override val unit: () -> ResultUnit,
    override val operand1 : ()->N,
    override val operand2 : ()->N,
    override val operand1Unit : ()->Operand1Unit,
    override val operand2Unit : ()->Operand2Unit): UnitfulAmount<ResultUnit,N>(quantity,unit), Operation<Operand1Unit,Operand2Unit,N>

open class UnitlessAmountYeildingOperation<
        Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        N: Number<N>>(
        override val quantity : () -> N,
        override val unit: () -> NoUnit,
        override val operand1 : ()->N,
        override val operand2 : ()->N,
        override val operand1Unit : ()->Operand1Unit,
        override val operand2Unit : ()->Operand2Unit): UnitlessAmount<N>(quantity,unit), Operation<Operand1Unit,Operand2Unit,N>{
    override fun unit(): NoUnit = NoUnit
}

inline fun <N: Number<N>> numericalOperation(o1 : ()->N, o2 : ()->N, numberOperation : (N, N)->N) =
        numberOperation.invoke(o1.invoke(),o2.invoke())

inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        ResultUnit : UnitLike<ResultUnit>>
        unitOperation( o1 : ()->O1Unit, o2 : ()->O2Unit, uOperation : (O1Unit,O2Unit)-> ResultUnit) =
        uOperation(o1.invoke(), o2.invoke())

inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        ResultAmount : Equatable<ResultUnit,N>,
        N: Number<N>> operation( crossinline o1 : ()->N,
                                 crossinline o2 : ()->N,
                                 crossinline o1Unit : ()->O1Unit,
                                 crossinline o2Unit : ()->O2Unit,
                                 crossinline numberOperation : (N, N)->N,
                                 crossinline uOperation : (O1Unit,O2Unit)-> ResultUnit,
                                 amountCreator : (()->N, ()->ResultUnit) -> ResultAmount) : ResultAmount{
    return amountCreator({->numericalOperation(o1,o2,numberOperation)},{->unitOperation(o1Unit, o2Unit, uOperation )})
}

inline fun <OperandUnit : UnitLike<OperandUnit>,
        ResultAmount : Equatable<NoUnit,N>,
        N: Number<N>> noUnitYieldingSameUnitOperation(crossinline o1 : ()->N,
                                                      crossinline o2 : ()->N,
                                                      crossinline operandUnit : ()->OperandUnit,
                                                      crossinline numberOperation : (N, N)->N,
                                                      crossinline uOperation : (OperandUnit,OperandUnit)-> NoUnit,
                                                      amountCreator : (()->N,()->N, ()->OperandUnit, ()->OperandUnit, ()-> N, ()->NoUnit) -> ResultAmount) : ResultAmount{
    return amountCreator({->o1.invoke()}, {->o2.invoke()}, {->operandUnit.invoke()}, {->operandUnit.invoke()}, {->numericalOperation(o1,o2,numberOperation)},{->unitOperation(operandUnit, operandUnit, uOperation )})
}

inline fun <OperandUnit : UnitLike<OperandUnit>,
        ResultAmount : Equatable<OperandUnit,N>,
        N: Number<N>> sameUnitYeildingSameUnitOperation( crossinline o1 : ()->N,
                                                       crossinline o2 : ()->N,
                                                       crossinline o1Unit : ()->OperandUnit,
                                                       crossinline o2Unit : ()->OperandUnit,
                                                       crossinline numberOperation : (N, N)->N,
                                                       crossinline uOperation : (OperandUnit,OperandUnit)-> OperandUnit,
                                                         amountCreator : (()->N, ()->OperandUnit) -> ResultAmount) : ResultAmount{
    return amountCreator({->numericalOperation(o1,o2,numberOperation)}, {->unitOperation(o1Unit, o2Unit, uOperation )})
}

inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        ResultAmount : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit,N>,
        N: Number<N>> operation( crossinline o1 : ()->N,
                                 crossinline o2 : ()->N,
                                 crossinline o1Unit : ()->O1Unit,
                                 crossinline o2Unit : ()->O2Unit,
                                 crossinline numberOperation : (N, N)->N,
                                 crossinline uOperation : (O1Unit,O2Unit)-> ResultUnit,
                                 amountCreator : (()->N,()->N, ()->O1Unit, ()->O2Unit, ()-> N, ()->ResultUnit) -> ResultAmount) : ResultAmount{
    return amountCreator({->o1.invoke()}, {->o2.invoke()}, {->o1Unit.invoke()}, {->o2Unit.invoke()}, {->numericalOperation(o1,o2,numberOperation)},{->unitOperation(o1Unit, o2Unit, uOperation )})
}

inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        N: Number<N>> noUnitYieldingOperation(crossinline o1 : ()->N,
                                              crossinline o2 : ()->N,
                                              crossinline o1Unit : ()->O1Unit,
                                              crossinline o2Unit : ()->O2Unit,
                                              crossinline numberOperation : (N, N)->N,
                                              crossinline unitOperation : (O1Unit,O2Unit)-> NoUnit) : UnitlessAmount<N>{
    return operation(o1,o2,o1Unit,o2Unit,numberOperation, unitOperation, {n, u -> UnitlessAmount(n,u)})
}


inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        ResultUnit : Unit<ResultUnit>,
        N: Number<N>> unitYieldingOperation(crossinline o1 : ()->N,
                                            crossinline o2 : ()->N,
                                            crossinline o1Unit : ()->O1Unit,
                                            crossinline o2Unit : ()->O2Unit,
                                            crossinline numberOperation : (N, N)->N,
                                            crossinline unitOperation : (O1Unit,O2Unit)-> ResultUnit) : UnitfulAmount<ResultUnit,N>{
    return operation(o1,o2,o1Unit,o2Unit,numberOperation, unitOperation, {u, n -> UnitfulAmount(u,n)})
}


inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        Result : Equatable<ResultUnit,N>,
        N: Number<N>> equatableOperation(o1 : Operand1,
                                         o2 : Operand2,
                                         crossinline numberOperation: (N, N) -> N,
                                         crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                                         crossinline equatableCreator : (()->N, ()->ResultUnit) -> Result): Result{
    return operation({->o1.quantity()}, {->o2.quantity()}, {->o1.unit()}, {->o2.unit()}, numberOperation, unitOperation, equatableCreator)
}

inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        Result : Amount<ResultUnit,N>,
        N: Number<N>> equatableOperation(o1 : Operand1,
                                         o2 : Operand2,
                                         crossinline numberOperation: (N, N) -> N,
                                         crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                                         crossinline equatableCreator : (()->N, ()->ResultUnit) -> Result): Result{
    return operation({->o1.quantity()}, {->o2.quantity()}, {->o1.unit()}, {->o2.unit()}, numberOperation, unitOperation, equatableCreator)
}

inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        Result : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit,N>,
        N: Number<N>> equatableOperation(o1 : Operand1,
                                         o2 : Operand2,
                                         crossinline numberOperation: (N, N) -> N,
                                         crossinline unitOperation: (Operand1Unit, Operand2Unit) -> ResultUnit,
                                         crossinline equatableCreator : (()->N,()->N, ()->Operand1Unit, ()->Operand2Unit, ()-> N, ()->ResultUnit) -> Result): Result{
    return operation({->o1.quantity()}, {->o2.quantity()}, {->o1.unit()}, {->o2.unit()}, numberOperation, unitOperation, equatableCreator)
}


inline fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        ResultUnit : UnitLike<ResultUnit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        Result : AmountOperation<Operand1Unit, Operand2Unit, ResultUnit,N>,
        N: Number<N>> equatableOperation(o1 : Operand1,
                                         o2 : Operand2,
                                         operation : (()->N,()->N, ()->Operand1Unit, ()->Operand2Unit) -> Result): Result{
    return operation({->o1.quantity()}, {->o2.quantity()}, {->o1.unit()}, {->o2.unit()})
}

inline fun <OperandsUnit : UnitLike<OperandsUnit>,
        Operand1 : Equatable<OperandsUnit,N>,
        Operand2 : Equatable<OperandsUnit,N>,
        Result : UnitlessAmountOperation<OperandsUnit, OperandsUnit,N>,
        N: Number<N>> equatableOperation(o1 : Operand1,
                                          o2 : Operand2,
                                          operation : (()->N,()->N, ()->OperandsUnit) -> Result): Result{
    return operation({->o1.quantity()}, {->o2.quantity()}, {->o1.unit()})
}

fun <Operand1Unit : UnitLike<Operand1Unit>,
        Operand2Unit : UnitLike<Operand2Unit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        N: Number<N>> divisionOperation(o1 : Operand1,
                                        o2 : Operand2): Division<Operand1Unit, Operand2Unit, Div<Operand1Unit,Operand2Unit>, N>{
    return equatableOperation(o1,o2, {a :()->N,b:()->N, c:()->Operand1Unit, d:()->Operand2Unit -> division(a,b,c,d)})
}

fun <   Operand2Unit : UnitLike<Operand2Unit>,
        Operand1 : Equatable<NoUnit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        N: Number<N>> noUnitDivisionByUnitOperation(o1 : Operand1,
                                        o2 : Operand2): Division<NoUnit, Operand2Unit, Div<NoUnit,Operand2Unit>, N>{
    return divisionOperation(o1,o2)
}

inline fun <Operand2Unit : Unit<Operand2Unit>,
    Operand1 : Equatable<NoUnit,N>,
    Operand2 : UnitOnlyEquatable<Operand2Unit>,
    reified N: Number<N>> noUnitDivisionByUnitOperation(o1 : Operand1,
                                                o2 : Operand2): Division<NoUnit, Operand2Unit, Div<NoUnit,Operand2Unit>, N>{
    return divisionOperation(o1,o2.unaryAmount(N::class))
}

fun <Operand1Unit : Unit<Operand1Unit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<NoUnit,N>,
        N: Number<N>> unitfulDivisionByNoUnitOperation(o1 : Operand1,
                                               o2 : Operand2): AmountOperation<Operand1Unit, NoUnit, Operand1Unit, N>{
    return equatableOperation(o1,o2,{a :()->N,b:()->N, c:()->Operand1Unit, d:()->NoUnit -> division(a,b,c,d)})
}

fun <Operand1Unit : Unit<Operand1Unit>,
        Operand2Unit : Unit<Operand2Unit>,
        Operand1 : Equatable<Operand1Unit,N>,
        Operand2 : Equatable<Operand2Unit,N>,
        N: Number<N>> unitfulDivisionOperation(o1 : Operand1,
                                               o2 : Operand2): Division<Operand1Unit, Operand2Unit, Div<Operand1Unit,Operand2Unit>, N>{
    return divisionOperation(o1,o2)
}

fun <OperandsUnit : Unit<OperandsUnit>,
        Operand1 : Equatable<OperandsUnit,N>,
        Operand2 : Equatable<OperandsUnit,N>,
        N: Number<N>> sameUnitDivisionOperation(o1 : Operand1,
                                               o2 : Operand2): UnitlessDivision<OperandsUnit,OperandsUnit,N>{
    return equatableOperation(o1,o2, {a :()->N,b:()->N, c:()->OperandsUnit -> division(a,b,c)})
}

fun <Operand1 : Equatable<NoUnit,N>,
        Operand2 : Equatable<NoUnit,N>,
        N: Number<N>> noUnitDivisionOperation(o1 : Operand1,
                                                o2 : Operand2): UnitlessDivision<NoUnit,NoUnit,N>{
    return equatableOperation(o1,o2, {a :()->N,b:()->N, c:()->NoUnit -> division(a,b,c)})
}

inline fun <NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        N: Number<N>> division( crossinline o1 : ()->N,
                                crossinline o2 : ()->N,
                                crossinline o1Unit : ()->NumeratorUnit,
                                crossinline o2Unit : ()->DenominatorUnit) : Division<NumeratorUnit, DenominatorUnit, Div<NumeratorUnit,DenominatorUnit>, N> {
    return operation(o1,o2, o1Unit, o2Unit,
            {n1 : N, n2 : N -> n1 / n2},
            {u1 : NumeratorUnit, u2 : DenominatorUnit -> u1 / u2},
            {o1n : ()->N, o2n : ()->N, o1u : ()->NumeratorUnit, o2u : ()->DenominatorUnit, resultn, resultu -> Division(resultn, resultu, o1n, o2n, o1u, o2u)})
}

inline fun <NumeratorUnit : Unit<NumeratorUnit>,
        N: Number<N>> division( crossinline o1 : ()->N,
                                crossinline o2 : ()->N,
                                crossinline o1Unit : ()->NumeratorUnit,
                                crossinline o2Unit : ()->NoUnit) : AmountOperation<NumeratorUnit, NoUnit, NumeratorUnit, N> {
    return operation(o1,o2, o1Unit, o2Unit,
            {n1 : N, n2 : N -> n1 / n2},
            {u1 : NumeratorUnit, u2 : NoUnit -> u1 / u2},
            {o1n : ()->N, o2n : ()->N, o1u : ()->NumeratorUnit, o2u : ()->NoUnit, resultn, resultu -> AmountOperation(resultn, resultu, o1n, o2n, o1u, o2u)})
}

inline fun <OperandUnit : UnitLike<OperandUnit>,
        N: Number<N>> division( crossinline o1 : ()->N,
                                crossinline o2 : ()->N,
                                crossinline operandUnit : ()->OperandUnit) : UnitlessDivision<OperandUnit,OperandUnit,N>{
    return noUnitYieldingSameUnitOperation(o1,o2, operandUnit,
            {n1 : N, n2 : N -> n1 / n2},
            {u1 : OperandUnit, u2 : OperandUnit -> u1 / u2},
            {o1n : ()->N, o2n : ()->N, o1u : ()->OperandUnit, o2u : ()->OperandUnit, resultn, resultu -> UnitlessDivision(resultn, resultu, o1n, o2n, o1u, o2u)})
}

inline fun <SameUnit : Unit<SameUnit>,
        N: Number<N>> sameUnitNoUnitYeildingDivision( crossinline o1 : ()->N,
                                                      crossinline o2 : ()->N,
                                                      crossinline o1Unit : ()->SameUnit,
                                                      crossinline o2Unit : ()->SameUnit) : UnitlessAmount<N> {
     return noUnitYieldingOperation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 / n2}, {u1 : SameUnit, u2 : SameUnit -> u1 / u2}.memoize(100))
}

inline fun <N: Number<N>> noUnitOperandsNoUnitYeildingDivision( crossinline o1 : ()->N,
                                                                crossinline o2 : ()->N) : UnitlessAmount<N> {
    return noUnitYieldingOperation(o1,o2, {->NoUnit}, {->NoUnit}, {n1 : N, n2 : N -> n1 / n2}, {u1 : NoUnit, u2 : NoUnit -> u1 / u2})
}

inline fun <O1Unit : Unit<O1Unit>,
        O2Unit : Unit<O2Unit>,
        N: Number<N>> multiplication( crossinline o1 : ()->N,
                                      crossinline o2 : ()->N,
                                      crossinline o1Unit : ()->O1Unit,
                                      crossinline o2Unit : ()->O2Unit) : UnitfulAmount<Times<O1Unit, O2Unit>, N> {
    return operation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 * n2}, {u1 : O1Unit, u2 : O2Unit -> u1 * u2})
}

inline fun <O1Numerator : Unit<O1Numerator>,
        O1Denominator : Unit<O1Denominator>,
        O2Numerator : Unit<O2Numerator>,
        O2Denominator : Unit<O2Denominator>,
        N: Number<N>> ratioMultiplication( crossinline o1 : ()->N,
                                           crossinline o2 : ()->N,
                                           crossinline o1Unit : ()->Div<O1Numerator,O1Denominator>,
                                           crossinline o2Unit : ()->Div<O2Numerator,O2Denominator>): UnitfulAmount<Div<Times<O1Numerator,O2Numerator>,Times<O1Denominator,O2Denominator>>, N> {
    return operation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 * n2}, {u1 : Div<O1Numerator,O1Denominator>, u2 : Div<O2Numerator,O2Denominator> -> u1 * u2})
}

inline fun <UnitToCancel : Unit<UnitToCancel>,
        O1Denominator : Unit<O1Denominator>,
        O2Numerator : Unit<O2Numerator>,
        N: Number<N>> ratioMultiplicationWithInclineCancelation( crossinline o1 : ()->N,
                                                                 crossinline o2 : ()->N,
                                                                 crossinline o1Unit : ()->Div<UnitToCancel,O1Denominator>,
                                                                 crossinline o2Unit : ()->Div<O2Numerator,UnitToCancel>) :UnitfulAmount<Div<O2Numerator,O1Denominator>,N> {
    return operation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 * n2}, {u1 : Div<UnitToCancel,O1Denominator>, u2 : Div<O2Numerator,UnitToCancel> -> u1 Xdec u2})
}

inline fun <UnitToCancel : Unit<UnitToCancel>,
        O1Numerator : Unit<O1Numerator>,
        O2Denominator : Unit<O2Denominator>,
        N: Number<N>> ratioMultiplicationWithDeclineCancelation( crossinline o1 : ()->N,
                                                                 crossinline o2 : ()->N,
                                                                 crossinline o1Unit : ()->Div<O1Numerator,UnitToCancel>,
                                                                 crossinline o2Unit : ()->Div<UnitToCancel,O2Denominator>): UnitfulAmount<Div<O1Numerator,O2Denominator>,N> {
    return operation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 * n2}, {u1 : Div<O1Numerator,UnitToCancel>, u2 : Div<UnitToCancel,O2Denominator> -> u1 Xinc u2})
}


inline fun <UnitToCancel1 : Unit<UnitToCancel1>,
        UnitToCancel2 : Unit<UnitToCancel2>,
        N: Number<N>> ratioMultiplicationWithFullCancellation(
        crossinline o1 : ()->N,
        crossinline o2 : ()->N,
        crossinline o1Unit : ()->Div<UnitToCancel1,UnitToCancel2>,
        crossinline o2Unit : ()->Div<UnitToCancel2,UnitToCancel1>) : UnitlessAmount<N>{
    return noUnitYieldingOperation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 * n2}, {u1 : Div<UnitToCancel1,UnitToCancel2>, u2 : Div<UnitToCancel2,UnitToCancel1> -> u1 * u2})
}

inline fun <O1Unit : Unit<O1Unit>,
        N: Number<N>> noUnitSecondOperandMultiplication( crossinline o1 : ()->N,
                                                         crossinline o2 : ()->N,
                                                         crossinline o1Unit : ()->O1Unit) : UnitfulAmount<O1Unit, N> {
    return operation(o1,o2, o1Unit, {->NoUnit}, {n1 : N, n2 : N -> n1 * n2}, {u1 : O1Unit, u2 : NoUnit -> u1 * u2})
}



inline fun <O2Unit : Unit<O2Unit>,
        N: Number<N>> noUnitFirstOperandMultiplication( crossinline o1 : ()->N,
                                                        crossinline o2 : ()->N,
                                                        crossinline o2Unit : ()->O2Unit) : UnitfulAmount<O2Unit, N> {
    return operation(o1,o2, {->NoUnit}, o2Unit, {n1 : N, n2 : N -> n1 * n2}, {u1 :NoUnit, u2: O2Unit -> u1 * u2})
}

inline fun <N: Number<N>> noUnitYeildingMultiplication( crossinline o1 : ()->N,
                                                        crossinline o2 : ()->N) : UnitlessAmount<N> {
    return noUnitYieldingOperation(o1,o2, {->NoUnit}, {->NoUnit}, {n1 : N, n2 : N -> n1 * n2}, {u1 : NoUnit, u2: NoUnit -> u1 * u2})
}

inline fun <U : Unit<U>, N: Number<N>> addition( crossinline o1 : ()->N,
                                                 crossinline o2 : ()->N,
                                                 crossinline o1Unit : ()->U,
                                                 crossinline o2Unit : ()->U) : UnitfulAmount<U, N> {
    return operation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 + n2}, {u1 : U, u2 : U -> u1 + u2})
}

inline fun <N: Number<N>> noUnitYeildingAddition( crossinline o1 : ()->N,
                                                  crossinline o2 : ()->N) : UnitlessAmount<N> {
    return noUnitYieldingOperation(o1,o2, {->NoUnit}, {->NoUnit}, {n1 : N, n2 : N -> n1 + n2}, {u1 : NoUnit, u2: NoUnit -> u1 + u2})
}


inline fun <U : Unit<U>, N: Number<N>> subtraction( crossinline o1 : ()->N,
                                                    crossinline o2 : ()->N,
                                                    crossinline o1Unit : ()->U,
                                                    crossinline o2Unit : ()->U) : UnitfulAmount<U, N> {
    return operation(o1,o2, o1Unit, o2Unit, {n1 : N, n2 : N -> n1 + n2}, {u1 : U, u2 : U -> u1 + u2})
}

inline fun <N: Number<N>> noUnitYeildingSubtaction( crossinline o1 : ()->N,
                                                    crossinline o2 : ()->N) : UnitlessAmount<N> {
    return noUnitYieldingOperation(o1,o2, {->NoUnit}, {->NoUnit}, {n1 : N, n2 : N -> n1 - n2}, {u1 : NoUnit, u2: NoUnit -> u1 - u2})
}

