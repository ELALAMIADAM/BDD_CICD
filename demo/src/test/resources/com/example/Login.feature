Feature: orangehrmlive

  Background:
    Given go to "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"
  Scenario: User Story
    When Enter username "Admin" and password "admin123"
    And Click Login button
    Then Verify Existance of "My Actions" and "Quick Launch" 
