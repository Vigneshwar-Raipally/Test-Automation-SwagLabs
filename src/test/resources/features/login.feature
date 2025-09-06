Feature: Login functionality

  Scenario: Successful login with valid credentials
    Given User is on the SauceDemo login page
    When User enters valid username "standard_user" and password "secret_sauce"
    Then User should be redirected to the products page
