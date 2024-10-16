import java.util.List;
import java.util.Optional;

public class User {
    private String name;
    private List<Order> orderList;

    public User() {
    }

    public User(String name, List<Order> orderList) {
        this.name = name;
        this.orderList = orderList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<List<Order>> getOrderList() {
        return  Optional.ofNullable(orderList);
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", orderList=" + orderList +
                '}';
    }

}
