
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import org.apache.spark.sql._
import org.apache.spark.sql.SQLContext

import org.apache.log4j.Logger
import org.apache.log4j.Level

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DoubleType

import scala.reflect.runtime.universe
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.tuning.ParamGridBuilder
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.Row
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.tuning.TrainValidationSplit
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.mllib.evaluation.MulticlassMetrics

object sqlAnalysis {
  def main(args: Array[String]): Unit ={
      val conf = new SparkConf().setAppName("HelloSpark").setMaster("local")
      Logger.getLogger("org").setLevel(Level.OFF)
      Logger.getLogger("akka").setLevel(Level.OFF)
      val sc = new SparkContext(conf)
      val sqlContext = new SQLContext(sc)
      
      val df1 = sqlContext.read.format("csv").option("header","true").load("C:/Users/spchandgude/Desktop/Scala Project/cpdata.csv")
      df1.registerTempTable("cpdata")
      val df2 = sqlContext.read.format("csv").option("header","true").load("C:/Users/spchandgude/Desktop/Scala Project/cost_of_production.csv")
      df2.registerTempTable("cost_of_production")
      val df3 = sqlContext.read.format("csv").option("header","true").load("C:/Users/spchandgude/Desktop/Scala Project/pesticides.csv")
      df3.registerTempTable("pesticides")
      val df4 = sqlContext.read.format("csv").option("header","true").load("C:/Users/spchandgude/Desktop/Scala Project/loans.csv")
      df4.registerTempTable("loans")
      val df5 = sqlContext.read.format("csv").option("header","true").load("C:/Users/spchandgude/Desktop/Scala Project/tenure.csv")
      df5.registerTempTable("tenure")
      
      println("---------------------Schema of tables:---------------------")
        df1.printSchema()
        println()
        df2.printSchema()
        println()
        df3.printSchema()
        println()
        df4.printSchema()
        println()
        df5.printSchema()
        println()
      
        println("---------------------Toatl count of data:---------------------")
      
      println(df1.count())
      
      println("---------------------Distinct crops are:---------------------")
     
      sqlContext.sql("select Distinct label AS Crops FROM cpdata").show(100,truncate  = false);
    //sqlContext.sql("select label FORM cpdata ").show(100,truncate  = false);
      
      println()
      
      println("---------------------Average humidity for the crops to grow: ---------------------")
      sqlContext.sql("select label As Crop, AVG(humidity) Average_humidity FROM cpdata GROUP BY label").show(100,truncate  = false);
      println()
      
      println("---------------------Average temperatures for the crops to grow: ---------------------")
      sqlContext.sql("select label As Crop, AVG(temperature) Average_temperature FROM cpdata GROUP BY label").show(100,truncate  = false);
      println()
      
      println("---------------------Average ph of soil for the crops to grow: ---------------------")
      sqlContext.sql("select label As Crop, AVG(ph) Average_Ph_of_Soil FROM cpdata GROUP BY label").show(100,truncate  = false);
      println()
      
      println("---------------------Average rainfall for the crops to grow: ---------------------")
      sqlContext.sql("select label As Crop, AVG(rainfall) Average_rainfall FROM cpdata GROUP BY label").show(100,truncate  = false);
      println()
      
      
       println("---------------------Average numbers of days required to grow a crop in Months  ---------------------")
      sqlContext.sql("select Crops,(mintime + maxtime)/2 AS Average_time_required FROM tenure").show(100,truncate  = false);
      println()
      
      println("---------------------Diseases and pesticides for particaula crop WHEAT  ---------------------")
      sqlContext.sql("select Crop,Diseases,Pesticide FROM pesticides WHERE Crop = 'WHEAT'").show(100,truncate  = false);
      println()
      
      
      println("--------------------- Count of Diseases for crops ---------------------")
      sqlContext.sql("select Crop,COUNT(Diseases) FROM pesticides GROUP BY Crop").show(100,truncate  = false);
      println()
      
      println("--------------------- Total cost of production of crops ---------------------")
      sqlContext.sql("select Field_Preparation,Nursery_and_planting_sowing,Weeding,Protection,Fertilizers,Wages,Staking__transport_and_other_expenses,Field_Preparation+Nursery_and_planting_sowing+Weeding+Protection+Fertilizers+Wages+Staking__transport_and_other_expenses AS Total_Cost FROM cost_of_production ").show(100,truncate  = false);
      println()
      
      
      println("--------------------- List of top 3 bank which providing loans to farmers at less Interest---------------------")
      sqlContext.sql("SELECT Bank,interest FROM loans ORDER BY int(interest)  ").show(3,truncate  = false);
      println()
  }

}