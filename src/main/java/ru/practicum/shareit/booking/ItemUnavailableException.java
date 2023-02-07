package ru.practicum.shareit.booking;

public class ItemUnavailableException extends RuntimeException {
    private final int itemId;

    public ItemUnavailableException(int itemId) {
        super("Item " + itemId + " is unavailable");
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}
