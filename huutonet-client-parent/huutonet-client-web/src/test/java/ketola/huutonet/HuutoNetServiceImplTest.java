package ketola.huutonet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;
import ketola.huutonet.service.HuutoNetServiceImpl;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

public class HuutoNetServiceImplTest
{

    @Test
    public void testFetchHuutoNetItems()
        throws ParseException
    {
        HuutoNetServiceImpl service = HuutoNetServiceImplWithMockAbderaClient();
        List<HuutoNetItem> items = service.fetchHuutoNetItems();

        assertEquals( 2, items.size() );

        HuutoNetItem item1 = items.get( 0 );

        assertEquals( "Tyttöjen mekot ja hameet 86-104 cm", item1.getCategory() );
        assertEquals( DateUtils.parseDate( "2013-12-08 12:54:55", new String[] { "yyyy-MM-dd HH:mm:ss" } ),
                      item1.getCloseDate() );
        assertEquals( "Siistissä kunnossa oleva Metsolan froteinen mekko koko 86/92cm. Mekko on ehjä ja tahraton ja frotee on pehme��. Pohjav�ri on tummanruskea.",
                      item1.getDescription() );
        assertEquals( "http://www.huuto.net/kohteet/metsola-mekko-koko-86_92cm/291236849?ref=api",
                      item1.getHuutoNetUrl() );
        assertEquals( "http://kuvat2.huuto.net/0/3e/18453ee21f2d9ec514ea6b5d89cbc-m.jpg", item1.getImageNormalUrl() );
        assertEquals( "http://kuvat2.huuto.net/0/3e/18453ee21f2d9ec514ea6b5d89cbc-orig.jpg",
                      item1.getImageOriginalUrl() );
        assertEquals( "http://kuvat2.huuto.net/0/3e/18453ee21f2d9ec514ea6b5d89cbc-s.jpg", item1.getImageThumbnailUrl() );
        assertEquals( new BigDecimal( "12.00" ), item1.getPriceCurrent() );
        assertEquals( new BigDecimal( "10.00" ), item1.getPriceStart() );
        assertEquals( "Metsola mekko koko 86/92cm", item1.getTitle() );
    }

    private HuutoNetServiceImpl HuutoNetServiceImplWithMockAbderaClient()
    {
        Abdera abdera = Abdera.getInstance();

        final ClientResponse itemsResponse = mock( ClientResponse.class );
        when( itemsResponse.getDocument() )
            .thenReturn( abdera.getParser().parse( this.getClass().getResourceAsStream( "/satukirppu-items.xml" ) ) );

        final ClientResponse items1Response = mock( ClientResponse.class );
        when( items1Response.getDocument() )
            .thenReturn( abdera.getParser().parse( this.getClass()
                                                       .getResourceAsStream( "/satukirppu-item-291236849.xml" ) ) );
        final ClientResponse items2Response = mock( ClientResponse.class );
        when( items2Response.getDocument() )
            .thenReturn( abdera.getParser().parse( this.getClass()
                                                       .getResourceAsStream( "/satukirppu-item-291247952.xml" ) ) );

        return new HuutoNetServiceImpl()
        {
            @Override
            protected AbderaClient abderaClient()
            {
                return new AbderaClient()
                {
                    @Override
                    public ClientResponse get( String uri )
                    {
                        if ( uri.equals( HuutoNetServiceImpl.URL_ITEMS ) )
                        {
                            return itemsResponse;
                        }

                        if ( uri.contains( "291236849" ) )
                        {
                            return items1Response;
                        }

                        if ( uri.contains( "291247952" ) )
                        {
                            return items2Response;
                        }
                        return null;
                    }
                };
            }
        };
    }
}
