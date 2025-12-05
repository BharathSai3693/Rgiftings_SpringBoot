# Rgiftings Spring Boot backend

This service powers the Rgiftings store with APIs for defining product attributes, managing products with those attributes (and optional extra pricing), and capturing customer orders with a full snapshot of the chosen options and pricing.

## Stack & configuration
- Spring Boot 4 (Web MVC + JPA/Hibernate), PostgreSQL driver, Lombok.
- Data source defaults: see `src/main/resources/application.properties` (Postgres on localhost, `spring.jpa.hibernate.ddl-auto=update`).
- CORS is open via `@CrossOrigin` on the controllers; all routes are under `/api`.

## Domain model
- Attributes: `AttributeType` (name + `inputType` such as text/file/select) with child `AttributeValue` options.
- Products: `Product` with pricing, stock, category, tax rate, timestamps, and a gallery of `ProductImage` rows (primary + additional images); has `ProductAttribute` entries that point to an `AttributeType`, use a display label, and hold `ProductAttributeValue` rows (linking to an `AttributeValue` plus an optional `extraPrice`).
- Orders: `Order` tracks user/guest info, totals, status, timestamps; `OrderItem` stores a snapshot of the product name/base price/tax and quantity plus line totals; `OrderItemAttribute` + `OrderItemAttributeValue` persist the selected attribute labels/values (or custom text/file uploads) and any extra price charged.

## API surface (under `/api`)
- Attributes:  
  - `GET /attribute` list attribute types with their values.  
  - `POST /attribute` create a type with values.  
  - `PUT /attribute/{id}` update type and value list (missing values are removed, new ones added).  
  - `DELETE /attribute/{id}` delete a type.
- Products:  
  - `GET /products` list products with attributes and extra-price values.  
  - `POST /product` create a product; payload includes `productAttributes` that reference existing `attributeTypeId`/`attributeValueId` entries.  
  - `PUT /product/{id}` update product fields; removes attributes/values omitted from the payload, allows adding new ones, and prevents changing an existing attribute’s type.  
  - `DELETE /product/{id}` delete a product.
- Orders:  
  - `POST /order/place` place an order for one or more products; the service recalculates line totals, applies per-attribute extra prices, stores a snapshot of names/prices, and marks status `PENDING`.  
  - `GET /orders/user/{userId}` returns order history with nested items and selected attributes (currently returns all orders; the user filter is not applied in the service implementation).

## Request shape highlights
- Product create/update expects `productAttributes` each with an `attributeTypeId`, a display label, and `productAttributeValues` pointing to existing `attributeValueId` records plus optional `extraPrice`.
- Order create accepts nested items/attributes; for free-form inputs (text/file) the `attributeValueId` can be `null` and the custom text/file URL is stored with zero extra price.

## Samples
- `samples/product_with_attributes.json` shows a sample product payload with attribute references.
