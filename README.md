# Bertram Capital Technical Challenge

## Introduction

Bob, Jeremy, and the other 5 coworkers in the Bertram Labs office love coffee. In fact, everyday,
right after lunch, they walk down the street to their favorite coffee shop to grab a cup to go. Bob
always gets a cappuccino, Jeremy likes his coffee black, and the others have their favorite
coffee beverage too. To ease the checkout process, only one coworker pays for all the coffees
each day. As you can imagine, they have a problem every day: who's turn is it to pay for coffee?

## Usage

### Requirements

[Docker Desktop](https://docs.docker.com/get-docker/) 

### Installation

Docker Compose

```shell
docker compose build
docker compose up
```

Docker Commands

```shell
docker build --tag bertram-capital-challenge -f Dockerfile .
docker run bertram-capital-challenge:latest
```

### Quick Start

1. Run the dockerized application
3. Review the preloaded [Demo Data](http://localhost:8080/tab/demo) and [Test Data](http://localhost:8080/tab/test) to
   get an idea of what the Tab object looks like.
3. Check out [Today's Payer](http://localhost:8080/tab/demo/today) based on the preloaded demo data.
4. Check out the [Payer on Day 5](http://localhost:8080/tab/demo/day/5), which assumes we've paid the same tab for 5 trips in a row. 
5. Check out the [Payer Schedule](http://localhost:8080/tab/demo/schedule/5) for the next 5 days.


### Interface

This application is currently backend-only and can be used through RESTful API endpoints. Once the application is
running locally, requests can be made 
- Using your favorite client (cURL, Postman, etc)
- On the [swagger page](http://localhost:8080/swagger-ui/index.html#/) by opening endpoint documentation and clicking "Try Me"
- Through the browser, as demonstrated in the Quick Start  

### Setup

Once the application is running, users are able to add their personal data through the Tab CRUD API.

- To add your own tab, use the `GET /tab` endpoint
- To modify an existing tab, use the `PUT /tab` endpoint

For convenience, the application has been initialized with two sample tabs for quick testing. These can
be modified using the `PUT /tab` endpoint.

`test_tab.json` with `tab_id` = "test"

```json
{
  "id": "test",
  "startDate": "05-01-2024",
  "items": {
    "personA": 1.00,
    "personB": 2.00,
    "personC": 3.00
  }
}
```

`demo_tab.json` with `tab_id` = "demo"

```json
{
  "id": "demo",
  "startDate": "01-01-2024",
  "items": {
    "Bob": 4.25,
    "Jeremy": 3.00,
    "Andrea": 3.50,
    "Karleen": 4.00,
    "Carl": 3.25,
    "Greg": 4.50,
    "Kirsten": 3.75
  }
}
```

### Querying

Users can make requests to the application through

[Swagger Interface](http://localhost:8080/swagger-ui/index.html#/)

[Postman](BertramCapitalChallenge.postman_collection.json)


## Description

### Definitions

**Tab**: Equivalent to a receipt - structured purchase information.

**Items**: A list of who spent how much. Analogous to an itemized receipt, but grouped by Person instead of Product.

**Person**: Just like it sounds - a single person. Might be Bob, might be Jeremy, might be Andrea.

**Balance**:  A lightweight structure for tracking who owes what. Each person has their own balance.

**Paid**: The amount of money tendered to the beloved coffee establishment.

**Spent**: The amount of money due to the beloved coffee establishment.

**Owe**: Owe = Amount Spent - Amount Paid. If the person is indebted, they will owe a (+) amount. If the person has paid
in full or more, they will owe a (-) amount

### Assumptions

**Tax & Tip**: The tax and tip are not explicitly accounted for. By the
[distributive property](https://en.wikipedia.org/wiki/Distributive_property), adding a constant rate of tax and/or tip
does not change either "Who pays today?" or "How many days until fair?".
`(Person 1 Cost x Tax) + (Person 2 Cost x Tax) + (Person 3 Cost x Tax) = Tax x (Person 1 Cost + Person 2 Cost + Person 3 Cost)`.

**Modelling of Products & Menu**: As an MVP solution, we associate beverage cost directly with a person and do not
build out a person <> product(s) <> cost relationship. This allows for quicker application setup and a lightweight user
experience.

**Payments in Series**: When requesting payment information, we assume that the same tab is used for each day in the
series. When this
calculation is performed with date logic (`/tab/{tab_id}/today`), we assume the tab is executed once per day for the
entirety of the date range, including weekends.


## Future Development

## Technical Improvements

- Improve exception handling
- Endpoint to List All Tabs with Pagination
- Data validation isn't working well - ended up hard-coding some of it where I thought the annotation should work.
- Metrics and Monitoring

## Product Features

- Basic GUI
- Allow "overrides" such that if someone wants to change their order, or is absent for a day, or forgets their wallet,
  we can deal with it.
- Adjust for weekdays on the scheduling endpoint
- "Make it even" endpoint that when called, calculates minimum number of transactions between parties to zero out the
  balances.  

