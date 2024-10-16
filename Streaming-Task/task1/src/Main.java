import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(new Product("Laptop", "Electronics", 999.99),
                new Product("Smartphone", "Electronics", 799.99),
                new Product("T-shirt", "Clothing", 19.99),
                new Product("Jeans", "Clothing", 49.9),
                new Product("Bananas", "Grocery", 0.99),
                new Product("Bread", "Grocery", 2.49),
                new Product("Book", "Books", 14.99),
                new Product("Notebook", "Books", 6));

        Map<String, Double> totalPricesByCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.summingDouble(Product::getPrice)));
        totalPricesByCategory.forEach((category,totalPrice)->{
            System.out.println("Category is " + category + "total price is "+ String.format("%.2f", totalPrice));
        });
    }
}