package in.kamranali.fpcourse.list

import scala.annotation.tailrec
// Where ever recursive call occurs its the last expression of its code branch

sealed abstract class RList[+T] {
  def head: T
  def tail: RList[T]
  def isEmpty: Boolean

  // Scala methods ending with :: are right associative
  def ::[S >: T](element: S): RList[S] = new ::(element, this) // Common impl for both RNil and Cons

  // GET K-th element
  def apply(index: Int): T

  // Calculate Length of List
  def length: Int

  // Reverse the List
  def reverse: RList[T]

  // concatenate another list to this one
  def ++[S >: T](anotherList: RList[S]): RList[S]

  // remove the Kth Element
  def removeAt(index: Int): RList[T]

  // Implement the Big 3
  def map[S](f: T => S): RList[S]
  def flatMap[S](f: T => RList[S]): RList[S]
  def filter(f: T => Boolean): RList[T]

}

case object RNil extends RList[Nothing] {
  override def head: Nothing = throw new NoSuchElementException
  override def tail: RList[Nothing] = throw new NoSuchElementException
  override def isEmpty: Boolean = true

  override def toString: String = "[]"
  override def apply(index: Int): Nothing = throw new NoSuchElementException
  override def length: Int = 0
  override def reverse: RList[Nothing] = this
  override def ++[S >: Nothing](anotherList: RList[S]): RList[S] = anotherList
  override def removeAt(index: Int): RList[Nothing] = this
  override def map[S](f: Nothing => S): RList[S] = this
  override def flatMap[S](f: Nothing => RList[S]): RList[S] = this
  override def filter(f: Nothing => Boolean): RList[Nothing] = this
}

case class ::[+T](override val head: T, override val tail: RList[T]) extends RList[T] {

  override def isEmpty: Boolean = false
  override def toString: String = {

    @tailrec // As whenever recursion occurs its the last expression in its code branch
    def toStringTailRec(remaining: RList[T], result: String): String = {
      if (remaining.isEmpty) result
      else if (remaining.tail.isEmpty) s"$result${remaining.head}"
      else toStringTailRec(remaining.tail, s"$result${remaining.head}, ")
    }

    "[" + toStringTailRec(this, "") + "]"

  }

  // Complexity -> O(N)
  override def apply(index: Int): T = {
    @tailrec
    def applyTailRec(idx: Int, remaining: RList[T]): T = {
      if (idx == index) remaining.head
      else applyTailRec(idx + 1, remaining.tail)
    }

    if (index < 0) throw new NoSuchElementException
    else applyTailRec(0, this)
  }

  // Complexity -> O(N)
  override def length: Int = {
    @tailrec
    def lengthTailRec(remaining: RList[T], accumulator: Int): Int = {
      if (remaining.isEmpty) accumulator
      else lengthTailRec(remaining.tail, accumulator + 1)
    }

    lengthTailRec(this, 0)
  }

  // Complexity -> O(N)
  override def reverse: RList[T] = {
    @tailrec
    def reverseTailrec(remaining: RList[T], accumulator: RList[T]): RList[T] = {
      if (remaining.isEmpty) accumulator
      else reverseTailrec(remaining.tail, remaining.head :: accumulator)
    }

    reverseTailrec(this, RNil)
  }

  // Complexity -> O(anotherList) + O(N + anotherList)
  override def ++[S >: T](anotherList: RList[S]): RList[S] = {
    @tailrec
    def concatTailRec(remainingList: RList[S], accum: RList[S]): RList[S] = {
      if (remainingList.isEmpty) accum
      else concatTailRec(remainingList.tail, remainingList.head :: accum)
    }

    concatTailRec(anotherList, this.reverse).reverse
  }

  // Complexity -> O(N)
  def removeAt(index: Int): RList[T] = {
    @tailrec
    def removeTailRec(idx: Int, remaining: RList[T], predecessors: RList[T]): RList[T] = {

      if (remaining.isEmpty) predecessors.reverse
      else if (idx == index) predecessors.reverse ++ remaining.tail
      else removeTailRec(idx + 1, remaining.tail, remaining.head :: predecessors) // Since we have only prepend Operator
    }

    if (index < 0) this
    else removeTailRec(0, this, RNil)
  }

  // Complexity -> O(N)
  override def map[S](f: T => S): RList[S] = {
    @tailrec
    def mapTailrec(remaining: RList[T], accumulator: RList[S]): RList[S] = {

      if (remaining.isEmpty) accumulator.reverse
      else mapTailrec(remaining.tail, f(remaining.head) :: accumulator)
    }

    mapTailrec(this, RNil)
  }

  // Complexity -> O(N * M)
  override def flatMap[S](f: T => RList[S]): RList[S] = {
    @tailrec
    def flatMapTailrec(remaining: RList[T], accumulator: RList[S]): RList[S] = {

      if (remaining.isEmpty) accumulator.reverse
      else flatMapTailrec(remaining.tail, f(remaining.head).reverse ++ accumulator)
    }

    flatMapTailrec(this, RNil)
  }

  // Complexity -> O(N)
  override def filter(predicate: T => Boolean): RList[T] = {
    @tailrec
    def filterTailrec(remaining: RList[T], predecessors: RList[T]): RList[T] = {

      if (remaining.isEmpty) predecessors.reverse
      else if (predicate(remaining.head)) filterTailrec(remaining.tail, remaining.head :: predecessors)
      else filterTailrec(remaining.tail, predecessors)
    }

    filterTailrec(this, RNil)
  }

}

object RList {
  def from[T](iterable: Iterable[T]): RList[T] = {
    @tailrec
    def convertToRListTailrec(remaining: Iterable[T], acc: RList[T]): RList[T] = {
      if (remaining.isEmpty) acc
      else convertToRListTailrec(remaining.tail, remaining.head :: acc)
    }

    convertToRListTailrec(iterable, RNil).reverse
  }
}
object ListProblems extends App {

  val aSmallList = 1 :: 2 :: 3 :: 4 :: RNil // ::(1, ::(2, ::(3, RNil)))
  println(aSmallList)
  println(aSmallList(2)) // 3
  println(aSmallList.length) // 3
  println(aSmallList.reverse) // [3, 2, 1]
  println(RList.from(1 to 5)) // [1, 2, 3, 4, 5]
  println(aSmallList ++ (5 :: 6 :: RNil))
  println(aSmallList.removeAt(1))
  println(aSmallList.map(_ * 2))
  println(aSmallList.flatMap(x => x :: (2 * x) :: RNil))
  println(aSmallList.filter(_ %2 == 1))

}
