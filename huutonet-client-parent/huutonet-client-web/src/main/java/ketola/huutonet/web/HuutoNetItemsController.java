package ketola.huutonet.web;

import java.util.List;

import ketola.huutonet.domain.HuutoNetItem;
import ketola.huutonet.service.HuutoNetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HuutoNetItemsController
{
    @Autowired
    private HuutoNetService huutoNetService;

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    @ResponseBody
    public List<HuutoNetItem> fetchHuutoNetItems()
    {
        return huutoNetService.fetchHuutoNetItems();
    }
}
