import java.util.List;

public class Order {
    private int orderID;
    private List<Product> products;

    public Order(int orderID, List<Product> products) {
        this.orderID = orderID;
        this.products = products;
    }

    public int getOrderID() {
        return orderID;
    }

    public List<Product> getProductList() {
        return products;
    }

    public double getTotalPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", products=" + products +
                '}';
    }
}
