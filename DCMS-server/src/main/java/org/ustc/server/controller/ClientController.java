package org.ustc.server.controller;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ustc.common.pojo.Client;
import org.ustc.common.pojo.Report;
import org.ustc.server.service.RecordService;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ClientController
 * Package: org.ustc.server.controller
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/23
 * @Version 1.0
 */

@Slf4j
@Controller
public class ClientController {

    @Resource
    private RecordService recordService;

    @RequestMapping(value = "/clients")
    @ResponseBody
    public String getClientsList() {
        return JSON.toJSONString(recordService.clients.getClients().values());
    }

    @PostMapping("/saveClient")
    public void saveClient(@RequestBody Client client){
        client.setOnline(true);
        recordService.clients.getClients().put(client.getName(), client);
        //TODO: 开辟新线程运行client代码
    }

    @PostMapping("/editClient")
    public void editClient(@RequestBody Client client){
        String name = client.getName();
        recordService.clients.getClients().get(name).setOnline(false);
    }

    @PostMapping("/deleteClient")
    public void deleteClient(@RequestBody Client client){
        String name = client.getName();
        recordService.clients.getClients().remove(name);
        recordService.deleteRecordByName(name);
    }

    @RequestMapping("/historyClientsReport")
    @ResponseBody
    public String getHistoryClientReport() {
        List<Report> reports = recordService.findAll();
        return JSON.toJSONString(reports);
    }

    @RequestMapping("/realTimeClientsReport")
    @ResponseBody
    public String getRealTimeClientsReport() {
        List<Report> reports = new ArrayList<>();
        for (String key: recordService.clients.getClients().keySet()) {
            if (recordService.clients.getClients().get(key).isOnline())
                reports.addAll(recordService.getRecordByName(recordService.clients.getClients().get(key).getName()));
        }
        return JSON.toJSONString(reports);
    }
}
