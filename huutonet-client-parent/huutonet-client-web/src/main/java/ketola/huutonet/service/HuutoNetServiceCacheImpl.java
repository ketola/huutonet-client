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

    public HuutoNetServiceCacheImpl()
    {

    }

    public void init()
    {
        createTimer();
    }

    private Timer createTimer()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask()
        {

            @Override
            public void run()
            {
                cache = wrappedHuutoNetService.fetchHuutoNetItems();
            }
        }, 1000, FIVE_MINUTES );
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
}
