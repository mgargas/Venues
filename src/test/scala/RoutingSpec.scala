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

    "respond with message telling that player can't buy the venue" in {
      putVenueRequest(venueId, putVenueJson(venueName, venuePrice)) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
      postBuyVenueRequest(venueId, postBuyVenueJson("player1")) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "player1 can't afford Rynek Główny"
      }
    }

    "respond with message telling that player bought the venue" in {
      putVenueRequest(venueId, putVenueJson(venueName, venuePrice)) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
      postBuyVenueRequest(venueId, postBuyVenueJson("player2")) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "Rynek Główny was bought by player2 for 1000"
      }
      Get(s"/venues/$venueId") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String].contains("owner: player2")
      }
      Delete(s"/venues/$venueId") ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
    }

    "respond with message telling that venue has been already bought" in {
      putVenueRequest(venueId, putVenueJson(venueName, venuePrice)) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual venueId
      }
      postBuyVenueRequest(venueId, postBuyVenueJson("player2")) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "Rynek Główny was bought by player2 for 1000"
      }
      postBuyVenueRequest(venueId, postBuyVenueJson("player2")) ~> mainRoute ~> check {
        status shouldBe OK
        responseAs[String] shouldEqual "Rynek Główny has been already bought by player2 for 1000"
      }
    }

  }
}
