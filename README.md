# Vending Machine System (Java)

## Overview

* Java-based vending machine system using LLD principles
* Implements **State Pattern** and **Strategy Pattern**

## Key Features

* Inventory management (add/remove items)
* Multiple payment methods (coin, card)
* Product selection via code
* Automatic state transitions
* Change calculation
* Out-of-stock handling

## Design Patterns

### State Pattern

Handles machine behavior based on state:

* Idle → HasMoney → Selection → Dispense → Idle
* OutOfStock (when no items available)

### Strategy Pattern

Handles payment methods:

* Coin payment
* Card payment
* Easily extendable (e.g., UPI)

## Core Components

* **Item** → Product with type & price
* **ItemShelf** → Holds items, tracks availability
* **Inventory** → Manages all shelves
* **VendingMachine** → Main controller (context)
* **State Classes** → Control flow
* **Payment Strategies** → Handle payment logic

## Flow

1. Insert coin / choose card
2. Select product
3. Validate payment
4. Dispense item
5. Return change (if any)

## Limitations

* No real payment integration
* No UI (console only)
* Not thread-safe

## Future Improvements

* Add refund/cancel feature
* Add UPI payment
* Make system concurrent
* Add database support

## Run

```bash
javac Main.java
java Main
```
