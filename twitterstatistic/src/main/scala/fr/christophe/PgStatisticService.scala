package fr.christophe

import com.google.inject.{ImplementedBy, Inject}
import com.twitter.inject.Logging

case class Word(word: String)

@ImplementedBy(classOf[PgStatisticService])
trait StatisticService {
  def getFirst10: Array[Word]
}

class PgStatisticService @Inject()() extends StatisticService {

  //PG query
  override def getFirst10: Array[Word] = ???


}