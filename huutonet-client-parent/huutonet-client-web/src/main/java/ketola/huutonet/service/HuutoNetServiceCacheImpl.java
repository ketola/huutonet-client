package ketola.huutonet.service;

import java.util.Date;
import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;

public class HuutoNetServiceCacheImpl
    implements HuutoNetService
{
    private static final long FIVE_MINUTES = 300000;

    private Date queryTimeStamp;

    private List<HuutoNetItem> cache;

    private HuutoNetService wrappedHuutoNetService;

    @Override
    public List<HuutoNetItem> fetchHuutoNetItems()
    {
        if ( cache == null || isCacheExpired() )
        {
            cache = wrappedHuutoNetService.fetchHuutoNetItems();
            queryTimeStamp = newCacheTimestamp();
        }
        return cache;
    }

    private boolean isCacheExpired()
    {
        if ( queryTimeStamp == null )
            return true;

        Date now = new Date();

        long cacheAge = now.getTime() - queryTimeStamp.getTime();

        return cacheAge > FIVE_MINUTES;
    }

    protected Date newCacheTimestamp()
    {
        return new Date();
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
