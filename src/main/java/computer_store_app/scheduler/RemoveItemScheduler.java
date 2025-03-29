package computer_store_app.scheduler;

import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class RemoveItemScheduler {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public RemoveItemScheduler(ItemService itemService, ItemRepository itemRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

//     Scheduled Job = Cron Job
//    @Scheduled(fixedDelay = 10000)
//    public void sayHellowEvery10Seconds() {
//
//        System.out.println(LocalDateTime.now() + " Hello World!!!!");
//    }

    @Scheduled(fixedDelay = 1800000)
//    @Scheduled(fixedDelay = 6000) // 6 sec(testing)
    public void removeItemAfterOneMonthNotSold() {

        List<Item> items = itemService.getAllItems();

        if (items.isEmpty()) {
            log.info("There are no items in the store.");
            return;
        }

        for (Item item : items) {
            if (item.isSold()){
                continue;
            }
            LocalDateTime itemAddedOnDate = item.getAddedOn();
            LocalDateTime oneMonthPassed = itemAddedOnDate.plusMonths(1);
//            LocalDateTime oneMonthPassed = itemAddedOnDate.plusSeconds(30); // testing

            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(oneMonthPassed)) {
                log.info("Removing item with id [%s] after one month".formatted(item.getId()));
                itemRepository.deleteById(item.getId());
            }
        }

        System.out.println("are de");
    }
}
