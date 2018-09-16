package repositories

import doobie.util.fragment.Fragment
import doobie.util.query.Query

object FragmentCreator {
  def create(sql: String): Fragment = {
    Query(sql).toFragment().stripMargin
  }
}
