# Quote Service

Quote Service provides quotes for digital currency trades using data from the FTX orderbook. It handles requests to buy
or sell a particular amount of a currency (the base currency) with another currency (the quote currency).

## Route

```
POST /quote
```

## Request Body

- **action** (String): Either “buy” or “sell”
- **base_currency** (String): The currency to be bought or sold
- **quote_currency** (String): The currency to quote the price in
- **amount** (BigDecimal): The amount of the base currency to be traded _- Note that it was String in the doc._

### Request Body Example

```json
{
  "action": "buy",
  "base_currency": "BTC",
  "quote_currency": "TRYB",
  "amount": 0.12
}
```

## Response Body

- **total** (String): Total quantity of quote currency _- Note that it was String in the doc._
- **price** (String): The per-unit cost of the base currency _- Note that it was String in the doc._
- **currency** (String): The quote currency

### Response Body Example

```json
{
  "total": 54610.212,
  "price": 455085.1,
  "currency": "TRYB"
}
```

## Deployed Example

### URL

```
https://api.tufan.ee/quote
```

### Swagger

#### [Link](http://api.tufan.ee/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/quote-controller/quote)

### cURL

```shell
curl --location --request POST 'https://api.tufan.ee/quote' \
--header 'Content-Type: application/json' \
--data-raw '{
  "action": "buy",
  "base_currency": "BTC",
  "quote_currency": "TRYB",
  "amount": 0.12
}'
```
