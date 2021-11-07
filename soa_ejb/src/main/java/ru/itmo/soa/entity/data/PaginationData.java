package ru.itmo.soa.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.itmo.soa.entity.HumanBeing;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class PaginationData {

    private final int pageSize;
    private final int pageIndex;
    private final long totalItems;
    private final List<HumanBeing> list;
    public PaginationData() {
        pageSize = 0;
        pageIndex = 0;
        totalItems = 0;
        list = new ArrayList<>();
    }
}