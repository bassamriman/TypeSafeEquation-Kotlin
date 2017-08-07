package api

data class Ratio<NumeratorUnit : UnitLike<NumeratorUnit>, DenominatorUnit : UnitLike<DenominatorUnit>, N: Number<N>> (
        val n : ()->N,
        val d : ()->N,
        val nUnit : ()->NumeratorUnit,
        val dUnit : ()->DenominatorUnit)
    : Operation<NumeratorUnit, DenominatorUnit, Div<NumeratorUnit, DenominatorUnit>, N>(
        n,
        d,
        nUnit,
        dUnit,
        {n1 : N, n2 : N -> n1 / n2},
        {u1 : NumeratorUnit, u2 : DenominatorUnit -> u1 / u2}){

    //operator fun <ONU : UnitLike<ONU>, ODU : UnitLike<ODU>> times(other : Ratio<ONU,ODU,N>) :
}

inline fun <N: Number<N>> numericalOperation(o1 : ()->N, o2 : ()->N, numberOperation : (N, N)->N)= numberOperation(o1.invoke(), o2.invoke())
inline fun <O1Unit : UnitLike<O1Unit>,
        O2Unit : UnitLike<O2Unit>,
        ResultUnit : Unit<ResultUnit>>
        unitOperation( o1 : ()->O1Unit, o2 : ()->O2Unit, unitOperation : (O1Unit,O2Unit)-> ResultUnit) = unitOperation(o1.invoke(), o2.invoke())

interface RatioYieldingOperation<NumeratorUnit : Unit<NumeratorUnit>, DenominatorUnit : Unit<DenominatorUnit>, N: Number<N>> : Equatable<Div<NumeratorUnit,DenominatorUnit>,N>{
    operator fun times(other : RatioYieldingOperation<DenominatorUnit, NumeratorUnit,N>) : RatioMultiplicationWithFullCancellation<NumeratorUnit,DenominatorUnit,N> =
            RatioMultiplicationWithFullCancellation(this::quantity,other::quantity,this::unit,other::unit)
    infix fun <ONU : Unit<ONU>> Xdec(other : RatioYieldingOperation<ONU,NumeratorUnit,N>) : RatioMultiplicationWithDecliningCancelation<NumeratorUnit,DenominatorUnit,ONU,N> =
            RatioMultiplicationWithDecliningCancelation(this::quantity,other::quantity,this::unit,other::unit)
    infix fun <ODU : Unit<ODU>> Xinc(other : RatioYieldingOperation<DenominatorUnit,ODU,N>) : RatioMultiplicationWithInclineCancelation<DenominatorUnit,NumeratorUnit,ODU,N> =
            RatioMultiplicationWithInclineCancelation(this::quantity,other::quantity,this::unit,other::unit)
}

data class UnitfulRatio<NumeratorUnit : Unit<NumeratorUnit>, DenominatorUnit : Unit<DenominatorUnit>, N: Number<N>> (
        val n : ()->N,
        val d : ()->N,
        val nUnit : ()->NumeratorUnit,
        val dUnit : ()->DenominatorUnit)
    : Operation<NumeratorUnit, DenominatorUnit, Div<NumeratorUnit, DenominatorUnit>, N>(
        n,
        d,
        nUnit,
        dUnit,
        {n1 : N, n2 : N -> n1 / n2},
        {u1 : NumeratorUnit, u2 : DenominatorUnit -> u1 / u2}), RatioYieldingOperation<NumeratorUnit,DenominatorUnit,N>{
    companion object {
        inline fun <NumeratorUnit : Unit<NumeratorUnit>, DenominatorUnit : Unit<DenominatorUnit>, N: Number<N>> of(
                n : ()->N,
                d : ()->N,
                nUnit : ()->NumeratorUnit,
                dUnit : ()->DenominatorUnit) = nUnit.invoke()
    }
}

data class NoUnitYieldingSameUnitRatio<Unit : UnitLike<Unit>, N: Number<N>> (
        val n : ()->N,
        val d : ()->N,
        val sameUnit : ()-> Unit)
    : NoUnitYieldingOperation<Unit, Unit, N>(
        n,
        d,
        sameUnit,
        sameUnit,
        {n1 : N, n2 : N -> n1 / n2},
        {u1 : Unit, u2 : Unit -> u1 / u2})

data class NoUnitYieldingNoUnitRatio<N: Number<N>> (
        val n : ()->N,
        val d : ()->N)
    : NoUnitYieldingOperation<NoUnit, NoUnit, N>(
        n,
        d,
        {->NoUnit},
        {->NoUnit},
        {n1 : N, n2 : N -> n1 / n2},
        {u1 : NoUnit, u2 : NoUnit -> u1 / u2})

data class Multiplication<Operand1Unit : Unit<Operand1Unit>, Operand2Unit : Unit<Operand2Unit>, N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o1Unit : ()->Operand1Unit,
        val o2Unit : ()->Operand2Unit)
    : Operation<Operand1Unit, Operand2Unit, Times<Operand1Unit, Operand2Unit>, N>(
        o1,
        o2,
        o1Unit,
        o2Unit,
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : Operand1Unit, u2 : Operand2Unit -> u1 * u2})

data class RatioMultiplicationWithFullCancellation<UnitToCancel1 : Unit<UnitToCancel1>, UnitToCancel2 : Unit<UnitToCancel2>, N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o1Unit : ()->Div<UnitToCancel1,UnitToCancel2>,
        val o2Unit : ()->Div<UnitToCancel2,UnitToCancel1>)
    : NoUnitYieldingOperation<Div<UnitToCancel1,UnitToCancel2>, Div<UnitToCancel2,UnitToCancel1>, N>(
        o1,
        o2,
        o1Unit,
        o2Unit,
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : Div<UnitToCancel1,UnitToCancel2>, u2 : Div<UnitToCancel2,UnitToCancel1> -> u1 * u2})

data class RatioMultiplicationWithDecliningCancelation
        <UnitToCancel : Unit<UnitToCancel>,
        O1Denominator : Unit<O1Denominator>,
        O2Numerator : Unit<O2Numerator>,
        N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o1Unit : ()->Div<UnitToCancel,O1Denominator>,
        val o2Unit : ()->Div<O2Numerator,UnitToCancel>)
    : Operation<Div<UnitToCancel,O1Denominator>, Div<O2Numerator,UnitToCancel>, Div<O2Numerator,O1Denominator>, N>(
        o1,
        o2,
        o1Unit,
        o2Unit,
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : Div<UnitToCancel,O1Denominator>, u2 : Div<O2Numerator,UnitToCancel> -> u1 Xdec u2}), RatioYieldingOperation<O2Numerator,O1Denominator,N>

data class RatioMultiplicationWithInclineCancelation
        <UnitToCancel : Unit<UnitToCancel>,
        O1Numerator : Unit<O1Numerator>,
        O2Denominator : Unit<O2Denominator>,
        N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o1Unit : ()->Div<O1Numerator,UnitToCancel>,
        val o2Unit : ()->Div<UnitToCancel,O2Denominator>)
    : Operation<Div<O1Numerator,UnitToCancel>, Div<UnitToCancel,O2Denominator>, Div<O1Numerator,O2Denominator>, N>(
        o1,
        o2,
        o1Unit,
        o2Unit,
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : Div<O1Numerator,UnitToCancel>, u2 : Div<UnitToCancel,O2Denominator> -> u1 Xinc u2}), RatioYieldingOperation<O1Numerator,O2Denominator,N>

data class RatioMultiplication
        <O1Numerator : Unit<O1Numerator>,
        O1Denominator : Unit<O1Denominator>,
        O2Numerator : Unit<O2Numerator>,
        O2Denominator : Unit<O2Denominator>,
        N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o1Unit : ()->Div<O1Numerator,O1Denominator>,
        val o2Unit : ()->Div<O2Numerator,O2Denominator>)
    : Operation<Div<O1Numerator,O1Denominator>, Div<O2Numerator,O2Denominator>, Div<Times<O1Numerator,O2Numerator>,Times<O1Denominator,O2Denominator>>, N>(
        o1,
        o2,
        o1Unit,
        o2Unit,
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : Div<O1Numerator,O1Denominator>, u2 : Div<O2Numerator,O2Denominator> -> u1 * u2}), RatioYieldingOperation<Times<O1Numerator,O2Numerator>,Times<O1Denominator,O2Denominator>,N>

data class MultiplicationO2WithNoUnit<Operand1Unit : Unit<Operand1Unit>, N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o1Unit : ()->Operand1Unit)
    : Operation<Operand1Unit, NoUnit, Operand1Unit, N>(
        o1,
        o2,
        o1Unit,
        {->NoUnit},
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : Operand1Unit, u2 : NoUnit -> u1 * u2})

data class MultiplicationO1WithNoUnit<Operand2Unit : Unit<Operand2Unit>, N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val o2Unit : ()->Operand2Unit)
    : Operation<NoUnit, Operand2Unit, Operand2Unit, N>(
        o1,
        o2,
        {->NoUnit},
        o2Unit,
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : NoUnit, u2 : Operand2Unit -> u1 * u2})

data class NoUnitYieldingMultiplication<N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N)
    : NoUnitYieldingOperation<NoUnit, NoUnit, N>(
        o1,
        o2,
        {->NoUnit},
        {->NoUnit},
        {n1 : N, n2 : N -> n1 * n2},
        {u1 : NoUnit, u2 : NoUnit -> u1 * u2})

data class Addition<U : Unit<U>, N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val unit : ()->U)
    : Operation<U, U, U, N>(
        o1,
        o2,
        unit,
        unit,
        {n1 : N, n2 : N -> n1 + n2},
        {u1 : U, u2 : U -> u1 + u2})

data class NoUnitYieldingAddition<N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N)
    : NoUnitYieldingOperation<NoUnit, NoUnit, N>(
        o1,
        o2,
        {->NoUnit},
        {->NoUnit},
        {n1 : N, n2 : N -> n1 + n2},
        {u1 : NoUnit, u2 : NoUnit -> u1 + u2})

data class NoUnitYieldingSubtraction<N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N)
    : NoUnitYieldingOperation<NoUnit, NoUnit, N>(
        o1,
        o2,
        {->NoUnit},
        {->NoUnit},
        {n1 : N, n2 : N -> n1 - n2},
        {u1 : NoUnit, u2 : NoUnit -> u1 - u2})

data class Subtraction<U : Unit<U>, N: Number<N>> (
        val o1 : ()->N,
        val o2 : ()->N,
        val unit : ()->U)
    : Operation<U, U, U, N>(
        o1,
        o2,
        unit,
        unit,
        {n1 : N, n2 : N -> n1 - n2},
        {u1 : U, u2 : U -> u1 - u2})

