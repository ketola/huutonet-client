package ketola.huutonet.service;

import java.util.Date;
import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;

public class HuutoNetServiceCacheImpl
    implements HuutoNetService
{
    private static final long FIVE_MINUTES = 1000 * 60 * 5;

    private List<HuutoNetItem> cache;

    private Date cacheTimestamp;

    private HuutoNetService wrappedHuutoNetService;

    public HuutoNetServiceCacheImpl()
    {

    }

    public void init()
    {

    }

    private void refreshCache()
    {
        this.cache = wrappedHuutoNetService.fetchHuutoNetItems();
        this.cacheTimestamp = new Date();
    }

    @Override
    public List<HuutoNetItem> fetchHuutoNetItems()
    {
        // cache updating timer stops working at some point on openshift
        // this is here to restart it if cache has not been refreshed
        if ( cacheTimestamp == null || getCacheAge() > FIVE_MINUTES )
        {
            refreshCache();
        }

        return cache;
    }

    private long getCacheAge()
    {
        return new Date().getTime() - cacheTimestamp.getTime();
    }

    public void setWrappedHuutoNetService( HuutoNetService wrappedHuutoNetService )
    {
        this.wrappedHuutoNetService = wrappedHuutoNetService;
    }

    public List<HuutoNetItem> getCache()
    {
        return cache;
    }
}
