# Buy Venues
This project is a simple proof-of-concept application that allows adding `venues` and buying them for money.

## Usage
The application can be started by executing `sbt run`.

### Creating/updating a venue
You can use `PUT` and provide your own `UUID` to be sure that only one venue is created.
```
> curl -XPUT -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1 -d '{
  "name": "Rynek Główny",
  "price": 1000
}' "687e8292-1afd-4cf7-87db-ec49a3ed93b1"
```

### Getting all venues
```
> curl http://localhost:8080/venues
[
  {
    "id": "687e8292-1afd-4cf7-87db-ec49a3ed93b1",
    "name": "Rynek Główny",
    "price": 1000
  }
]
```

### Deleting venues
```
> curl -XDELETE "http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1"
"687e8292-1afd-4cf7-87db-ec49a3ed93b1"
```

### Hardcoded players
For now, two players are hardcoded and they are:
- `id=player1`, `money=500`
- `id=player2`, `money=2000`

Each restart of the application resets the state to the above.

### Buying a venue

#### Scenario 1: Buying a venue when player can't afford it
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1/buy -d '{
  "playerId": "player1"
}'
"player1 can't afford Rynek Główny"
```

#### Scenario 2: Buying a venue when player can afford it
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1/buy -d '{
  "playerId": "player2"
}'
"Rynek Główny was bought by player2 for 1000"
```

```
> curl http://localhost:8080/venues
[
  {
    "id": "687e8292-1afd-4cf7-87db-ec49a3ed93b1",
    "name": "Rynek Główny",
    "price": 1000,
    "owner:" "player2"
  }
]
```

#### Scenario 3: Buying a venue that has been already bought
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1/buy -d '{
  "playerId": "player2"
}'
"Rynek Główny was bought by player2 for 1000"
```

```
> curl http://localhost:8080/venues
[
  {
    "id": "687e8292-1afd-4cf7-87db-ec49a3ed93b1",
    "name": "Rynek Główny",
    "price": 1000,
    "owner:" "player2"
  }
]
```
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1/buy -d '{
  "playerId": "player2"
}'
"Rynek Główny has been already bought by player2 for 1000"
```