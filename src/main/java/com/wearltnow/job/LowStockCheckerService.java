package com.wearltnow.job;
import com.wearltnow.model.Notification;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductInventory;
import com.wearltnow.model.User;
import com.wearltnow.repository.NotificationRepository;
import com.wearltnow.repository.ProductInventoryRepository;
import com.wearltnow.service.NotificationService;
import com.wearltnow.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LowStockCheckerService {

    ProductInventoryRepository inventoryRepository;
    NotificationService notificationService;
    UserService userService;
    NotificationRepository notificationRepository;


    @Scheduled(cron = "0 */1 * * * *")
    public void checkLowStock() {
        List<ProductInventory> lowStockInventories = inventoryRepository.findByQuantityLessThan(10);
        User admin = userService.findAdmin();
        if (!lowStockInventories.isEmpty()) {
            for (ProductInventory inventory : lowStockInventories) {
                Product product = inventory.getProduct();
                String message = "Sản phẩm có tồn kho thấp: " + product.getName() + " với ID " + product.getProductId();
                List<Notification> existingNotifications = notificationRepository.findByMessage(message);
                if (existingNotifications.isEmpty()) {
                    notificationService.sendLowStockNotifyToAdmin(admin.getUserId(),message);
                }
            }
        }
    }
}
