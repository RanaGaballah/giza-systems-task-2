import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        //Creating Products
        Product product1 = new Product("Laptop", 999.99);
        Product product2 = new Product("Smartphone", 799);
        Product product3 = new Product("Book", 14.99);
        Product product4 = new Product("Smartwatch", 100);

        //Creating Orders
        Order order1 = new Order(1,Arrays.asList(product1,product2));
        Order order2 = new Order(2, Arrays.asList(product3));
        Order order3 = new Order(3, Arrays.asList(product4));

        //Creating users
        List<User> users = Arrays.asList(
                new User("Omar", Arrays.asList(order1, order2)),
                new User("Dalia", Arrays.asList(order3)),
                new User("Layla", null)
        );


        List<Integer> orderIDs = users.stream().flatMap(user -> user.getOrderList().orElse(Collections.emptyList())
                .stream())
                .map(Order::getOrderID).toList();
        System.out.println(orderIDs);
    }
}