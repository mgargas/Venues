import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import WebServer.mainRoute
class RoutingSpec extends WordSpec with Matchers with ScalatestRouteTest {
  WebServer.main(Array.empty)
  "Service" should {

    "respond with empty venues list" in {
      Get("/venues") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "[]"
      }
    }

  }
}
