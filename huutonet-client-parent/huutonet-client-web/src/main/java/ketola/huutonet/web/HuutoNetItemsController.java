package ketola.huutonet.web;

import ketola.huutonet.service.HuutoNetService;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HuutoNetItemsController
{
    @Autowired
    private HuutoNetService huutoNetService;

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    @ResponseBody
    public String fetchHuutoNetItems( @RequestParam(required = false)
    String callback )
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            String json = mapper.writeValueAsString( huutoNetService.fetchHuutoNetItems() );

            if ( StringUtils.isNotEmpty( callback ) )
            {
                json = String.format( callback + "(%s);", json );
            }

            return json;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

}
