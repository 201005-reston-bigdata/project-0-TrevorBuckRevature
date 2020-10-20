package pj0_cli

import org.bson.types.ObjectId

case class Score(_id: ObjectId, playerScore: Int, compScore: Int, outcome: String) {}

object Score {
  def apply(playerScore:Int, compScore:Int, outcome:String) : Score = Score(new ObjectId(), playerScore, compScore, outcome)
}
