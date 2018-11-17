package utils

import utils.errors.Error

object GeneralTypes {
  type ActualResult[T]       = Either[Error, T]

  type Condition[T]          = T               =>   Boolean

  type ThrowableAsError      = Throwable       => Error
}
