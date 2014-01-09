package ketola.huutonet.service;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ketola.huutonet.domain.HuutoNetItem;

public class HuutoNetServiceCacheImpl
    implements HuutoNetService
{
    private static final long FIVE_MINUTES = 1000 * 60 * 5;

    private List<HuutoNetItem> cache;

    private Date cacheTimestamp;

    private HuutoNetService wrappedHuutoNetService;

    private Timer timer;

    public HuutoNetServiceCacheImpl()
    {

    }

    public void init()
    {
        refreshCacheAndStartTimer();
    }

    private void refreshCacheAndStartTimer()
    {
        this.cache = wrappedHuutoNetService.fetchHuutoNetItems();
        this.cacheTimestamp = new Date();
        this.timer = createTimer();
    }

    private Timer createTimer()
    {
        Timer timer = new Timer();
        timer.schedule( new UpdateTask(), FIVE_MINUTES );
        return timer;
    }

    @Override
    public List<HuutoNetItem> fetchHuutoNetItems()
    {
        // cache updating timer stops working at some point on openshift
        // this is here to restart it if cache has not been refreshed
        if ( getCacheAge() > FIVE_MINUTES )
        {
            refreshCacheAndStartTimer();
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

    private class UpdateTask
        extends TimerTask
    {

        @Override
        public void run()
        {
            // the timer seems to stop working after a while on openshift, 
            // i changed the implementation to create a new timer (new thread) each time to work around
            HuutoNetServiceCacheImpl.this.timer.cancel();
            HuutoNetServiceCacheImpl.this.cache = wrappedHuutoNetService.fetchHuutoNetItems();
            HuutoNetServiceCacheImpl.this.timer = createTimer();
        }
    }
}
