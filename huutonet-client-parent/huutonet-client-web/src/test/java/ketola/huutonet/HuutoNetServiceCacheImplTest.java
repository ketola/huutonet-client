package ketola.huutonet;

import java.util.ArrayList;
import java.util.Date;

import ketola.huutonet.domain.HuutoNetItem;
import ketola.huutonet.service.HuutoNetService;
import ketola.huutonet.service.HuutoNetServiceCacheImpl;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class HuutoNetServiceCacheImplTest
{

    @Test
    public void testFirstCallGeneratesCache()
    {
        HuutoNetServiceCacheImpl service = new HuutoNetServiceCacheImpl();
        Assert.assertNull( service.getCache() );

        HuutoNetService wrappedService = Mockito.mock( HuutoNetService.class );
        Mockito.when( wrappedService.fetchHuutoNetItems() ).thenReturn( new ArrayList<HuutoNetItem>() );
        service.setWrappedHuutoNetService( wrappedService );

        service.fetchHuutoNetItems();

        Assert.assertNotNull( service.getCache() );
    }

    @Test
    public void testOnSecondCallServiceReturnsFromCache()
    {
        HuutoNetServiceCacheImpl service = new HuutoNetServiceCacheImpl();

        HuutoNetService wrappedService = Mockito.mock( HuutoNetService.class );
        Mockito.when( wrappedService.fetchHuutoNetItems() ).thenReturn( new ArrayList<HuutoNetItem>() );
        service.setWrappedHuutoNetService( wrappedService );

        service.fetchHuutoNetItems();
        service.fetchHuutoNetItems();

        Mockito.verify( wrappedService, Mockito.times( 1 ) ).fetchHuutoNetItems();
    }

    @Test
    public void testCacheExpiresAfter5minutes()
    {
        HuutoNetServiceCacheImpl service = new HuutoNetServiceCacheImpl()
        {
            @Override
            protected Date newCacheTimestamp()
            {
                Date date = new Date();
                date = DateUtils.addMinutes( new Date(), -6 );
                return date;
            }
        };

        HuutoNetService wrappedService = Mockito.mock( HuutoNetService.class );
        Mockito.when( wrappedService.fetchHuutoNetItems() ).thenReturn( new ArrayList<HuutoNetItem>() );
        service.setWrappedHuutoNetService( wrappedService );

        // 1st call populates the cache and sets the timestamp
        service.fetchHuutoNetItems();

        // on 2nd call the cache should be expired which causes a new call to the wrapped service
        service.fetchHuutoNetItems();

        Mockito.verify( wrappedService, Mockito.times( 2 ) ).fetchHuutoNetItems();
    }
}
