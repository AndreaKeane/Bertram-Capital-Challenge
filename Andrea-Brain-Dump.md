# Andrea's Working Doc of Things  

## TO DO 

X Clean up business logic  
- Do the routes / verbs make sense? 
- Data Validation, helpful error handling  
- OpenAPI / Swagger spec  
- Docker container  


## Params  

### Inputs  
Who are the people? 
Are they all attending every day?  
Which people purchase which products?
(Flexibility if one person decides to switch their order for a day)  

What are the menu options? How much do they cost?
(Flexibility for menu changes)


### Outputs  
Whose turn is it to pay for coffee?  


### Tools  

Maven - preferred for simple projects. Less flexibility, also less room for things to go wrong.  

https://www.baeldung.com/maven-directory-structure

### Notes  

Assuming that all parties pay the same tax and tip percentages, Tax and Tip don't change the outcome due to the distributive property of addition   
`a(b + c) = ab + ac`
`tax(latte + espresso) = tax * latte + tax * espresso`


## Least Common Multiple  

We could get this very close to perfect by using an LCM algorithm. That would likely result in the rotation basis being a very large number of days. Given that this coffee outing doesn't usually have iperfect attendance, a rotation set on a recurring 30+ day schedule seems overly complicated. 
Instead, I've added a variable to set the number of days we want the schedule to repeat on. 

