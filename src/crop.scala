import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._


import scala.io.Source
import scala.util.Random
import org.apache.mahout.math.DenseVector
import org.apache.mahout.math.{ Vector => MahoutVector }
import scala.collection.mutable.ArrayBuffer
import org.apache.mahout.vectorizer.encoders.Dictionary
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression
import org.apache.mahout.classifier.sgd.L2
import java.io.File
import java.io.PrintStream

object crop {
  Logger.getLogger("org").setLevel(Level.ERROR)
  
  def main(args: Array[String]): Unit = {
    val irisData = if (args.length > 0) args(0)
    else "C:/Users/spchandgude/Desktop/Scala Project/cpdata.csv"

    val dict = new Dictionary()
    var features = ArrayBuffer[MahoutVector]()
    var target = ArrayBuffer[Int]()
    // Read data from file
    // temperature,humidity,ph,rainfall,label
    Source.fromFile(irisData).getLines().drop(1) // skip header
      .filter(_ != null).map(_.split(",")).filter(_.length == 5)
      .foreach { elems =>
        val v: MahoutVector = new DenseVector(5)
        v.set(0, 1) // constant term
        v.set(1, elems(0).toDouble) //temperature
        v.set(2, elems(1).toDouble) //humidity
        v.set(3, elems(2).toDouble) //ph
        v.set(4, elems(3).toDouble) //rainfall
        val categoryCode = dict.intern(elems(4))
        target += categoryCode
        features += v
      }

  //  println(irisData)
    
  
    val dataIndices = (0 until features.length).toList
    val order = Random.shuffle(dataIndices)
    val (train, rest) = order.splitAt(4200)
    val test = rest.take(2000)

  
    val correct = Array.ofDim[Int](test.size + 1)

    val numClasses = 31
    val numFeatures = 5
    val totalRuns = 200
    val trainingPasses = 10
    
    for (run <- 0 until totalRuns) {
     
      val lr = new OnlineLogisticRegression(numClasses, numFeatures, new L2(1))
      for (pass <- 0 until trainingPasses) {
        for (k <- Random.shuffle(train)) {
          lr.train(target(k), features(k))
        }
      }

 
      var numCorrect = 0

      val originalCount = Array.ofDim[Int](numClasses)
      val classifyCount = Array.ofDim[Int](numClasses)
      for (k <- test) {
        val v = features(k)
        val t = target(k)
        originalCount(t) += 1
        // classify the feature vector in test sample
        val r = lr.classifyFull(features(k)).maxValueIndex()

        classifyCount(r) += 1
        val isCorrect = r == target(k)
        numCorrect += (if (isCorrect) 1 else 0)
      }



      correct(numCorrect) += 1
    }
     val output = new File("output.dat")
    val stream = new PrintStream(output)
    var maccurracy = 0.0
    val table = (0 until Math.floor(0.99 * test.length).toInt).map { i =>
      val inaccurateTrials = correct(i)
      val accuracy = 100.0 * i / test.length
      if (0 != inaccurateTrials) {
        if(accuracy>maccurracy){
          maccurracy=accuracy
        }
        
      }
      (accuracy, inaccurateTrials)
    }
     
     println("Accurracy: "+ maccurracy)

  }
}