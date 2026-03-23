import java.util.*;

// ================= ENUMS =================
enum ItemType {
    COKE, PEPSI, JUICE, SODA
}

enum Coin {
    ONE_RUPEE(1), TWO_RUPEES(2), FIVE_RUPEES(5), TEN_RUPEES(10);

    public int value;
    Coin(int value) { this.value = value; }
}

// ================= MODEL =================
class Item {
    private ItemType type;
    private int price;

    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}

class ItemShelf {
    private int code;
    private List<Item> items = new ArrayList<>();
    private boolean isSoldOut;

    public ItemShelf(int code) {
        this.code = code;
    }

    public int getCode() { return code; }
    public List<Item> getItems() { return items; }
    public boolean checkIsSoldOut() { return isSoldOut; }

    public void addItem(Item item) {
        items.add(item);
        isSoldOut = false;
    }

    public void removeItem() {
        if (!items.isEmpty()) {
            items.remove(0);
        }
        if (items.isEmpty()) isSoldOut = true;
    }
}

// ================= INVENTORY =================
class Inventory {
    private ItemShelf[] inventory;

    public Inventory(int size) {
        inventory = new ItemShelf[size];
        int code = 101;
        for (int i = 0; i < size; i++) {
            inventory[i] = new ItemShelf(code++);
        }
    }

    public ItemShelf[] getInventory() { return inventory; }

    public void addItem(Item item, int code) throws Exception {
        for (ItemShelf shelf : inventory) {
            if (shelf.getCode() == code) {
                shelf.addItem(item);
                return;
            }
        }
        throw new Exception("Invalid Code");
    }

    public Item getItem(int code) throws Exception {
        for (ItemShelf shelf : inventory) {
            if (shelf.getCode() == code && !shelf.checkIsSoldOut()) {
                return shelf.getItems().get(0);
            }
        }
        throw new Exception("Item not available");
    }

    public void removeItem(int code) throws Exception {
        for (ItemShelf shelf : inventory) {
            if (shelf.getCode() == code) {
                shelf.removeItem();
                return;
            }
        }
        throw new Exception("Invalid Code");
    }

    public boolean hasItems() {
        for (ItemShelf shelf : inventory) {
            if (!shelf.checkIsSoldOut()) return true;
        }
        return false;
    }
}

// ================= PAYMENT STRATEGY =================
interface PaymentStrategy {
    boolean processPayment(double amount);
}

class CoinPaymentStrategy implements PaymentStrategy {
    private List<Coin> coins;

    public CoinPaymentStrategy(List<Coin> coins) {
        this.coins = coins;
    }

    public boolean processPayment(double amount) {
        int total = coins.stream().mapToInt(c -> c.value).sum();
        return total >= amount;
    }
}

class CardPaymentStrategy implements PaymentStrategy {
    public boolean processPayment(double amount) {
        System.out.println("Card payment of " + amount + " successful");
        return true;
    }
}

// ================= STATE PATTERN =================
interface State {
    void handle(VendingMachine vm);
}

class IdleState implements State {
    public void handle(VendingMachine vm) {
        if (!vm.inventory.hasItems()) {
            vm.setState(new OutOfStockState());
        } else if (!vm.coins.isEmpty()) {
            vm.setState(new HasMoneyState());
        }
    }
}

class HasMoneyState implements State {
    public void handle(VendingMachine vm) {
        vm.setState(new SelectionState());
    }
}

class SelectionState implements State {
    public void handle(VendingMachine vm) {
        vm.setState(new DispenseState());
    }
}

class DispenseState implements State {
    public void handle(VendingMachine vm) {
        try {
            Item item = vm.inventory.getItem(vm.selectedCode);

            if (!vm.paymentStrategy.processPayment(item.getPrice())) {
                System.out.println("Payment Failed");
                vm.reset();
                return;
            }

            vm.inventory.removeItem(vm.selectedCode);
            System.out.println("Dispensed: " + item.getType());

            int change = vm.getBalance() - item.getPrice();
            if (change > 0) System.out.println("Change returned: " + change);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        vm.reset();
        vm.setState(new IdleState());
    }
}

class OutOfStockState implements State {
    public void handle(VendingMachine vm) {
        System.out.println("Out of stock!");
    }
}

// ================= CONTEXT =================
class VendingMachine {
    State state;
    Inventory inventory;
    List<Coin> coins = new ArrayList<>();
    PaymentStrategy paymentStrategy;
    int selectedCode;

    public VendingMachine() {
        inventory = new Inventory(10);
        state = new IdleState();
    }

    public void setState(State state) {
        this.state = state;
    }

    public void insertCoin(Coin coin) {
        coins.add(coin);
        paymentStrategy = new CoinPaymentStrategy(coins);
        state.handle(this);
    }

    public void payByCard() {
        paymentStrategy = new CardPaymentStrategy();
        state.handle(this);
    }

    public void selectItem(int code) {
        this.selectedCode = code;
        state.handle(this);
    }

    public int getBalance() {
        return coins.stream().mapToInt(c -> c.value).sum();
    }

    public void reset() {
        coins.clear();
        paymentStrategy = null;
        selectedCode = 0;
    }
}

// ================= MAIN =================
public class Main {
    public static void main(String[] args) throws Exception {
        VendingMachine vm = new VendingMachine();

        // Fill inventory
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setType(ItemType.COKE);
            item.setPrice(10);
            vm.inventory.addItem(item, 101 + i);
        }

        // Flow
        vm.insertCoin(Coin.TEN_RUPEES);
        vm.selectItem(101);
        vm.state.handle(vm);
    }
}
