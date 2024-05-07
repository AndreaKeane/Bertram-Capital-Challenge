# Bertram Capital Technical Challenge

## Introduction

Bob, Jeremy, and the other 5 coworkers in the Bertram Labs office love coffee. In fact, everyday,
right after lunch, they walk down the street to their favorite coffee shop to grab a cup to go. Bob
always gets a cappuccino, Jeremy likes his coffee black, and the others have their favorite
coffee beverage too. To ease the checkout process, only one coworker pays for all the coffees
each day. As you can imagine, they have a problem every day: whose turn is it to pay for coffee?  

This solution constructs a sequence and identifies the next payer by evaluating who has the highest debt for a given day within the sequence. In case of a tie, the individual with both the highest debt and the largest outstanding balance will be assigned payment duties. This solution gradually approaches fairness, which can be seen by running a [6-day sequence with the Test Tab](http://localhost:8080/tab/test/day/6).  

This application implements a Springboot RESTful API, which provides a [swagger page](http://localhost:8080/swagger-ui/index.html#/).  
A [Postman Collection](BertramCapitalChallenge.postman_collection.json) is also available. 

## Usage

### Requirements

[Docker Desktop](https://docs.docker.com/get-docker/)

#### Project Dependencies  
Java: `21.0.2`  
Maven: `Apache Maven 3.9.6`

### Run Application

1. Clone the repository. 
2. Build and run the dockerized application using one of the following: 

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

### Demo Use Cases

1. Review the preloaded [Demo Data](http://localhost:8080/tab/demo) and [Test Data](http://localhost:8080/tab/test) to
   get an idea of what the Tab object looks like.
2. Check out [Today's Payer](http://localhost:8080/tab/demo/today) based on the preloaded demo data.
3. Check out the [Payer on Day 5](http://localhost:8080/tab/demo/day/5), which assumes we've paid the same tab for 5 trips in a row. 
4. Check out the [Payer Schedule](http://localhost:8080/tab/demo/schedule/5) for the next 5 days.

### Custom Tab Data

Users are able to add their personal data through the Tab CRUD API.

- To add your own tab, use the `GET /tab` endpoint
- To modify an existing tab, use the `PUT /tab` endpoint

The Tab request body should look like:

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
series. When this calculation is performed with date logic (`/tab/{tab_id}/today`), we assume the tab is executed once per day for the
entirety of the date range, including weekends.


## Future Development

### Technical Improvements

- Improve exception handling
- Endpoint to List All Tabs with Pagination
- Data validation isn't working well - I ended up hard-coding some of it where there's surely a cleaner implementation option. 
- Metrics and Monitoring

### Product Features

- Basic GUI
- Allow "overrides" such that if someone wants to change their order, or is absent for a day, or forgets their wallet,
  we can deal with it.
- Adjust for weekdays on the scheduling endpoint. Maybe allow users to specify a customer schedule (M/W/F, Tu/W/Th)
- "Make it even" endpoint that when called, calculates minimum number of transactions between parties to zero out the
  balances. How much does Andre need to Venmo Jeremy every Friday to keep our balances balanced? 

