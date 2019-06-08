import RequestHelpers._
import WebServer.mainRoute
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class RoutingSpec extends WordSpec with Matchers with ScalatestRouteTest {
  WebServer.main(Array.empty)
  val venueId = "687e8292-1afd-4cf7-87db-ec49a3ed93b1"
  val venueName = "Rynek Główny"
  val venuePrice = BigDecimal(1000)
  "Service" should {

    "respond with empty venues list" in {
      Get("/venues") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "[]"
      }
    }

    "respond with id of venue that was put" in {
      putVenueRequest(venueId, putVenueJson(venueName, venuePrice)) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
    }

    "respond with non empty venues list" in {
      Get("/venues") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String].contains("Rynek Główny")
      }
    }

    "respond with id after deleting existing venue" in {
      putVenueRequest(venueId, putVenueJson(venueName, venuePrice)) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
      Delete(s"/venues/$venueId") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
      Get("/venues") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "[]"
      }
    }

  }
}
