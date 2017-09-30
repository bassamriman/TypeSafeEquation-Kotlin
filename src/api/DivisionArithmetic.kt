package api

open class Division<NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        ResultUnit : UnitLike<ResultUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> ResultUnit,
        val numerator: () -> N,
        val denominator: () -> N,
        val numeratorUnit: () -> NumeratorUnit,
        val denominatorUnit: () -> DenominatorUnit) :
        AmountOperation<NumeratorUnit, DenominatorUnit, ResultUnit, N>(quantity, unit, numerator, denominator, numeratorUnit, denominatorUnit)

open class UnitlessDivision<NumeratorUnit : UnitLike<NumeratorUnit>,
        DenominatorUnit : UnitLike<DenominatorUnit>,
        N : Number<N>>(
        override val quantity: () -> N,
        override val unit: () -> NoUnit,
        val n: () -> N,
        val d: () -> N,
        val nUnit: () -> NumeratorUnit,
        val dUnit: () -> DenominatorUnit) :
        UnitlessAmountOperation<NumeratorUnit, DenominatorUnit, N>(quantity, unit, n, d, nUnit, dUnit)

object RawDivisionArithmetic{
    /**
     *  Raw operation
     */
    inline fun <NumeratorUnit : UnitLike<NumeratorUnit>,
            DenominatorUnit : UnitLike<DenominatorUnit>,
            N : Number<N>> division(crossinline o1: () -> N,
                                    crossinline o2: () -> N,
                                    crossinline o1Unit: () -> NumeratorUnit,
                                    crossinline o2Unit: () -> DenominatorUnit): Division<NumeratorUnit, DenominatorUnit, Div<NumeratorUnit, DenominatorUnit>, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 / n2 },
                { u1: NumeratorUnit, u2: DenominatorUnit -> u1 / u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> NumeratorUnit, o2u: () -> DenominatorUnit, resultn, resultu -> Division(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <NumeratorUnit : Unit<NumeratorUnit>,
            N : Number<N>> amountYeildingDivision(crossinline o1: () -> N,
                                                  crossinline o2: () -> N,
                                                  crossinline o1Unit: () -> NumeratorUnit,
                                                  crossinline o2Unit: () -> NoUnit): AmountOperation<NumeratorUnit, NoUnit, NumeratorUnit, N> {
        return Arithmetic.operation(o1, o2, o1Unit, o2Unit,
                { n1: N, n2: N -> n1 / n2 },
                { u1: NumeratorUnit, u2: NoUnit -> u1 / u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> NumeratorUnit, o2u: () -> NoUnit, resultn, resultu -> Division(resultn, resultu, o1n, o2n, o1u, o2u) })
    }

    inline fun <OperandUnit : UnitLike<OperandUnit>,
            N : Number<N>> noUnitYieldingDivision(crossinline o1: () -> N,
                                                  crossinline o2: () -> N,
                                                  crossinline operandUnit: () -> OperandUnit): UnitlessDivision<OperandUnit, OperandUnit, N> {
        return Arithmetic.noUnitYieldingSameUnitOperation(
                o1,
                o2,
                operandUnit,
                { n1: N, n2: N -> n1 / n2 },
                { u1: OperandUnit, u2: OperandUnit -> u1 / u2 },
                { o1n: () -> N, o2n: () -> N, o1u: () -> OperandUnit, o2u: () -> OperandUnit, resultn, resultu -> UnitlessDivision(resultn, resultu, o1n, o2n, o1u, o2u) })
    }
}

object BaseEquatableDivision {
    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : UnitLike<Operand2Unit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : Equatable<Operand2Unit, N>,
            N : Number<N>> divisionOperation(operand1: Operand1,
                                             operand2: Operand2): Division<Operand1Unit, Operand2Unit, Div<Operand1Unit, Operand2Unit>, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawDivisionArithmetic.division(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : EquatableWithUnit<OperandUnit, N>,
            Operand2 : EquatableWithNoUnit<N>,
            N : Number<N>> divisionOperation(operand1: Operand1,
                                             operand2: Operand2): AmountOperation<OperandUnit, NoUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> OperandUnit, o2Unit: () -> NoUnit -> RawDivisionArithmetic.amountYeildingDivision(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    inline fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : UnitOnlyEquatable<OperandUnit>,
            Operand2 : EquatableWithNoUnit<N>,
            reified N : Number<N>> divisionOperation(operand1: Operand1,
                                                     operand2: Operand2): AmountOperation<OperandUnit, NoUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> OperandUnit, o2Unit: () -> NoUnit -> RawDivisionArithmetic.amountYeildingDivision(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : UnitOnlyEquatable<Operand2Unit>,
            reified N : Number<N>> divisionOperation(operand1: Operand1,
                                                     operand2: Operand2): Division<Operand1Unit, Operand2Unit, Div<Operand1Unit, Operand2Unit>, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> Operand1Unit, o2Unit: () -> Operand2Unit -> RawDivisionArithmetic.division(o1, o2, o1Unit, o2Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    fun <OperandUnit : UnitLike<OperandUnit>,
            Operand : Equatable<OperandUnit, N>,
            N : Number<N>> unitlessDivisionOperation(operand1: Operand,
                                                     operand2: Operand): UnitlessDivision<OperandUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> OperandUnit -> RawDivisionArithmetic.noUnitYieldingDivision(o1, o2, o1Unit) }
        )
    }

    /**
     *  Equatable Operation: Represents a division that takes two operands and yields a division
     */
    inline fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : EquatableWithUnit<OperandUnit, N>,
            Operand2 : UnitOnlyEquatable<OperandUnit>,
            reified N : Number<N>> unitlessDivisionOperation(operand1: Operand1,
                                                             operand2: Operand2): UnitlessDivision<OperandUnit, OperandUnit, N> {
        return Arithmetic.equatableOperation(
                operand1,
                operand2,
                { o1: () -> N, o2: () -> N, o1Unit: () -> OperandUnit -> RawDivisionArithmetic.noUnitYieldingDivision(o1, o2, o1Unit) }
        )
    }
}


object EquatableDivision {

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

    /**
     *  Equatable Operation
     *
     *  unit1/unit2 = unit1/unit2
     *
     *  @param o1 numerator with unit
     *  @param o2 denominator with unit
     *  @return division with numerator unit/ denominator unit as unit
     */
    inline fun <Operand1Unit : UnitLike<Operand1Unit>,
            Operand2Unit : Unit<Operand2Unit>,
            Operand1 : Equatable<Operand1Unit, N>,
            Operand2 : UnitOnlyEquatable<Operand2Unit>,
            reified N : Number<N>> unitDivisionByUnitOperation(o1: Operand1,
                                                               o2: Operand2): Division<Operand1Unit, Operand2Unit, Div<Operand1Unit, Operand2Unit>, N> {
        return BaseEquatableDivision.divisionOperation(o1, o2)
    }

    /**
     *  Equatable Operation
     *
     *  unit/unit = NoUnit
     *
     *  @param o1 numerator with unit
     *  @param o2 denominator with same unit as numerator
     *  @return division NoUnit as unit
     */
    fun <OperandsUnit : UnitLike<OperandsUnit>,
            Operand1 : Equatable<OperandsUnit, N>,
            Operand2 : Equatable<OperandsUnit, N>,
            N : Number<N>> sameUnitDivisionOperation(o1: Operand1,
                                                     o2: Operand2): UnitlessDivision<OperandsUnit, OperandsUnit, N> {
        return BaseEquatableDivision.unitlessDivisionOperation(o1, o2)
    }

    /**
     *  Equatable Operation
     *
     *  unit/unit = NoUnit
     *
     *  @param o1 numerator with unit
     *  @param o2 denominator with same unit as numerator
     *  @return division NoUnit as unit
     */
    inline fun <OperandsUnit : Unit<OperandsUnit>,
            Operand1 : EquatableWithUnit<OperandsUnit, N>,
            Operand2 : UnitOnlyEquatable<OperandsUnit>,
            reified N : Number<N>> sameUnitDivisionOperation(o1: Operand1,
                                                             o2: Operand2): UnitlessDivision<OperandsUnit, OperandsUnit, N> {
        return BaseEquatableDivision.unitlessDivisionOperation(o1, o2)
    }


    /**
     *  Equatable Operation
     *
     *  unit/NoUnit= unit
     *
     *  @param o1 numerator with unit
     *  @param o2 denominator with NoUnit unit
     *  @return division with numerator as unit
     */
    fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : EquatableWithUnit<OperandUnit, N>,
            Operand2 : EquatableWithNoUnit<N>,
            N : Number<N>> unitDivisionByNoUnitOperation(o1: Operand1,
                                                         o2: Operand2): AmountOperation<OperandUnit, NoUnit, OperandUnit, N> {
        return BaseEquatableDivision.divisionOperation(o1, o2)
    }

    /**
     *  Equatable Operation
     *
     *  unit/NoUnit= unit
     *
     *  @param o1 numerator with unit
     *  @param o2 denominator with NoUnit unit
     *  @return division with numerator as unit
     */
    inline fun <OperandUnit : Unit<OperandUnit>,
            Operand1 : UnitOnlyEquatable<OperandUnit>,
            Operand2 : EquatableWithNoUnit<N>,
            reified N : Number<N>> unitDivisionByNoUnitOperation(o1: Operand1,
                                                                 o2: Operand2): AmountOperation<OperandUnit, NoUnit, OperandUnit, N> {
        return BaseEquatableDivision.divisionOperation(o1, o2)
    }

    /**
     *  Equatable Operation
     *
     *  NoUnit/unit = NoUnit/unit
     *
     *  @param o1 numerator with NO unit
     *  @param o2 denominator with unit
     *  @return division with NoUnit/denominatorUnitas unit
     */
    fun <Operand2Unit : UnitLike<Operand2Unit>,
            Operand1 : EquatableWithNoUnit<N>,
            Operand2 : EquatableWithUnit<Operand2Unit, N>,
            N : Number<N>> noUnitDivisionByUnitOperation(o1: Operand1,
                                                         o2: Operand2): Division<NoUnit, Operand2Unit, Div<NoUnit, Operand2Unit>, N> {
        return BaseEquatableDivision.divisionOperation(o1, o2)
    }

}