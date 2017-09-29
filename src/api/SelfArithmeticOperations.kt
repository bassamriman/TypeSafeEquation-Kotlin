package api

interface SelfArithmeticOperable<Self : SelfArithmeticOperable<Self>> : SelfToOtherArithmeticOperable<Self, Self>
interface SelfDividableOperable<Self : SelfDividableOperable<Self>> : SelfToOtherDividableOperable<Self, Self>
interface SelfMultiplicativeOperable<Self : SelfMultiplicativeOperable<Self>> : SelfToOtherMultiplicativeOperable<Self, Self>
interface SelfAdditionOperable<Self : SelfAdditionOperable<Self>> : SelfToOtherAdditionOperable<Self, Self>
interface SelfSubtractableOperable<Self : SelfSubtractableOperable<Self>> : SelfToOtherSubtractableOperable<Self, Self>

interface SelfToOtherArithmeticOperable<Self : SelfToOtherArithmeticOperable<Self, Result>, out Result> :
        SelfToOtherDividableOperable<Self, Result>,
        SelfToOtherMultiplicativeOperable<Self, Result>,
        SelfToOtherAdditionOperable<Self, Result>,
        SelfToOtherSubtractableOperable<Self, Result>

interface SelfToOtherDividableOperable<Self : SelfToOtherDividableOperable<Self, Result>, out Result> {
    operator fun div(other: Self): Result
}

interface SelfToOtherMultiplicativeOperable<Self : SelfToOtherMultiplicativeOperable<Self, Result>, out Result> {
    operator fun times(other: Self): Result
}

interface SelfToOtherAdditionOperable<Self : SelfToOtherAdditionOperable<Self, Result>, out Result> {
    operator fun plus(other: Self): Result
}

interface SelfToOtherSubtractableOperable<Self : SelfToOtherSubtractableOperable<Self, Result>, out Result> {
    operator fun minus(other: Self): Result
}

