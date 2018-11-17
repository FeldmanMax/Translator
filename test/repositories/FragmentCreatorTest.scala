package repositories

import doobie.util.fragment.Fragment
import org.scalatest.FunSuite

class FragmentCreatorTest extends FunSuite {
  test("create fragment without params") {
    val sql: String = "select * from users where userid = 10"
    val fragment: Fragment = FragmentCreator.create(sql)
    assert(fragment.toString() == intoFragmentText(sql))
  }

  test("create fragment without params but with replacement") {
    val dbName: String = "users"
    val userId: Int = 10
    val sql: String = s"select * from $dbName where userid = $userId"
    val expectedSql: String = "select * from users where userid = 10"
    val fragment: Fragment = FragmentCreator.create(sql)
    assert(fragment.toString() == intoFragmentText(expectedSql), fragment.toString())
  }

  private def intoFragmentText(sql: String): String =s"""Fragment("$sql")"""
}
