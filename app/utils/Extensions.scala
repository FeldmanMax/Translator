package utils

import doobie.util.fragment.Fragment
import repositories.FragmentCreator
import utils.GeneralTypes.ActualResult
import utils.errors._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object Extensions {

  implicit class OptionExtensions[T](val option: Option[T]) {
    def cata[Q, S](left: => Q, right: T => S): Either[Q, S] = {
      option match {
        case None         =>  Left(left)
        case Some(value)  =>  Right(right(value))
      }
    }

    def toEither[L, R](none: => L, some: T => Either[L, R]): Either[L, R] = {
      option match {
        case None     =>  Left(none)
        case Some(v)  =>  some(v)
      }
    }
  }

  implicit class EitherExtensions[L, R](val either: Either[L, R]) {
    def to[S](left: L => S, right: R => S): S = {
      either match {
        case Left(error)    =>  left(error)
        case Right(result)  =>  right(result)
      }
    }

    def toFuture: Future[Either[L, R]] = {
      Future.successful { either }
    }
  }

  implicit class BooleanExtensions(bool: Boolean) {
    def toOption[T](tran: => T): Option[T] = {
      if(bool)  Some(tran)
      else      None
    }

    def cata[L, R](left: => L, right: => R): Either[L, R] = {
      if(bool)  Right(right)
      else      Left(left)
    }

    def to[R](ifFalse: => R, ifTrue: => R): R = if(bool) ifTrue else ifFalse
  }

  implicit class FutureExtensions[T](val future: Future[T]) {
    def attemptAction[R](action: T => Either[Error, R])(implicit ec: ExecutionContext): Future[ActualResult[R]] = {
      attempt[Error, R]((t: Throwable) => ThrowableError(t), action)
    }

    def attempt[L, R](left: Throwable => L, action: T => Either[L, R])(implicit ec: ExecutionContext): Future[Either[L, R]] = {
      val p = Promise[Either[L,R]]()
      future.onComplete {
        case Success(t)  => p success action(t)
        case Failure(ex) => p success Left(left(ex))
      }
      p.future
    }
  }

  implicit class StringExtensions(val string: String) {
    def toFragment: Fragment = FragmentCreator.create(string)
  }
}
