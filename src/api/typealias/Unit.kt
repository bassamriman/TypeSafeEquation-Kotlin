package api.`typealias`

import api.Times

typealias Times2<U1, U2> = Times<U1, U2>
typealias Times3<U1, U2, U3> = Times<U1, Times<U2, U3>>
typealias Times4<U1, U2, U3, U4> = Times<U1, Times3<U2, U3, U4>>
typealias Times5<U1, U2, U3, U4, U5> = Times<U1, Times4<U2, U3, U4, U5>>