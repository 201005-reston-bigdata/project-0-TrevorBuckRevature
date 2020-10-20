package pj0_cli

import scala.io.StdIn
import scala.language.postfixOps
import scala.util.Random

import org.mongodb.scala._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}
import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

class Game {


  def menuOptions(): Unit = {
    println("\n\n*****MENU OPTIONS*****")
    println("New : Starts new game")
    println("Exit: Close the program")
    println("Options: Toggles On/Off option menus")
  }

  def turnOptions(): Unit = {
    println("**Turn Options**")
    println("Roll: Rolls dice")
    println("Save: Adds your current roll to your total score")
    println("Quit: Quits current game and returns to Main Menu")
  }

  def start(): Unit = {
    var running = true
    var playing = false
    var options = true
    var myTurn = true
    val diceRolls = FileUtil.getDiceRolls("26000DiceRolls.csv")
    val length = diceRolls.length
    var num = 0
    var myRoll = 0
    var turnScore = 0
    var pScore = 0
    var cScore = 0
    val codecRegistry = fromRegistries(fromProviders(classOf[Score]), MongoClient.DEFAULT_CODEC_REGISTRY)
    val client = MongoClient()
    val db = client.getDatabase("project0").withCodecRegistry(codecRegistry)
    val collection : MongoCollection[Score] = db.getCollection("scores")

    //helper functions for access and printing, to get us started + skip the Observable data type
    def getResults[T](obs: Observable[T]): Seq[T] = {
      Await.result(obs.toFuture(), Duration(10, SECONDS))
    }

    def printResults[T](obs: Observable[T]): Unit = {
      getResults(obs).foreach(println(_))
    }

    def printScore(playerScore: Int, compScore: Int): Unit = {
      println("****SCOREBOARD******")
      println("Player | Computer")
      println("-----------------")
      println(s"  $playerScore       $compScore\n\n\n")
    }


    while(running) {


      if (!playing) {
        if (options) menuOptions()
      } else {
        if (options && myTurn) turnOptions()
      }

      if (myTurn) {
        StdIn.readLine() toLowerCase match {
          case "new" =>
            playing = true
            num = Random.nextInt(length)
          case "exit" => running = false
          case "quit" => playing = false
          case "options" => options = false
          case "roll" =>
            myRoll = 0
            num += 1
            num = num % length
            myRoll += diceRolls(num).toInt
            num += 1
            num = num % length
            myRoll += diceRolls(num).toInt
            myRoll match {
              case 7 =>
                println("\n\n\nYOU ROLLED A 7!! DEVIL TAKES A TURN!!")
                myRoll = 0
                turnScore = 0
                myTurn = false

              case _ =>
                turnScore += myRoll
                println(s"\n\n\nYou rolled a $myRoll. Your total for the turn is $turnScore\n")
            }
          case "save" =>
            println("\n\nScore Saved. Devil takes a turn.")
            pScore += turnScore
            turnScore = 0
            myTurn = false

          case notRecognized => println(s"$notRecognized not a recognized command")
        }
        if (options && myTurn && playing) printScore(pScore, cScore)
      } else {
        // TODO: Devils Turn
        cScore += 12
        println("Devil rolled a 12")
        printScore(pScore, cScore)
        myTurn = true
      }

      if (cScore >= 100 || pScore >= 100) {
        println("\n\n****GAME OVER****")
        printScore(pScore, cScore)
        if (pScore >= 100) {
          println("CONGRATULATIONS, YOU WIN!!!\n\n")
          printResults(collection.insertOne(Score(pScore, cScore, "Win")))
        } else {
          println("DEVIL WINS!!\n\n")
          printResults(collection.insertOne(Score(pScore, cScore, "Loss")))
        }
        playing = false
        pScore = 0
        cScore = 0
        turnScore = 0
        myTurn = true
      }

    }
  }
}


// TODO: Add in Devil AI (Optional)

// TODO: Add in Database Reading features (win pct, last game, avg score)
// TODO: Add comments to code
