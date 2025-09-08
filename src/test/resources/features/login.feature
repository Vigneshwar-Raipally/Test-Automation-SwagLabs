Feature: SauceDemo Login and Cross-Browser Testing

  Scenario: Valid login on default browser
    Given User opens the SauceDemo login page
    When User enters valid username "standard_user" and password "secret_sauce"
    Then User should be redirected to the products page

  Scenario Outline: Valid login on multiple browsers (Cross-Browser)
    Given User opens the SauceDemo login page on browser "<browser>"
    When User enters valid username "standard_user" and password "secret_sauce"
    Then User should be redirected to the products page

    Examples:
      | browser |
      | chrome  |
