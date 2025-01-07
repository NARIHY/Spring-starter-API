Feature: TestController API

  Background:
    * def baseUrl = 'http://localhost:8080'
    * url baseUrl
    * print 'Base URL is:', baseUrl
    * def req = read('./requests.json')
    * def res = read('./responses.json')

  Scenario: Manage Test Entities

    # Should get all entities
    Given path 'api', 'v1', 'test'
    When method GET
    Then status 200

    # Should create a new entity
    Given path 'api', 'v1', 'test'
    And request req.createTest
    When method POST
    Then status 201
    * print 'Actual response for creation:', response

    # Should get all entities
    Given path 'api', 'v1', 'test'
    When method GET
    Then status 200
    * print 'Should get all entities:', response

    # Should get a single entity
    Given path 'api', 'v1', 'test', '1'
    When method GET
    Then status 200
    * print 'Should get a single entity:', response

    # Should not find a non-existent entity
    Given path 'api', 'v1', 'test', '9999'
    When method GET
    Then status 404



    # Should update an existing entity
    Given path 'api', 'v1', 'test', '1'
    And request req.updateTest
    When method PUT
    Then status 200

    # Should delete an existing entity
    Given path 'api', 'v1', 'test', '1'
    When method DELETE
    Then status 200

    # Should not delete a non-existent entity
    Given path 'api', 'v1', 'test', '9999'
    When method DELETE
    Then status 500
