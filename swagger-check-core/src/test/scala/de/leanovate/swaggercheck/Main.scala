package de.leanovate.swaggercheck


import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.generators.Generators
import de.leanovate.swaggercheck.schema.SwaggerAPI

object Main {
  val nodeFactory = JsonNodeFactory.instance

  def main(args: Array[String]): Unit = {
    val swaggerAPI = SwaggerAPI.parse(getClass.getClassLoader.getResourceAsStream("bookdb_api.yaml"))

    println(swaggerAPI)
    //    println(UUID.randomUUID().toString)
    println(Generators.regex.sample)
    println(Generators.regexMatch("[a-zA-Z0-9\\.]+@[a-z]+\\.[a-z]+").sample)
    println(Generators.regexMatch("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}").sample)
    println(Generators.regexMatch("^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$").sample)
//    val fail = "[1-v5P-d sv-wO-jdLaEIG-a4-duK4-fj-rt-yh1-s;M8EV-rE-w,:\\&\\&]+[oR2];?"
//    val gen = Generators.regexMatch(fail)
//    Range(0, 100).foreach {
//      idx =>
//        gen.sample.foreach {
//          sample =>
//            println(sample)
//val m =            Pattern.compile(fail).matcher(sample)
//            println(m.matches())
//        }
//    }
//    println(gen.sample)
    //    val swaggerGenerators = SwaggerGenerators(getClass.getResourceAsStream("/uber_api.yml"))
    //
    //    val json1 = swaggerGenerators.genJson("PriceEstimate")
    //
    //    println(json1.sample)
  }

}
