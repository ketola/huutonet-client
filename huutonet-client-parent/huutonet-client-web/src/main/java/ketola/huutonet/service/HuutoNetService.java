package ketola.huutonet.service;

import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;

public interface HuutoNetService
{
    List<HuutoNetItem> fetchHuutoNetItems();

    List<String> fetchItemIds();

    HuutoNetItem fetchItem( String id );
}
