import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ReportGenerator {
    static class TaskRunnable implements Runnable
    {
        private final String fileName;
        private double totalCost;
        private int totalAmount;
        private int totalDiscountSum;
        private int totalLines;
        private Product mostExpensiveProduct;
        private double highestCostAfterDiscount;

        public TaskRunnable(String fileName)
        {
            this.fileName = fileName;
            this.totalCost = 0;
            this.totalAmount = 0;
            this.totalDiscountSum = 0;
            this.totalLines = 0;
            this.highestCostAfterDiscount = 0;
            this.mostExpensiveProduct = null;
        }

        @Override
        public void run()
        {
            try
            {
                InputStream in = ReportGenerator.class.getClassLoader().getResourceAsStream(fileName);
                if (in == null)
                {
                    System.out.println("File not found: " + fileName);
                    return;
                }

                List<String> lines = new BufferedReader(new InputStreamReader(in)).lines().toList();
                for (String line : lines)
                {
                    String[] parts = line.split(",");
                    int productId = Integer.parseInt(parts[0].trim());
                    int amount = Integer.parseInt(parts[1].trim());
                    int discount = Integer.parseInt(parts[2].trim());
                    Product product = null;
                    for (Product p : ReportGenerator.productCatalog)
                    {
                        if (p != null && p.getProductID() == productId)
                        {
                            product = p;
                            break;
                        }
                    }

                    if (product != null)
                    {
                        double originalPrice = product.getPrice();
                        double discountedPrice = (originalPrice - discount) * amount;
                        totalCost += discountedPrice;
                        totalAmount += amount;
                        totalDiscountSum += discount;
                        totalLines++;
                        if (discountedPrice > highestCostAfterDiscount)
                        {
                            highestCostAfterDiscount = discountedPrice;
                            mostExpensiveProduct = product;
                        }
                    }
                }
            } catch (Exception e)
            {
                System.out.println("Error processing file: " + fileName);
                e.printStackTrace();
            }
        }

        public void makeReport()
        {
            System.out.println("📄 Report for: " + fileName);
            System.out.printf("Total Cost: %.2f%n", totalCost);
            System.out.println("Total Items Bought: " + totalAmount);
            double avgDiscount = totalLines > 0 ? (double) totalDiscountSum / totalLines : 0.0;
            System.out.printf("Average Discount: %.2f%n", avgDiscount);
            if (mostExpensiveProduct != null)
            {
                System.out.printf("Most Expensive Purchase After Discount: %s (%.2f)%n",
                        mostExpensiveProduct.getProductName(), highestCostAfterDiscount);
            }
            System.out.println("---------------------------------------------------");
        }
    }

    static class Product
    {
        private int productID;
        private String productName;
        private double price;

        public Product(int productID, String productName, double price)
        {
            this.productID = productID;
            this.productName = productName;
            this.price = price;
        }

        public int getProductID()
        {
            return productID;
        }

        public String getProductName()
        {
            return productName;
        }

        public double getPrice()
        {
            return price;
        }
    }

    private static final String[] ORDER_FILES = {
            "2021_order_details.txt",
            "2022_order_details.txt",
            "2023_order_details.txt",
            "2024_order_details.txt"
    };

    static Product[] productCatalog = new Product[100];

    public static void loadProducts()
    {
        try
        {
            InputStream in = ReportGenerator.class.getClassLoader().getResourceAsStream("Products.txt");
            if (in == null)
            {
                System.out.println("Products.txt not found in resources!");
                return;
            }
            List<String> lines = new BufferedReader(new InputStreamReader(in)).lines().toList();

            int index = 0;
            for (String line : lines)
            {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                productCatalog[index++] = new Product(id, name, price);
            }
        } catch (Exception e)
        {
            System.out.println("Error loading products.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        loadProducts();
        TaskRunnable[] tasks = new TaskRunnable[ORDER_FILES.length];
        Thread[] threads = new Thread[ORDER_FILES.length];
        for (int i = 0; i < ORDER_FILES.length; i++)
        {
            tasks[i] = new TaskRunnable(ORDER_FILES[i]);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }
        for (Thread thread : threads)
        {
            thread.join();
        }
        for (TaskRunnable task : tasks)
        {
            task.makeReport();
        }
    }
}
