package ketola.huutonet.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ketola.huutonet.domain.HuutoNetItem;

public class HuutoNetServiceCacheImpl
    implements HuutoNetService
{
    private static final long FIVE_MINUTES = 1000 * 60 * 5;

    private List<HuutoNetItem> cache;

    private HuutoNetService wrappedHuutoNetService;

    private Timer timer;

    public HuutoNetServiceCacheImpl()
    {

    }

    public void init()
    {
        // populate cache and schedule an update timer
        this.cache = wrappedHuutoNetService.fetchHuutoNetItems();
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
        return cache;
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
