package in.kamranali.algorithms.apis


/**
  * In Scala [Immutable / Mutable] stack is a wrapper on top of list and is NOT performant and is deprecated
  *
  * How to use List as Stack
  * - stack push x becomes x :: list; stack.pop is list.tail.", "2.11.0"
  */
object ScalaStackAPIs extends App {

  var st = List.empty[Int]

  println("Pushing Elements \n")
  st = 0 :: st
  st = 1 :: st
  st = 2 :: st

  println(st)

  println("Popping Elements \n")

  println("First Pop Method \n")
  val data = st.head
  st = st.tail

  println(data)
  println(st)

  println("Second Pop Method \n")
  val data1 :: st1 = st

  println(data1)
  println(st1)

}
