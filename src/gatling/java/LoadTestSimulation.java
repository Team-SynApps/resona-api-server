import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class LoadTestSimulation extends Simulation {

  // Add the HttpProtocolBuilder:
  HttpProtocolBuilder httpProtocol =
      http.baseUrl("https://computer-database.gatling.io")
          .acceptHeader("application/json")
          .contentTypeHeader("application/json");

  // Add the ScenarioBuilder:
  ScenarioBuilder myScenario = scenario("My Scenario")
      .exec(http("Request 1").get("/computers/"));

  // Add the setUp block:
  {
    setUp(
        myScenario.injectOpen(constantUsersPerSec(2).during(60))
    ).protocols(httpProtocol);
  }
}
