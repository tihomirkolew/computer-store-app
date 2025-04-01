package computer_store_app.scheduler;

import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.item.service.ItemService;
import org.hibernate.Remove;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerUnitTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RemoveItemScheduler removeItemScheduler;


    @Test
    void givenSoldItem_whenSchedulerRuns_thenItemIsSkipped() {
        // Given: A sold item
        Item soldItem = Item.builder()
                .id(UUID.randomUUID())
                .addedOn(LocalDateTime.now().minusMonths(2)) // Older than one month
                .archived(false)
                .sold(true) // Sold item
                .build();
        when(itemService.getAllItems()).thenReturn(List.of(soldItem));

        // When
        removeItemScheduler.archiveItemAfterOneMonthNotSold();

        // Then
        verify(itemService, times(1)).getAllItems(); // Ensure items are retrieved
        verify(itemRepository, never()).save(any()); // Sold items should not be saved
    }

    @Test
    void givenUnarchivedItemOlderThanOneMonth_whenSchedulerRuns_thenItemIsArchived() {

        // Given
        UUID itemId = UUID.randomUUID();
        Item item = Item.builder()
                .id(itemId)
                .addedOn(LocalDateTime.now().minusMonths(2))
                .archived(false)
                .sold(false)
                .build();
        when(itemService.getAllItems()).thenReturn(List.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        // When
        removeItemScheduler.archiveItemAfterOneMonthNotSold();

        // Then
        assertTrue(item.isArchived());
        verify(itemService, times(1)).getAllItems();
        verify(itemRepository, times(1)).save(item);
    }

}
